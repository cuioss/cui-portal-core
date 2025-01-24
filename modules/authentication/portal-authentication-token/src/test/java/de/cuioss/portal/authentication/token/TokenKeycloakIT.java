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

import de.cuioss.portal.test.keycloakit.KeycloakITBase;
import de.cuioss.portal.test.keycloakit.TestRealm;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Splitter;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenKeycloakIT extends KeycloakITBase {

    public static final String SCOPES = "openid email profile";
    public static final List<String> SCOPES_AS_LIST = Splitter.on(" ").splitToList(SCOPES);
    private static final CuiLogger LOGGER = new CuiLogger(TokenKeycloakIT.class);

    @BeforeEach
    void initTls() {
        // Configure RestAssured to use the keystore used / provided by testcontainers-keycloak
        RestAssured.config = RestAssured.config().sslConfig(
                SSLConfig.sslConfig().trustStore(TestRealm.providedKeyStore.KEYSTORE_PATH, TestRealm.providedKeyStore.PASSWORD)
        );
    }

    @Test
    void shouldHandleValidKeycloakTokens() {
        var tokenString = requestToken(parameterForScopedToken(SCOPES), TokenTypes.ACCESS);

        var parser = JwksAwareTokenParser.builder().jwksEndpoint(getJWKSUrl()).jwksRefreshIntervall(100).jwksIssuer(getIssuer()).tTlsCertificatePath(TestRealm.providedKeyStore.PUBLIC_CERT).build();
        var factory = TokenFactory.of(parser);
        var retrievedAccessToken = factory.createAccessToken(tokenString);
        assertTrue(retrievedAccessToken.isPresent());

        var accessToken = retrievedAccessToken.get();
        assertFalse(accessToken.isExpired());
        assertTrue(accessToken.providesScopes(SCOPES_AS_LIST));
        assertEquals(TestRealm.testUser.EMAIL.toLowerCase(), accessToken.getEmail().get());
        assertEquals(TokenType.ACCESS_TOKEN, accessToken.getType());

        tokenString = requestToken(parameterForScopedToken(SCOPES), TokenTypes.ID_TOKEN);

        var idToken = factory.createIdToken(tokenString);
        assertFalse(idToken.isEmpty());

        assertFalse(idToken.get().isExpired());
        assertEquals(TestRealm.testUser.EMAIL.toLowerCase(), accessToken.getEmail().get());

        assertEquals(TokenType.ID_TOKEN, idToken.get().getType());

        tokenString = requestToken(parameterForScopedToken(SCOPES), TokenTypes.REFRESH);
        var refreshToken = ParsedRefreshToken.fromTokenString(tokenString);
        assertFalse(refreshToken.isEmpty());
        assertEquals(TokenType.REFRESH_TOKEN, refreshToken.getType());
    }

    private String requestToken(Map<String, String> parameter, String tokenType) {
        String tokenString = given().contentType("application/x-www-form-urlencoded")
                .formParams(parameter)
                .post(getTokenUrl()).then().assertThat().statusCode(200)
                .extract().path(tokenType);
        LOGGER.info(tokenType + "\n" + tokenString);
        return tokenString;
    }

}
