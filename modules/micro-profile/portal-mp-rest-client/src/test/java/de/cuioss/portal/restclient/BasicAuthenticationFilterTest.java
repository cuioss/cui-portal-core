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
package de.cuioss.portal.restclient;

import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link BasicAuthenticationFilter} class focusing on parameter validation
 * and header generation for Basic Authentication.
 */
@EnableTestLogger
@DisplayName("BasicAuthenticationFilter Tests")
class BasicAuthenticationFilterTest {

    private static final String VALID_USERNAME = "testUser";
    private static final String VALID_PASSWORD = "testPass123!";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Should throw NPE for null constructor parameters")
        void shouldThrowNPEForNullParameters() {
            assertThrows(IllegalArgumentException.class, () -> new BasicAuthenticationFilter(null, VALID_PASSWORD),
                    "Should throw IllegalArgumentException for null username");
            assertThrows(IllegalArgumentException.class, () -> new BasicAuthenticationFilter(VALID_USERNAME, null),
                    "Should throw IllegalArgumentException for null password");
            assertThrows(IllegalArgumentException.class, () -> new BasicAuthenticationFilter(null, null),
                    "Should throw IllegalArgumentException for both null parameters");
        }

        @Test
        @DisplayName("Should create filter with valid parameters")
        void shouldCreateFilterWithValidParameters() {
            assertDoesNotThrow(() -> new BasicAuthenticationFilter(VALID_USERNAME, VALID_PASSWORD),
                    "Should create filter with valid parameters");
        }
    }

    @Nested
    @DisplayName("Header Generation Tests")
    class HeaderGenerationTests {
        @Test
        @DisplayName("Should generate correct basic auth header")
        void shouldGenerateCorrectBasicAuthHeader() throws IOException {
            // Given
            var filter = new BasicAuthenticationFilter(VALID_USERNAME, VALID_PASSWORD);
            var headers = new MultivaluedHashMap<String, Object>();
            ClientRequestContext requestContext = EasyMock.createMock(ClientRequestContext.class);
            EasyMock.expect(requestContext.getHeaders()).andReturn(headers).anyTimes();
            EasyMock.replay(requestContext);

            // When
            filter.filter(requestContext);

            // Then
            EasyMock.verify(requestContext);
            var authHeader = headers.getFirst(AUTH_HEADER).toString();
            assertTrue(authHeader.startsWith(BASIC_PREFIX), "Header should start with 'Basic '");
            
            // Verify Base64 encoding
            String credentials = new String(Base64.getDecoder().decode(
                    authHeader.substring(BASIC_PREFIX.length())), 
                    StandardCharsets.UTF_8);
            assertEquals(VALID_USERNAME + ":" + VALID_PASSWORD, credentials, 
                    "Decoded credentials should match input");
        }

        @ParameterizedTest(name = "Should handle special characters in username: {0}")
        @ValueSource(strings = {
            "user@domain.com",
            "user.name",
            "user+name",
            "user name",
            "user123"
        })
        void shouldHandleSpecialCharactersInUsername(String username) throws IOException {
            // Given
            var filter = new BasicAuthenticationFilter(username, VALID_PASSWORD);
            var headers = new MultivaluedHashMap<String, Object>();
            ClientRequestContext requestContext = EasyMock.createMock(ClientRequestContext.class);
            EasyMock.expect(requestContext.getHeaders()).andReturn(headers).anyTimes();
            EasyMock.replay(requestContext);

            // When
            filter.filter(requestContext);

            // Then
            EasyMock.verify(requestContext);
            var authHeader = headers.getFirst(AUTH_HEADER).toString();
            String credentials = new String(Base64.getDecoder().decode(
                    authHeader.substring(BASIC_PREFIX.length())), 
                    StandardCharsets.UTF_8);
            assertEquals(username + ":" + VALID_PASSWORD, credentials, 
                    "Should handle special characters in username");
        }

        @ParameterizedTest(name = "Should handle special characters in password: {0}")
        @ValueSource(strings = {
            "pass@word123",
            "pass.word123",
            "pass word 123",
            "pass123",
            "pass!@#$%^&*()"
        })
        void shouldHandleSpecialCharactersInPassword(String password) throws IOException {
            // Given
            var filter = new BasicAuthenticationFilter(VALID_USERNAME, password);
            var headers = new MultivaluedHashMap<String, Object>();
            ClientRequestContext requestContext = EasyMock.createMock(ClientRequestContext.class);
            EasyMock.expect(requestContext.getHeaders()).andReturn(headers).anyTimes();
            EasyMock.replay(requestContext);

            // When
            filter.filter(requestContext);

            // Then
            EasyMock.verify(requestContext);
            var authHeader = headers.getFirst(AUTH_HEADER).toString();
            String credentials = new String(Base64.getDecoder().decode(
                    authHeader.substring(BASIC_PREFIX.length())), 
                    StandardCharsets.UTF_8);
            assertEquals(VALID_USERNAME + ":" + password, credentials, 
                    "Should handle special characters in password");
        }

        @ParameterizedTest(name = "Should handle empty strings - username: {0}, password: {1}")
        @CsvSource({
            "'',validpass",
            "validuser,''",
            "'',''"
        })
        void shouldHandleEmptyStrings(String username, String password) {
            assertThrows(IllegalArgumentException.class,
                () -> new BasicAuthenticationFilter(username, password),
                "Should not allow empty/null credentials");
        }
    }
}
