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
package de.cuioss.portal.test.keycloakit;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeycloakITBaseTest extends KeycloakITBase {

    @BeforeEach
    void initTls() {
        // Configure RestAssured to use the keystore used / provided by testcontainers-keycloak
        RestAssured.config = RestAssured.config().sslConfig(
                SSLConfig.sslConfig().trustStore(TestRealm.ProvidedKeyStore.KEYSTORE_PATH, TestRealm.ProvidedKeyStore.PASSWORD)
        );
    }

    @Test
    void shouldStartKeycloak() {
        assertNotNull(keycloak);
        assertTrue(keycloak.isCreated());
        assertTrue(keycloak.isRunning());
    }

    @Test
    void shouldBeConfiguredWithHttpsAndProvideUrls() {

        assertTrue(keycloak.getAuthServerUrl().startsWith("https://"), keycloak.getAuthServerUrl());

        given()
                .when().get(keycloak.getAuthServerUrl())
                .then().statusCode(200);

        given()
                .when().get(getWellKnownUrl())
                .then().statusCode(200);

        given()
                .when().get(getJWKSUrl())
                .then().statusCode(200);

        given()
                .when().get(getIssuer())
                .then().statusCode(200);

    }

    // No @ParameterizedTest because of multiple keycloak-starts ... for some reason withReuse does not work
    // @CsvSource(value = {TokenTypes.ACCESS, TokenTypes.ID_TOKEN, TokenTypes.REFRESH})
    @Test
    void shouldAccessToken() {
        for (String tokenType : List.of(TokenTypes.ACCESS, TokenTypes.ID_TOKEN, TokenTypes.REFRESH)) {
            var token = requestToken(parameterForScopedToken("openid"), tokenType);
            assertNotNull(token);
        }
    }


    protected String requestToken(Map<String, String> parameter, String tokenType) {
        return given().contentType("application/x-www-form-urlencoded")
                .formParams(parameter)
                .post(getTokenUrl()).then().assertThat().statusCode(200)
                .extract().path(tokenType);
    }

}