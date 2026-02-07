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
package de.cuioss.portal.authentication.dummy;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.LoginResult;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import de.cuioss.uimodel.application.LoginCredentials;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@DisplayName("Tests DummyAuthenticationFacade behavior")
class DummyAuthenticationFacadeTest implements ShouldBeNotNull<DummyAuthenticationFacade> {

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private DummyAuthenticationFacade underTest;

    @Nested
    @DisplayName("Authentication Context Tests")
    class AuthenticationContextTests {

        @Test
        @DisplayName("Should return NOT_LOGGED_IN for null request")
        void shouldHandleNullRequest() {
            assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN,
                    underTest.retrieveCurrentAuthenticationContext(null));
        }

        @Test
        @DisplayName("Should return NOT_LOGGED_IN for mock request")
        void shouldHandleMockRequest() {
            HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
            EasyMock.replay(request);
            assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN,
                    underTest.retrieveCurrentAuthenticationContext(request));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should return NOT_LOGGED_IN for null request and credentials")
        void shouldHandleNullCredentials() {
            var result = underTest.login(null, null);
            assertInstanceOf(LoginResult.Success.class, result);
            assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN,
                    ((LoginResult.Success) result).authenticatedUserInfo());
        }

        @Test
        @DisplayName("Should return NOT_LOGGED_IN for valid request and credentials")
        void shouldHandleValidCredentials() {
            HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
            LoginCredentials credentials = new LoginCredentials();
            credentials.setUsername("user");
            credentials.setPassword("pass");
            EasyMock.replay(request);

            var result = underTest.login(request, credentials);
            assertInstanceOf(LoginResult.Success.class, result);
            assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN,
                    ((LoginResult.Success) result).authenticatedUserInfo());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should return false for logout attempt")
        void shouldHandleLogout() {
            HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
            EasyMock.replay(request);
            assertFalse(underTest.logout(request));
        }

        @Test
        @DisplayName("Should handle null request for logout")
        void shouldHandleNullLogout() {
            assertFalse(underTest.logout(null));
        }
    }

    @Nested
    @DisplayName("Default Configuration Tests")
    class DefaultConfigurationTests {

        @Test
        @DisplayName("Should provide NOT_LOGGED_IN with expected properties")
        void shouldProvideNotLoggedInDefaults() {
            AuthenticatedUserInfo notLoggedIn = DummyAuthenticationFacade.NOT_LOGGED_IN;
            assertFalse(notLoggedIn.isAuthenticated());
            assertTrue(notLoggedIn.getRoles().isEmpty());
            assertNotNull(notLoggedIn.getDisplayName());
            assertTrue(notLoggedIn.getContextMap().isEmpty());
        }

        @Test
        @DisplayName("Should provide empty user store list")
        void shouldProvideEmptyUserStores() {
            assertNotNull(underTest.getAvailableUserStores());
            assertTrue(underTest.getAvailableUserStores().isEmpty());
        }
    }
}
