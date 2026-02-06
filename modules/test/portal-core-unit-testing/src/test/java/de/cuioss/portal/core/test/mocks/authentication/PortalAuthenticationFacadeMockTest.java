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
package de.cuioss.portal.core.test.mocks.authentication;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
class PortalAuthenticationFacadeMockTest implements ShouldBeNotNull<PortalAuthenticationFacadeMock> {

    @Getter
    @Inject
    @PortalAuthenticationFacade
    private PortalAuthenticationFacadeMock underTest;

    @Test
    void shouldDefaultSensibly() {
        assertNotNull(underTest.getAvailableUserStores());
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        void shouldHandleValidLogin() {
            var result = underTest.login(null, PortalAuthenticationFacadeMock.createLoginCredentials(PortalAuthenticationFacadeMock.ADMIN));
            assertTrue(result.isValid());
            assertTrue(result.getResult().isAuthenticated());
        }

        @Test
        void shouldHandleInvalidLogin() {
            var result = underTest.login(null, PortalAuthenticationFacadeMock.createLoginCredentials("user", "wrongpass"));
            assertFalse(result.isValid());
        }

        @Test
        void shouldHandleLogout() {
            underTest.doLogin();
            assertTrue(underTest.retrieveCurrentAuthenticationContext(null).isAuthenticated());

            assertTrue(underTest.logout(null));
            assertFalse(underTest.retrieveCurrentAuthenticationContext(null).isAuthenticated());
        }
    }

    @Nested
    @DisplayName("User Store Tests")
    class UserStoreTests {

        @Test
        void shouldProvideDefaultUserStores() {
            var stores = underTest.getAvailableUserStores();
            assertNotNull(stores);
            assertTrue(stores.contains(PortalAuthenticationFacadeMock.DEFAULT_USER_STORE));
            assertTrue(stores.contains(PortalAuthenticationFacadeMock.SOME_LDAP_USER_STORE));
            assertTrue(stores.contains(PortalAuthenticationFacadeMock.SOME_OTHER_LDAP_USER_STORE));
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        void shouldCreateLoginCredentials() {
            var credentials = PortalAuthenticationFacadeMock.createLoginCredentials("testuser", "testpass");
            assertNotNull(credentials);
            assertTrue(credentials.isComplete());
        }

        @Test
        void shouldCreateLoginCredentialsWithSamePassword() {
            var credentials = PortalAuthenticationFacadeMock.createLoginCredentials("testuser");
            assertNotNull(credentials);
            assertTrue(credentials.isComplete());
        }

        @Test
        void shouldAssertAuthenticationState() {
            underTest.doLogin();
            underTest.assertAuthenticated(true);

            underTest.logout(null);
            underTest.assertAuthenticated(false);
        }
    }
}
