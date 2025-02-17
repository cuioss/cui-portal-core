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
package de.cuioss.portal.authentication.facade;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test cases for {@link BaseAuthenticationFacade} focusing on user enrichment,
 * authentication context, and logout functionality.
 */
@EnableAutoWeld
@AddBeanClasses({MockBaseAuthenticationFacade.class, MockPortalUserEnricher.class})
@PropertyReflectionConfig(skip = true)
class BaseAuthenticationFacadeTest implements ShouldBeNotNull<MockBaseAuthenticationFacade> {

    private static final TypedGenerator<String> STRING_GENERATOR = nonEmptyStrings();

    @Inject
    @Getter
    private MockBaseAuthenticationFacade underTest;

    @Nested
    @DisplayName("User Enrichment Tests")
    class UserEnrichmentTests {

        @Test
        @DisplayName("Should enrich user info with single enricher")
        void shouldEnrichWithSingleEnricher() {
            // given
            var displayName = STRING_GENERATOR.next();
            var identifier = STRING_GENERATOR.next();
            var userInfo = BaseAuthenticatedUserInfo.builder()
                    .authenticated(true)
                    .displayName(displayName)
                    .identifier(identifier)
                    .build();

            // when
            underTest.setAuthenticatedUserInfo(userInfo);
            var result = underTest.retrieveCurrentAuthenticationContext(null);

            // then
            assertNotNull(result, "Result should not be null");
            assertEquals(displayName, result.getDisplayName(), "Display name should match");
            assertEquals(identifier, result.getIdentifier(), "Identifier should match");
            assertEquals(1, result.getRoles().size(), "Should have one role");
            assertEquals("testRole", result.getRoles().get(0), "Should have test role");
        }

        @Test
        @DisplayName("Should enrich user info with roles and groups")
        void shouldEnrichWithRolesAndGroups() {
            // given
            var displayName = STRING_GENERATOR.next();
            var identifier = STRING_GENERATOR.next();
            var roles = Arrays.asList("role1", "role2");
            var groups = Arrays.asList("group1", "group2");
            var userInfo = BaseAuthenticatedUserInfo.builder()
                    .authenticated(true)
                    .displayName(displayName)
                    .identifier(identifier)
                    .roles(roles)
                    .groups(groups)
                    .build();

            // when
            underTest.setAuthenticatedUserInfo(userInfo);
            var result = underTest.retrieveCurrentAuthenticationContext(null);

            // then
            assertNotNull(result, "Result should not be null");
            assertTrue(result.getRoles().containsAll(roles), "Should contain original roles");
            assertTrue(result.getGroups().containsAll(groups), "Should contain original groups");
            assertTrue(result.getRoles().contains("testRole"), "Should contain enriched role");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null user info")
        void shouldHandleNullUserInfo() {
            // when
            underTest.setAuthenticatedUserInfo(null);
            var result = underTest.retrieveCurrentAuthenticationContext(null);

            // then
            assertNull(result, "Result should be null for null user info");
        }

        @Test
        @DisplayName("Should handle user info with empty collections")
        void shouldHandleEmptyCollections() {
            // given
            var userInfo = BaseAuthenticatedUserInfo.builder()
                    .authenticated(true)
                    .displayName("test")
                    .identifier("test")
                    .roles(Collections.emptyList())
                    .groups(Collections.emptyList())
                    .build();

            // when
            underTest.setAuthenticatedUserInfo(userInfo);
            var result = underTest.retrieveCurrentAuthenticationContext(null);

            // then
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.getRoles().size(), "Should only have enriched role");
            assertTrue(result.getGroups().isEmpty(), "Groups should be empty");
        }
    }

    @Nested
    @DisplayName("Authentication State Tests")
    class AuthenticationStateTests {

        @Test
        @DisplayName("Should handle logout")
        void shouldHandleLogout() {
            // when
            var result = underTest.logout(null);

            // then
            assertTrue(result, "Logout should return true");
            assertNull(underTest.retrieveCurrentAuthenticationContext(null),
                    "Context should be null after logout");
        }

        @Test
        @DisplayName("Should track authentication state")
        void shouldTrackAuthenticationState() {
            // given
            var userInfo = BaseAuthenticatedUserInfo.builder()
                    .authenticated(true)
                    .displayName("test")
                    .identifier("test")
                    .build();

            // when
            underTest.setAuthenticatedUserInfo(userInfo);
            var beforeLogout = underTest.retrieveCurrentAuthenticationContext(null);
            underTest.logout(null);
            var afterLogout = underTest.retrieveCurrentAuthenticationContext(null);

            // then
            assertNotNull(beforeLogout, "Context should exist before logout");
            assertNull(afterLogout, "Context should be null after logout");
        }
    }
}
