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
        var accessToken = ParsedAccessToken.fromTokenString(tokenString, parser);
        assertFalse(accessToken.isEmpty());
        assertTrue(accessToken.isValid());
        LOGGER.info(accessToken.getScopes().toString());
        assertTrue(accessToken.providesScopes(SCOPES_AS_LIST));
        assertEquals(TestRealm.testUser.EMAIL.toLowerCase(), accessToken.getEmail().get());

        tokenString = requestToken(parameterForScopedToken(SCOPES), TokenTypes.ID_TOKEN);
        var idToken = ParsedIdToken.fromTokenString(tokenString, parser);
        assertFalse(idToken.isEmpty());
        assertTrue(idToken.isValid());
        assertEquals(TestRealm.testUser.EMAIL.toLowerCase(), accessToken.getEmail().get());
    }

    private String requestToken(Map<String, String> parameter, String tokenType) {
        return given().contentType("application/x-www-form-urlencoded")
                .formParams(parameter)
                .post(getTokenUrl()).then().assertThat().statusCode(200)
                .extract().path(tokenType);
    }

}
