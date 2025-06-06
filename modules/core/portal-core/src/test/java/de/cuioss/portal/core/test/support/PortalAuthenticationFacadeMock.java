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
package de.cuioss.portal.core.test.support;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.AuthenticationResults;
import de.cuioss.portal.authentication.facade.AuthenticationSource;
import de.cuioss.portal.authentication.facade.FormBasedAuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.UserStore;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultObject;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static de.cuioss.portal.authentication.facade.AuthenticationResults.invalidResultKey;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@Named
@PortalAuthenticationFacade
@ToString
public class PortalAuthenticationFacadeMock implements FormBasedAuthenticationFacade {

    /**
     * username / password for admin.
     */
    public static final String ADMIN = "admin";

    /**
     * username / password for a standard user.
     */
    public static final String USER = "user";

    /**
     * "LOCAL", "Local".
     */
    public static final UserStore DEFAULT_USER_STORE = new UserStore("LOCAL", "Local");

    /**
     * "SOMELDAP", "SomeLdap".
     */
    public static final UserStore SOME_LDAP_USER_STORE = new UserStore("SOMELDAP", "SomeLdap");

    /**
     * "SOME_OTHER_LDAP", "SomeOtherLdap".
     */
    public static final UserStore SOME_OTHER_LDAP_USER_STORE = new UserStore("SOME_OTHER_LDAP", "SomeOtherLdap");

    @Getter
    @Setter
    private AuthenticationSource authenticationSource = AuthenticationSource.MOCK;

    private static final AuthenticatedUserInfo NOT_LOGGED_IN = new BaseAuthenticatedUserInfo(false, null, null, null,
            null);

    @Setter // setter only for test proposal available
    @Getter
    private List<UserStore> availableUserStores = immutableList(DEFAULT_USER_STORE, SOME_OTHER_LDAP_USER_STORE,
            SOME_LDAP_USER_STORE);

    @Setter
    private AuthenticatedUserInfo current;

    /**
     * Logs in the default user.
     */
    @PostConstruct
    public void init() {
        current = new BaseAuthenticatedUserInfo(true, USER, USER, USER, null);
    }

    @Override
    public ResultObject<AuthenticatedUserInfo> login(final HttpServletRequest request,
            final LoginCredentials loginCredentials) {
        requireNonNull(loginCredentials);
        if (loginCredentials.isComplete()
                && loginCredentials.getUsername().equalsIgnoreCase(loginCredentials.getPassword())) {
            return AuthenticationResults.validResult(new BaseAuthenticatedUserInfo(true, loginCredentials.getUsername(),
                    loginCredentials.getUsername(), loginCredentials.getUsername(), null));
        }
        return invalidResultKey(AuthenticationResults.KEY_INVALID_CREDENTIALS, loginCredentials.getUsername(), null);
    }

    @Override
    public boolean logout(final HttpServletRequest servletRequest) {
        current = NOT_LOGGED_IN;
        return true;
    }

    @Override
    public AuthenticatedUserInfo retrieveCurrentAuthenticationContext(final HttpServletRequest servletRequest) {
        return current;
    }

    /**
     * Mimics login for admin user.
     */
    public void doLogin() {
        login(null, createLoginCredentials(ADMIN));
    }

    /**
     * Asserts whether the current user is authenticated or not
     *
     * @param authenticated
     */
    public void assertAuthenticated(final boolean authenticated) {
        assertEquals(authenticated, current.isAuthenticated());
    }

    /**
     * Creates a {@link LoginCredentials} with a given Username / password.
     *
     * @param username
     * @param password
     *
     * @return created {@link LoginCredentials} with a given Username / password.
     */
    public static LoginCredentials createLoginCredentials(final String username, final String password) {
        final var credentials = new LoginCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        return credentials;
    }

    /**
     * Creates a {@link LoginCredentials} with a given username, and
     * username==password.
     *
     * @param username
     *
     * @return created {@link LoginCredentials} with a given Username / password.
     */
    public static LoginCredentials createLoginCredentials(final String username) {
        return createLoginCredentials(username, username);
    }
}
