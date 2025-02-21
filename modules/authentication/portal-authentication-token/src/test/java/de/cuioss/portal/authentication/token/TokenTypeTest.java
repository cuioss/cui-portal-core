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
package de.cuioss.portal.authentication.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Tests the TokenType enum functionality")
class TokenTypeTest {

    @Nested
    @DisplayName("TokenType.fromTypClaim() Tests")
    class FromTypClaimTests {

        @ParameterizedTest
        @EnumSource(TokenType.class)
        @DisplayName("Should correctly parse valid type claims")
        void shouldHandleValidTokenTypes(TokenType tokenType) {
            assertNotNull(tokenType.getTypeClaimName(), "Type claim name should not be null");
            assertEquals(tokenType, TokenType.fromTypClaim(tokenType.getTypeClaimName()),
                    "Should correctly parse " + tokenType.name());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"invalid", "unknown", "not_a_token_type"})
        @DisplayName("Should return UNKNOWN for invalid type claims")
        void shouldDefaultToUnknown(String invalidType) {
            assertEquals(TokenType.UNKNOWN, TokenType.fromTypClaim(invalidType),
                    "Should return UNKNOWN for invalid type: " + invalidType);
        }
    }

    @Nested
    @DisplayName("TokenType Enum Properties Tests")
    class EnumPropertiesTests {

        @Test
        @DisplayName("Should have non-null type claim names for all enum values")
        void shouldHaveValidTypeClaimNames() {
            for (TokenType tokenType : TokenType.values()) {
                assertNotNull(tokenType.getTypeClaimName(),
                        "Type claim name should not be null for " + tokenType.name());
            }
        }
    }
}