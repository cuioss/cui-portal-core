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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.oauth.OAuthConfigKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.generator.impl.URLGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import jakarta.inject.Inject;
import lombok.Getter;
import mockwebserver3.MockWebServer;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = "authentication.oidc.validation.enabled:false")
@EnableMockWebServer
@EnableTestLogger(debug = Oauth2DiscoveryConfigurationProducerTest.class)
@AddBeanClasses({Oauth2DiscoveryConfigurationProducer.class})
@AddExtensions(ResteasyCdiExtension.class)
@DisplayName("Tests OAuth2 Discovery Configuration Producer")
class Oauth2DiscoveryConfigurationProducerTest
        implements ShouldHandleObjectContracts<Oauth2DiscoveryConfigurationProducer> {

    @Inject
    @Getter
    private Oauth2DiscoveryConfigurationProducer underTest;

    @Inject
    private PortalTestConfiguration configuration;

    @Nested
    @DisplayName("Discovery Configuration Tests")
    @ExplicitParamInjection
    class DiscoveryConfigurationTests {

        @Getter
        private final OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

        @BeforeEach
        void beforeEach(MockWebServer mockWebServer) {
            dispatcher.reset();
            configuration.update(OAuthConfigKeys.CONFIG_VALIDATION_ENABLED, "false");
            dispatcher.configure(configuration, mockWebServer);
            mockWebServer.setDispatcher(dispatcher);
        }


        @Test
        @DisplayName("Should initialize with discovery configuration")
        void shouldInitializeWithDiscoveryConfiguration() {
            var result = underTest.getConfiguration();

            assertNotNull(result);
            assertNotNull(result.getTokenUri());
            assertNotNull(result.getAuthorizeUri());
            assertNotNull(result.getUserInfoUri());
            assertNotNull(result.getLogoutUri());
        }

        @Test
        @DisplayName("Should override discovery URLs with configured values")
        void shouldOverrideDiscoveryUrls() {
            var tokenUrl = new URLGenerator().next().toString();
            var userInfoUrl = new URLGenerator().next().toString();
            configuration.update(
                    OAuthConfigKeys.OPEN_ID_SERVER_TOKEN_URL, tokenUrl,
                    OAuthConfigKeys.OPEN_ID_SERVER_USER_INFO_URL, userInfoUrl);

            var result = underTest.getConfiguration();

            assertEquals(tokenUrl, result.getTokenUri());
            assertEquals(userInfoUrl, result.getUserInfoUri());
        }

        @Test
        @DisplayName("Should handle invalid OIDC configuration")
        void shouldHandleInvalidOidcConfig(MockWebServer mockWebServer) {
            configuration.update(OAuthConfigKeys.CONFIG_VALIDATION_ENABLED, "true");
            dispatcher.setSimulateInvalidOidcConfig(true);
            dispatcher.configure(configuration, mockWebServer);
            mockWebServer.setDispatcher(dispatcher);

            WeldException ex = assertThrows(WeldException.class, underTest::getConfiguration);
            assertNotNull(ex.getCause());
        }
    }

    @Nested
    @DisplayName("Configuration Property Tests")
    @ExplicitParamInjection
    class ConfigurationPropertyTests {
        @Getter
        private final OIDCWellKnownDispatcher dispatcher = new OIDCWellKnownDispatcher();

        @BeforeEach
        void beforeEach(MockWebServer mockWebServer) {
            dispatcher.reset();
            configuration.update(OAuthConfigKeys.CONFIG_VALIDATION_ENABLED, "false");
            dispatcher.configure(configuration, mockWebServer);
            mockWebServer.setDispatcher(dispatcher);
        }

        @Test
        @DisplayName("Should handle custom scopes")
        void shouldHandleCustomScopes() {
            String customScopes = "scope1 scope2";
            configuration.update(OAuthConfigKeys.OPEN_ID_CLIENT_DEFAULT_SCOPES, customScopes);

            var result = underTest.getConfiguration();

            assertEquals(customScopes, result.getInitialScopes());
        }

        @Test
        @DisplayName("Should handle role mapper claims")
        void shouldHandleRoleMapperClaims() {
            String claim = "roles";
            configuration.update(OAuthConfigKeys.OPEN_ID_ROLE_MAPPER_CLAIM, claim);

            var result = underTest.getConfiguration();

            assertNotNull(result.getRoleMapperClaims());
            assertEquals(1, result.getRoleMapperClaims().size());
            assertEquals(claim, result.getRoleMapperClaims().get(0));
        }

        @Test
        @DisplayName("Should handle logout configuration")
        void shouldHandleLogoutConfiguration() {
            Map<String, String> configMap = new HashMap<>();
            configMap.put(OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER, "post_logout_redirect_uri");
            configMap.put(OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT, "true");
            configMap.put(OAuthConfigKeys.OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI, "http://localhost/logout");
            configuration.update(configMap);

            var result = underTest.getConfiguration();

            assertEquals("post_logout_redirect_uri", result.getLogoutRedirectParamName());
            assertEquals("http://localhost/logout", result.getPostLogoutRedirectUri());
            assertTrue(result.isLogoutWithIdTokenHintEnabled());
        }
    }
}
