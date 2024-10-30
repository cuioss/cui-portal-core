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
                SSLConfig.sslConfig().trustStore(TestRealm.providedKeyStore.KEYSTORE_PATH, TestRealm.providedKeyStore.PASSWORD)
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

    }

    // No @ParameterizedTest because of multiple keycloak starts for some reason withReuse does not work
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