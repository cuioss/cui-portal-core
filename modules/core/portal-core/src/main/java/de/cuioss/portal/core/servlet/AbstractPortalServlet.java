/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Provides a minimal layer for modeling {@link Servlet}s that can be enabled by
 * role or configuration. Currently only get-requests are supported
 * ->{@link #executeDoGet(HttpServletRequest, HttpServletResponse)}. If you need
 * to implement a different method you can call
 * {@link #checkAccess(HttpServletResponse)} in order to participate in the
 * checks The algorithm:
 * <ul>
 * <li>Check enabled: {@link #isEnabled()}</li>
 * <li>Check authentication status: {@link #isLoggedInUserRequired()}</li>
 * <li>Check required roles: {@link #getRequiredRoles()}</li>
 * </ul>
 * If all previous checks are passed the method
 * {@link AbstractPortalServlet#executeDoGet(HttpServletRequest, HttpServletResponse)}
 * will be called.
 *
 * @author Oliver Wolff
 *
 */
public abstract class AbstractPortalServlet extends HttpServlet {

    /** Portal-523: Could not process Request, due to {}. */
    public static final String PORTAL_523 = "Portal-523: Could not process Request, due to {}";

    private static final String NOT_LOGGED_IN = "Portal-523: Could not process Request, because the user must be logged in for this request";
    private static final String USER = "Portal-523: Could not process Request, because of the condition '{}' is not met for user '{}'";

    private static final long serialVersionUID = 5418492528395532112L;

    private final CuiLogger log = new CuiLogger(getClass());

    @Inject
    @PortalUser
    Provider<AuthenticatedUserInfo> userInfoProvider;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAccess(resp)) {
            return;
        }
        try {
            executeDoGet(req, resp);
        } catch (RuntimeException | IOException e) {
            log.error(e, PORTAL_523, "Runtime Exception");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method that checks whether the payload method is allowed to be called.
     * It checks the parameter as defined at class-level. It modifies the
     * {@link HttpServletResponse} with the corresponding Http-Codes and logs the
     * findings on the appropriate level.
     *
     * @param resp must not be null
     * @return boolean indicating whether all checks are passed or not.
     */
    public boolean checkAccess(HttpServletResponse resp) {
        log.trace("Checking call preconditions");
        if (!isEnabled()) {
            log.debug(PORTAL_523, "Disabled by configuration");
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }
        var user = getUserInfo();

        if (isLoggedInUserRequired() && !user.isAuthenticated()) {
            log.warn(NOT_LOGGED_IN);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        var requiredRoles = getRequiredRoles();
        if (!requiredRoles.isEmpty() && !user.getRoles().containsAll(requiredRoles)) {
            log.warn(USER, "User should provide the roles " + requiredRoles, user);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        log.trace("All preconditions are ok, generating payload");
        return true;
    }

    /**
     * The actual payload method for the concrete servlet. If it is called all
     * checks are already done and the implementor can focus on the business logic.
     * All {@link RuntimeException} are translated to an
     * {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}
     *
     * @param request  to be passed
     * @param response to be passed
     * @throws IOException in case an IO-Error occurs
     */
    public abstract void executeDoGet(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * @return boolean indicating whether the Servlet-Request is enabled at all.
     *         Usually this is controlled by a configured-property. If this method
     *         returns false it will be translated to
     *         {@link HttpServletResponse#SC_SERVICE_UNAVAILABLE}. Defaults to
     *         {@code true}
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return boolean indicating whether the servlet needs a logged in user. If it
     *         is {@code true} the system will check whether the current user is
     *         logged in. If the user is not logged in it will return
     *         {@link HttpServletResponse#SC_FORBIDDEN}. Defaults to {@code false}
     */
    public boolean isLoggedInUserRequired() {
        return false;
    }

    /**
     * @return a {@link Collection} of roles that are <em>all</em> required in order
     *         to render the content. The default implementation will return an
     *         empty {@link Collection}. If this method provides at least one role
     *         that user does not provide the servlet will return
     *         {@link HttpServletResponse#SC_FORBIDDEN}
     */
    public Collection<String> getRequiredRoles() {
        return Collections.emptyList();
    }

    /**
     * @return the {@link AuthenticatedUserInfo} resolved against the injected
     *         {@link PortalUser}
     */
    protected AuthenticatedUserInfo getUserInfo() {
        return userInfoProvider.get();
    }
}
