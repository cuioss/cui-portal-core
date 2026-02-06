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
package de.cuioss.portal.authentication.oauth;

import de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.cuioss.portal.authentication.oauth.OAuthConfigKeys.*;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableTestLogger(debug = DefaultConfigTest.class)
@DisplayName("Tests OAuth Configuration Keys")
class DefaultConfigTest extends AbstractConfigurationKeyVerifierTest {

    @Override
    public Class<?> getKeyHolder() {
        return OAuthConfigKeys.class;
    }

    @Override
    public String getConfigSourceName() {
        return "portal-authentication-oauth";
    }

    @Override
    public List<String> getKeysIgnoreList() {
        return immutableList(
                OPEN_ID_CLIENT_ID,
                OPEN_ID_CLIENT_SECRET,
                OPEN_ID_SERVER_BASE_URL,
                EXTERNAL_HOSTNAME,
                OPEN_ID_SERVER_TOKEN_URL,
                OPEN_ID_SERVER_USER_INFO_URL,
                OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI,
                OPEN_ID_ROLE_MAPPER_CLAIM
        );
    }

    @Override
    public List<String> getConfigurationKeysIgnoreList() {
        return immutableList(OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER);
    }

    @Nested
    @DisplayName("Validate Configuration Keys")
    class ConfigurationKeyTests {

        @Test
        @DisplayName("Should provide valid client configuration keys")
        void shouldProvideClientConfigKeys() {
            assertNotNull(OPEN_ID_CLIENT_ID);
            assertNotNull(OPEN_ID_CLIENT_SECRET);
            assertEquals("authentication.oidc.client.id", OPEN_ID_CLIENT_ID);
            assertEquals("authentication.oidc.client.password", OPEN_ID_CLIENT_SECRET);
        }

        @Test
        @DisplayName("Should provide valid server configuration keys")
        void shouldProvideServerConfigKeys() {
            assertNotNull(OPEN_ID_SERVER_BASE_URL);
            assertNotNull(OPEN_ID_SERVER_TOKEN_URL);
            assertNotNull(OPEN_ID_SERVER_USER_INFO_URL);
            assertEquals("authentication.oidc.server.url", OPEN_ID_SERVER_BASE_URL);
            assertEquals("authentication.oidc.server.token_endpoint_url", OPEN_ID_SERVER_TOKEN_URL);
            assertEquals("authentication.oidc.server.userinfo_endpoint_url", OPEN_ID_SERVER_USER_INFO_URL);
        }

        @Test
        @DisplayName("Should provide valid logout configuration keys")
        void shouldProvideLogoutConfigKeys() {
            assertNotNull(OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI);
            assertNotNull(OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER);
            assertEquals("authentication.oidc.client.logout.redirect_uri", OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI);
            assertEquals("authentication.oidc.client.logout_redirect_parameter", OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER);
        }

        @Test
        @DisplayName("Should provide valid role mapper configuration key")
        void shouldProvideRoleMapperConfigKey() {
            assertNotNull(OPEN_ID_ROLE_MAPPER_CLAIM);
            assertEquals("authentication.oidc.client.role_mapper_claim", OPEN_ID_ROLE_MAPPER_CLAIM);
        }

        @Test
        @DisplayName("Should provide valid external hostname configuration key")
        void shouldProvideExternalHostnameConfigKey() {
            assertNotNull(EXTERNAL_HOSTNAME);
            assertEquals("authentication.externalHostname", EXTERNAL_HOSTNAME);
        }
    }
}
