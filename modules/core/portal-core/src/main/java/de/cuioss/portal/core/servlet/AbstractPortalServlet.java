/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.servlet;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.inject.Provider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static de.cuioss.portal.core.PortalCoreLogMessages.SERVLET;

/**
 * Base servlet implementation providing common portal functionality including
 * authentication, authorization, and error handling. This abstract class handles
 * common cross-cutting concerns, allowing concrete implementations to focus on
 * business logic.
 *
 * <p><strong>Key features:</strong></p>
 * <ul>
 *   <li>Role-based access control</li>
 *   <li>Authentication verification</li>
 *   <li>Standardized error handling</li>
 *   <li>Configurable servlet enablement</li>
 * </ul>
 *
 * <p><strong>Security features:</strong></p>
 * <ul>
 *   <li>Enforces authentication if required</li>
 *   <li>Validates user roles against required roles</li>
 *   <li>Handles unauthorized access with appropriate HTTP status codes</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * &#64;WebServlet("/api/data")
 * public class DataServlet extends AbstractPortalServlet {
 *     &#64;Override
 *     protected void executeDoGet(HttpServletRequest request, HttpServletResponse response) 
 *             throws IOException {
 *         // Implement business logic here
 *     }
 *
 *     &#64;Override
 *     protected Collection&lt;String&gt; getRequiredRoles() {
 *         return Set.of("ADMIN", "DATA_USER");
 *     }
 * }
 * </pre>
 *
 * <p><strong>HTTP Status Codes:</strong></p>
 * <ul>
 *   <li>{@code 401} - User is not authenticated but authentication is required</li>
 *   <li>{@code 403} - User is authenticated but lacks required roles</li>
 *   <li>{@code 500} - Internal server error during request processing</li>
 *   <li>{@code 503} - Service is currently disabled ({@link #isEnabled()} returns false)</li>
 * </ul>
 *
 * @see #executeDoGet(HttpServletRequest, HttpServletResponse)
 * @see #getRequiredRoles()
 * @see #isEnabled()
 * @since 1.0
 */
public abstract class AbstractPortalServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 5418492528395532112L;

    private static final CuiLogger LOGGER = new CuiLogger(AbstractPortalServlet.class);

    private final Provider<AuthenticatedUserInfo> userInfoProvider;

    protected AbstractPortalServlet(Provider<AuthenticatedUserInfo> userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAccess(resp)) {
            return;
        }
        try {
            executeDoGet(req, resp);
        } catch (RuntimeException | IOException e) {
            LOGGER.error(e, SERVLET.ERROR.REQUEST_PROCESSING_ERROR, e.getMessage());
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
        LOGGER.trace("Checking call preconditions");
        if (!isEnabled()) {
            LOGGER.debug("Could not process Request, disabled by configuration");
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }
        var user = getUserInfo();

        if (isLoggedInUserRequired() && !user.isAuthenticated()) {
            LOGGER.warn(SERVLET.WARN.USER_NOT_LOGGED_IN);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        var requiredRoles = getRequiredRoles();
        if (!requiredRoles.isEmpty() && !new HashSet<>(user.getRoles()).containsAll(requiredRoles)) {
            LOGGER.warn(SERVLET.WARN.USER_MISSING_ROLES, requiredRoles, user);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        LOGGER.trace("All preconditions are ok, generating payload");
        return true;
    }

    /**
     * The actual payload method for the concrete servlet.
     * If it is called, all
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
     * Usually, this is controlled by a configured property.
     * If this method
     * returns false it will be translated to
     * {@link HttpServletResponse#SC_SERVICE_UNAVAILABLE}.
     * Defaults to
     * {@code true}
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return boolean indicating whether the servlet needs a logged-in user.
     * If it is {@code true} the system will check whether the current user is
     * logged in.
     * If the user is not logged in it will return
     * {@link HttpServletResponse#SC_FORBIDDEN}.
     * Defaults to {@code false}
     */
    public boolean isLoggedInUserRequired() {
        return false;
    }

    /**
     * @return a {@link Collection} of roles that are <em>all</em> required
     * to render the content. The default implementation will return an
     * empty {@link Collection}. If this method provides at least one role
     * that user does not provide the servlet will return
     * {@link HttpServletResponse#SC_FORBIDDEN}
     */
    public Collection<String> getRequiredRoles() {
        return Collections.emptyList();
    }

    /**
     * @return the {@link AuthenticatedUserInfo} resolved against the injected {@link AuthenticatedUserInfo}
     */
    protected AuthenticatedUserInfo getUserInfo() {
        return userInfoProvider.get();
    }
}
