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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger(debug = Oauth2ConfigurationImplTest.class)
@DisplayName("Tests OAuth2 Configuration Implementation")
class Oauth2ConfigurationImplTest {

    private static final TypedGenerator<String> nonEmptyLetterStringGenerator = Generators.letterStrings(1, 5);

    @Nested
    @DisplayName("Configuration Validation Tests")
    class ConfigurationValidationTests {
        @Test
        @DisplayName("Should validate valid configuration")
        void validConfig() {
            Oauth2ConfigurationImpl config = createConfig();
            assertDoesNotThrow(config::validate);
        }

        @Test
        @DisplayName("Should require token URI")
        void missingTokenUri() {
            Oauth2ConfigurationImpl config = createConfig();
            config.setTokenUri(null);
            IllegalStateException exception = assertThrows(IllegalStateException.class, config::validate);
            assertEquals("OAuth tokenUri missing", exception.getMessage());
        }

        @Test
        @DisplayName("Should require client ID")
        void missingClientId() {
            Oauth2ConfigurationImpl config = createConfig();
            config.setClientId(null);
            IllegalStateException exception = assertThrows(IllegalStateException.class, config::validate);
            assertEquals("OAuth clientId missing", exception.getMessage());
        }

        @Test
        @DisplayName("Should require authorize URI")
        void missingAuthorizeUri() {
            Oauth2ConfigurationImpl config = createConfig();
            config.setAuthorizeUri(null);
            IllegalStateException exception = assertThrows(IllegalStateException.class, config::validate);
            assertEquals("OAuth authorizeUri missing", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {
        @Test
        @DisplayName("Should build complete configuration")
        void shouldBuildCompleteConfiguration() {
            String clientId = nonEmptyLetterStringGenerator.next();
            String clientSecret = nonEmptyLetterStringGenerator.next();
            String initialScopes = nonEmptyLetterStringGenerator.next();
            String logoutUri = nonEmptyLetterStringGenerator.next();
            String authorizeUri = nonEmptyLetterStringGenerator.next();
            String tokenUri = nonEmptyLetterStringGenerator.next();
            String userInfoUri = nonEmptyLetterStringGenerator.next();
            String externalContextPath = nonEmptyLetterStringGenerator.next();
            String logoutRedirectParamName = nonEmptyLetterStringGenerator.next();
            String postLogoutRedirectUri = nonEmptyLetterStringGenerator.next();
            List<String> roleMapperClaims = List.of(nonEmptyLetterStringGenerator.next());

            Oauth2ConfigurationImpl config = Oauth2ConfigurationImpl.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .initialScopes(initialScopes)
                    .logoutUri(logoutUri)
                    .authorizeUri(authorizeUri)
                    .tokenUri(tokenUri)
                    .userInfoUri(userInfoUri)
                    .externalContextPath(externalContextPath)
                    .logoutRedirectParamName(logoutRedirectParamName)
                    .postLogoutRedirectUri(postLogoutRedirectUri)
                    .roleMapperClaims(roleMapperClaims)
                    .logoutWithIdTokenHintEnabled(true)
                    .build();

            assertNotNull(config);
            assertEquals(clientId, config.getClientId());
            assertEquals(clientSecret, config.getClientSecret());
            assertEquals(initialScopes, config.getInitialScopes());
            assertEquals(logoutUri, config.getLogoutUri());
            assertEquals(authorizeUri, config.getAuthorizeUri());
            assertEquals(tokenUri, config.getTokenUri());
            assertEquals(userInfoUri, config.getUserInfoUri());
            assertEquals(externalContextPath, config.getExternalContextPath());
            assertEquals(logoutRedirectParamName, config.getLogoutRedirectParamName());
            assertEquals(postLogoutRedirectUri, config.getPostLogoutRedirectUri());
            assertEquals(roleMapperClaims, config.getRoleMapperClaims());
            assertTrue(config.isLogoutWithIdTokenHintEnabled());
        }

        @Test
        @DisplayName("Should build minimal configuration")
        void shouldBuildMinimalConfiguration() {
            String clientId = nonEmptyLetterStringGenerator.next();
            String authorizeUri = nonEmptyLetterStringGenerator.next();
            String tokenUri = nonEmptyLetterStringGenerator.next();

            Oauth2ConfigurationImpl config = Oauth2ConfigurationImpl.builder()
                    .clientId(clientId)
                    .authorizeUri(authorizeUri)
                    .tokenUri(tokenUri)
                    .build();

            assertNotNull(config);
            assertEquals(clientId, config.getClientId());
            assertEquals(authorizeUri, config.getAuthorizeUri());
            assertEquals(tokenUri, config.getTokenUri());
            assertFalse(config.isLogoutWithIdTokenHintEnabled());
            assertDoesNotThrow(config::validate);
        }
    }

    private Oauth2ConfigurationImpl createConfig() {
        return Oauth2ConfigurationImpl.builder()
                .clientId(nonEmptyLetterStringGenerator.next())
                .clientSecret(nonEmptyLetterStringGenerator.next())
                .initialScopes(nonEmptyLetterStringGenerator.next())
                .logoutUri(nonEmptyLetterStringGenerator.next())
                .authorizeUri(nonEmptyLetterStringGenerator.next())
                .tokenUri(nonEmptyLetterStringGenerator.next())
                .userInfoUri(nonEmptyLetterStringGenerator.next())
                .externalContextPath(nonEmptyLetterStringGenerator.next())
                .logoutRedirectParamName(nonEmptyLetterStringGenerator.next())
                .postLogoutRedirectUri(nonEmptyLetterStringGenerator.next())
                .roleMapperClaims(List.of(nonEmptyLetterStringGenerator.next()))
                .build();
    }
}
