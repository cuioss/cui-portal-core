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

import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.net.UrlParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@EnableTestLogger(debug = OidcRpInitiatedLogoutParamsTest.class)
@DisplayName("Tests OIDC RP-Initiated Logout Parameters")
class OidcRpInitiatedLogoutParamsTest {

    @Nested
    @DisplayName("Parameter Name Tests")
    class ParameterNameTests {
        @Test
        @DisplayName("Should define correct parameter names")
        void shouldDefineCorrectParameterNames() {
            assertEquals("id_token_hint", OidcRpInitiatedLogoutParams.ID_TOKEN_HINT);
            assertEquals("logout_hint", OidcRpInitiatedLogoutParams.LOGOUT_HINT);
            assertEquals("client_id", OidcRpInitiatedLogoutParams.CLIENT_ID);
            assertEquals("post_logout_redirect_uri", OidcRpInitiatedLogoutParams.POST_LOGOUT_REDIRECT_URI);
            assertEquals("state", OidcRpInitiatedLogoutParams.STATE);
            assertEquals("ui_locales", OidcRpInitiatedLogoutParams.UI_LOCALES);
        }
    }

    @Nested
    @DisplayName("Parameter Value Tests")
    class ParameterValueTests {
        @Test
        @DisplayName("Should handle valid parameter values")
        void shouldHandleValidParameterValues() {
            String testValue = "test-value";

            Optional<UrlParameter> idTokenParam = OidcRpInitiatedLogoutParams.getIdTokenHintUrlParam(testValue);
            assertTrue(idTokenParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.ID_TOKEN_HINT, idTokenParam.get().getName());
            assertEquals(testValue, idTokenParam.get().getValue());

            Optional<UrlParameter> logoutHintParam = OidcRpInitiatedLogoutParams.getLogoutHintUrlParam(testValue);
            assertTrue(logoutHintParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.LOGOUT_HINT, logoutHintParam.get().getName());
            assertEquals(testValue, logoutHintParam.get().getValue());

            Optional<UrlParameter> clientIdParam = OidcRpInitiatedLogoutParams.getClientIdUrlParam(testValue);
            assertTrue(clientIdParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.CLIENT_ID, clientIdParam.get().getName());
            assertEquals(testValue, clientIdParam.get().getValue());

            Optional<UrlParameter> redirectUriParam = OidcRpInitiatedLogoutParams.getPostLogoutRedirectUriUrlParam(testValue);
            assertTrue(redirectUriParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.POST_LOGOUT_REDIRECT_URI, redirectUriParam.get().getName());
            assertEquals(testValue, redirectUriParam.get().getValue());

            Optional<UrlParameter> stateParam = OidcRpInitiatedLogoutParams.getStateUrlParam(testValue);
            assertTrue(stateParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.STATE, stateParam.get().getName());
            assertEquals(testValue, stateParam.get().getValue());

            Optional<UrlParameter> localesParam = OidcRpInitiatedLogoutParams.getUiLocalesUrlParam(testValue);
            assertTrue(localesParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.UI_LOCALES, localesParam.get().getName());
            assertEquals(testValue, localesParam.get().getValue());
        }

        @Test
        @DisplayName("Should handle null parameter values")
        void shouldHandleNullParameterValues() {
            assertFalse(OidcRpInitiatedLogoutParams.getIdTokenHintUrlParam(null).isPresent());
            assertFalse(OidcRpInitiatedLogoutParams.getLogoutHintUrlParam(null).isPresent());
            assertFalse(OidcRpInitiatedLogoutParams.getClientIdUrlParam(null).isPresent());
            assertFalse(OidcRpInitiatedLogoutParams.getPostLogoutRedirectUriUrlParam(null).isPresent());
            assertFalse(OidcRpInitiatedLogoutParams.getStateUrlParam(null).isPresent());
            assertFalse(OidcRpInitiatedLogoutParams.getUiLocalesUrlParam(null).isPresent());
        }

        @Test
        @DisplayName("Should handle empty parameter values")
        void shouldHandleEmptyParameterValues() {
            String emptyValue = "";
            assertTrue(OidcRpInitiatedLogoutParams.getIdTokenHintUrlParam(emptyValue).isPresent());
            assertTrue(OidcRpInitiatedLogoutParams.getLogoutHintUrlParam(emptyValue).isPresent());
            assertTrue(OidcRpInitiatedLogoutParams.getClientIdUrlParam(emptyValue).isPresent());
            assertTrue(OidcRpInitiatedLogoutParams.getPostLogoutRedirectUriUrlParam(emptyValue).isPresent());
            assertTrue(OidcRpInitiatedLogoutParams.getStateUrlParam(emptyValue).isPresent());
            assertTrue(OidcRpInitiatedLogoutParams.getUiLocalesUrlParam(emptyValue).isPresent());
        }

        @Test
        @DisplayName("Should handle special characters in parameter values")
        void shouldHandleSpecialCharactersInParameterValues() {
            String specialValue = "test value with spaces & special chars!";
            String encodedValue = URLEncoder.encode(specialValue, StandardCharsets.UTF_8);

            Optional<UrlParameter> idTokenParam = OidcRpInitiatedLogoutParams.getIdTokenHintUrlParam(specialValue);
            assertTrue(idTokenParam.isPresent());
            assertEquals(OidcRpInitiatedLogoutParams.ID_TOKEN_HINT, idTokenParam.get().getName());
            assertEquals(encodedValue, idTokenParam.get().getValue());
        }
    }
}
