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

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultState;
import jakarta.inject.Inject;
import lombok.Getter;
import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration
class MockAuthenticationFacadeTest implements ShouldBeNotNull<MockAuthenticationFacade> {

    private MockHttpServletRequest servletRequest;

    @Inject
    private PortalTestConfiguration configuration;

    @BeforeEach
    void beforeEach() {
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
    }

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private MockAuthenticationFacade underTest;

    @Test
    void shouldAuthenticateAsDefault() {
        final var userInfo = underTest.retrieveCurrentAuthenticationContext(servletRequest);
        assertNotNull(userInfo);
        assertTrue(userInfo.isAuthenticated());
    }

    @Test
    void shouldLogoutAuthenticatedAsDefault() {
        underTest.logout(servletRequest);
        final var userInfo = underTest.retrieveCurrentAuthenticationContext(servletRequest);
        assertFalse(userInfo.isAuthenticated());
    }

    @Test
    void shouldNotAuthenticateAsDefaultAsConfigured() {
        configuration.fireEvent(MockAuthenticationFacade.CONFIGURATION_KEY_AUTHENTICATED, "false");
        final var userInfo = underTest.retrieveCurrentAuthenticationContext(servletRequest);
        assertNotNull(userInfo);
        assertFalse(userInfo.isAuthenticated());
    }

    @Test
    void shouldProvideConfiguredRoles() {
        configuration.fireEvent(MockAuthenticationFacade.CONFIGURATION_KEY_ROLES, "role, role2");
        final var userInfo = underTest.retrieveCurrentAuthenticationContext(servletRequest);
        assertEquals(2, userInfo.getRoles().size());
    }

    @Test
    void shouldHandleNonConfiguredRoles() {
        final var userInfo = underTest.retrieveCurrentAuthenticationContext(servletRequest);
        assertEquals(0, userInfo.getRoles().size());
    }

    @Test
    void shouldProvideAvailableUserStores() {
        assertNotNull(underTest.getAvailableUserStores());
        assertEquals(2, underTest.getAvailableUserStores().size());
    }

    @Test
    void shouldLoginWithUsernameAndPassword() {
        var name = letterStrings(3, 8).next();
        var password = letterStrings(3, 8).next();
        var result = underTest.login(servletRequest,
            LoginCredentials.builder().username(name).password(password).build());
        assertEquals(ResultState.ERROR, result.getState());
        // Incomplete Credentials
        result = underTest.login(servletRequest, LoginCredentials.builder().username(name).build());
        assertEquals(ResultState.ERROR, result.getState());
        result = underTest.login(servletRequest, LoginCredentials.builder().username(name).password(name).build());
        assertEquals(ResultState.VALID, result.getState());
    }
}
