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
package de.cuioss.portal.authentication.mock;

import static de.cuioss.portal.authentication.mock.MockAuthenticationLogMessages.*;
import static java.util.Objects.requireNonNull;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.*;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo.BaseAuthenticatedUserInfoBuilder;
import de.cuioss.portal.authentication.model.UserStore;
import de.cuioss.portal.configuration.types.ConfigAsList;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultObject;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy implementation of the {@link AuthenticationFacade} interface.
 * <p>
 * In order to control the authentication status you can provide a configuration
 * parameter with the key {@value #CONFIGURATION_KEY_AUTHENTICATED} and the
 * value <code>true</code> for being initially authenticated (default) or
 * <code>false</code> for not being authenticated.
 * </p>
 * <p>
 * In order to control the default name for the authenticated user you can
 * provide a configuration parameter with the key
 * {@value #CONFIGURATION_KEY_USER_NAME}. If it is not set it will default to
 * "user"
 * </p>
 * <p>
 * In order to control the applied roles for the authenticated user you can
 * provide a configuration parameter with the key
 * {@value #CONFIGURATION_KEY_ROLES}.
 * </p>
 *
 * @author Oliver Wolff
 */
@PortalAuthenticationFacade
@Named
@ApplicationScoped
@ToString
public class MockAuthenticationFacade implements FormBasedAuthenticationFacade {

    private static final String USER = "user";

    private static final AuthenticatedUserInfo NOT_LOGGED_IN = new BaseAuthenticatedUserInfo(false, null, null, null,
            null);

    private static final String BASE_KEY = "portal.MockAuthenticationFacade.";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_AUTHENTICATED = BASE_KEY + "authenticated";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_USER_NAME = BASE_KEY + "username";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_SYSTEM = BASE_KEY + "system";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_GROUPS = BASE_KEY + "groups";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_ROLES = BASE_KEY + "roles";

    /**
     *
     */
    public static final String CONFIGURATION_KEY_CONTEXT_MAP = BASE_KEY + "contextMap";

    private static final String USER_INFO_KEY = "userInfoKey";

    private static final String USER_INFO_LOGOUT_KEY = "userInfoLogoutKey";

    private List<UserStore> availableUserStores;

    private static final CuiLogger LOGGER = new CuiLogger(MockAuthenticationFacade.class);

    @ConfigProperty(name = CONFIGURATION_KEY_AUTHENTICATED, defaultValue = "true")
    @Inject
    private Provider<Boolean> defaultLoggedIn;

    @ConfigProperty(name = CONFIGURATION_KEY_USER_NAME, defaultValue = USER)
    @Inject
    private Provider<String> defaultUserName;

    @ConfigProperty(name = CONFIGURATION_KEY_SYSTEM, defaultValue = "mock")
    @Inject
    private String defaultSystem;

    @ConfigAsList(name = CONFIGURATION_KEY_GROUPS)
    @Inject
    private Provider<List<String>> defaultUserGroups;

    @ConfigAsList(name = CONFIGURATION_KEY_ROLES)
    @Inject
    private Provider<List<String>> defaultUserRoles;

    @ConfigAsList(name = CONFIGURATION_KEY_CONTEXT_MAP)
    @Inject
    private Provider<List<String>> defaultContextMapEntries;

    @Getter
    @Setter
    private AuthenticationSource authenticationSource = AuthenticationSource.MOCK;

    /**
     * The dummy implementation provides a successful login in case the identifier
     * and password are equal.
     */
    @Override
    public ResultObject<AuthenticatedUserInfo> login(final HttpServletRequest servletRequest,
            final LoginCredentials loginCredentials) {
        requireNonNull(loginCredentials);
        requireNonNull(servletRequest);
        if (loginCredentials.isComplete()
                && loginCredentials.getUsername().equalsIgnoreCase(loginCredentials.getPassword())) {
            var oldSession = servletRequest.getSession();
            if (null != oldSession) {
                oldSession.invalidate();
            }
            AuthenticatedUserInfo currentAuthenticationUserInfo = createDefaultUserInfoBuilder()
                    .identifier(loginCredentials.getUsername()).qualifiedIdentifier(loginCredentials.getUsername())
                    .displayName(loginCredentials.getUsername()).build();
            servletRequest.getSession(true).setAttribute(USER_INFO_KEY, currentAuthenticationUserInfo);
            LOGGER.info(INFO.USER_LOGIN.format(loginCredentials.getUsername()));
            return AuthenticationResults.validResult(currentAuthenticationUserInfo);
        }
        LOGGER.warn(WARN.INVALID_LOGIN.format(loginCredentials.getUsername()));
        return AuthenticationResults.invalidResult(
                "This is a mocked login, enter username with the same password, saying admin/admin, user/user,..",
                "testuser", null);
    }

    private BaseAuthenticatedUserInfoBuilder createDefaultUserInfoBuilder() {
        var roles = defaultUserRoles.get().stream().map(String::trim).toList();
        var groups = defaultUserGroups.get().stream().map(String::trim).toList();
        LOGGER.debug(DEBUG.DEFAULT_USER_INFO.format(roles, groups));
        var builder = BaseAuthenticatedUserInfo.builder().authenticated(true)
                .groups(groups)
                .roles(roles)
                .system(defaultSystem);
        for (String entry : defaultContextMapEntries.get()) {
            builder.contextMapElement(entry.split(":")[0].trim(), entry.split(":")[1].trim());
        }
        return builder;
    }

    @Override
    public boolean logout(final HttpServletRequest servletRequest) {
        var oldSession = servletRequest.getSession();
        var userInfo = (AuthenticatedUserInfo) oldSession.getAttribute(USER_INFO_KEY);
        if (null != oldSession) {
            oldSession.invalidate();
        }
        var newSession = servletRequest.getSession(true);
        newSession.setAttribute(USER_INFO_KEY, NOT_LOGGED_IN);
        newSession.setAttribute(USER_INFO_LOGOUT_KEY, USER_INFO_LOGOUT_KEY);
        if (userInfo != null && userInfo.isAuthenticated()) {
            LOGGER.info(INFO.USER_LOGOUT.format(userInfo.getDisplayName()));
        }
        return true;
    }

    @Override
    public AuthenticatedUserInfo retrieveCurrentAuthenticationContext(final HttpServletRequest servletRequest) {
        var userInfo = (AuthenticatedUserInfo) servletRequest.getSession().getAttribute(USER_INFO_KEY);
        if (null == userInfo) {
            if (defaultLoggedIn.get()) {
                var userName = defaultUserName.get();
                userInfo = createDefaultUserInfoBuilder().identifier(userName).qualifiedIdentifier(userName)
                        .displayName(userName).build();
            } else {
                userInfo = NOT_LOGGED_IN;
            }
        }
        if (userInfo.isAuthenticated()) {
            LOGGER.info(INFO.RETRIEVED_CONTEXT.format(userInfo.getDisplayName()));
        }
        return userInfo;
    }

    @Override
    public List<UserStore> getAvailableUserStores() {
        if (null == availableUserStores) {
            availableUserStores = new ArrayList<>();
            availableUserStores.add(new UserStore("LOCAL", "Local"));
            availableUserStores.add(new UserStore("SOMELDAP", "SomeLdap"));
        }
        return availableUserStores;
    }

}
