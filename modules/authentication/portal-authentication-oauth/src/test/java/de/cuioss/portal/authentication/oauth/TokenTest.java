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
package de.cuioss.portal.authentication.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBeanProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableTestLogger(debug = TokenTest.class)
@VerifyBeanProperty
@DisplayName("Tests Token Object")
class TokenTest extends ValueObjectTest<Token> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTest {

        @Test
        @DisplayName("Should deserialize minimal token")
        void shouldDeserializeMinimalToken() throws Exception {
            String json = """
                {
                    "access_token": "test-access-token",
                    "token_type": "Bearer"
                }
                """;

            Token token = MAPPER.readValue(json, Token.class);
            assertNotNull(token);
            assertEquals("test-access-token", token.getAccess_token());
            assertEquals("Bearer", token.getToken_type());
            assertNull(token.getId_token());
            assertNull(token.getRefresh_token());
            assertNull(token.getExpires_in());
            assertNull(token.getState());
        }

        @Test
        @DisplayName("Should deserialize complete token")
        void shouldDeserializeCompleteToken() throws Exception {
            String json = """
                {
                    "access_token": "test-access-token",
                    "id_token": "test-id-token",
                    "refresh_token": "test-refresh-token",
                    "token_type": "Bearer",
                    "expires_in": "3600",
                    "state": "test-state"
                }
                """;

            Token token = MAPPER.readValue(json, Token.class);
            assertNotNull(token);
            assertEquals("test-access-token", token.getAccess_token());
            assertEquals("test-id-token", token.getId_token());
            assertEquals("test-refresh-token", token.getRefresh_token());
            assertEquals("Bearer", token.getToken_type());
            assertEquals("3600", token.getExpires_in());
            assertEquals("test-state", token.getState());
        }

        @Test
        @DisplayName("Should ignore unknown properties")
        void shouldIgnoreUnknownProperties() throws Exception {
            String json = """
                {
                    "access_token": "test-access-token",
                    "token_type": "Bearer",
                    "unknown_property": "should-be-ignored"
                }
                """;

            Token token = MAPPER.readValue(json, Token.class);
            assertNotNull(token);
            assertEquals("test-access-token", token.getAccess_token());
            assertEquals("Bearer", token.getToken_type());
        }
    }
}
