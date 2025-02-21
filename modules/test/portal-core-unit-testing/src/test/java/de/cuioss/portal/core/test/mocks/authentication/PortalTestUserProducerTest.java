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
package de.cuioss.portal.core.test.mocks.authentication;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@AddBeanClasses(PortalTestUserProducer.class)
@DisplayName("PortalTestUserProducer Tests")
class PortalTestUserProducerTest {

    @Inject
    private PortalTestUserProducer userProducer;

    @Inject
    private Provider<AuthenticatedUserInfo> userProvider;

    @Test
    @DisplayName("Should provide producer instance")
    void shouldProvideProducer() {
        assertNotNull(userProducer);
        assertNotNull(userProvider);
    }

    @Nested
    @DisplayName("Authentication State Tests")
    class AuthenticationStateTest {

        @Test
        @DisplayName("Should handle authentication state changes")
        void shouldHandleAuthenticationState() {
            assertTrue(userProvider.get().isAuthenticated(), "Default state should be authenticated");

            userProducer.authenticated(false);
            assertFalse(userProvider.get().isAuthenticated(), "User should be unauthenticated");

            userProducer.authenticated(true);
            assertTrue(userProvider.get().isAuthenticated(), "User should be authenticated again");
        }

        @Test
        @DisplayName("Should preserve user info across state changes")
        void shouldPreserveUserInfo() {
            var user = userProvider.get();
            var identifier = user.getIdentifier();

            userProducer.authenticated(false);
            assertEquals(identifier, userProvider.get().getIdentifier(),
                    "User identifier should be preserved");
        }
    }

    @Nested
    @DisplayName("User Attributes Tests")
    class UserAttributesTest {

        @Test
        @DisplayName("Should handle role modifications")
        void shouldHandleRoles() {
            userProducer.roles(List.of("ADMIN"));
            assertTrue(userProvider.get().getRoles().contains("ADMIN"),
                    "Added role should be present");

            userProducer.roles(List.of());
            assertFalse(userProvider.get().getRoles().contains("ADMIN"),
                    "Removed role should not be present");
        }

        @Test
        @DisplayName("Should handle attribute modifications")
        void shouldHandleAttributes() {
            userProducer.contextMap(Map.of("department", "IT"));
            assertEquals("IT", userProvider.get().getContextMap().get("department"),
                    "Added attribute should be present");

            userProducer.contextMap(Map.of());
            assertFalse(userProvider.get().getContextMap().containsKey("department"),
                    "Removed attribute should not be present");
        }
    }
}
