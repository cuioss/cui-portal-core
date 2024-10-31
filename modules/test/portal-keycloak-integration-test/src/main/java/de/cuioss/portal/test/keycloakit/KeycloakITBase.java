package de.cuioss.portal.test.keycloakit;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

/**
 * Base-class for creating keycloak based integration-tests.
 * It uses configuration defined within {@link TestRealm}
 */
@Testcontainers
public class KeycloakITBase {

    public enum TokenTypes {
        ;
        public static final String ACCESS = "access_token";
        public static final String REFRESH = "refresh_token";
        public static final String ID_TOKEN = "id_token";
    }

    @Container
    KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile(TestRealm.REALM_CONFIGURATION).
            withAdminUsername(TestRealm.administrator.NAME).withAdminPassword(TestRealm.administrator.PASSWORD)
            .useTls();

    /**
     * Creates a map for post-requests containing all necessary parameters for retrieving an access- /id / refresh-token
     *
     * @param requestedScopes colon separated list of requested scopes
     */
    protected Map<String, String> parameterForScopedToken(String requestedScopes) {
        return Map.of(
                "username", TestRealm.testUser.NAME,
                "password", TestRealm.testUser.PASSWORD,
                "grant_type", "password",
                "scope", requestedScopes,
                "client_id", TestRealm.client.ID,
                "client_secret", TestRealm.client.PASSWORD
        );
    }

    /**
     * Resolves the realm-url for the configured realm.
     */
    protected String getRealmUrl() {
        return keycloak.getAuthServerUrl() + "/realms/" + TestRealm.REALM_NAME + "/";
    }

    /**
     * Resolves the well-known-url for the configured realm.
     */
    protected String getWellKnownUrl() {
        return getRealmUrl() + ".well-known/openid-configuration";
    }

    /**
     * Resolves the "protocol/openid-connect/"-url for the configured realm.
     */
    protected String getOIDCUrl() {
        return getRealmUrl() + "protocol/openid-connect/";
    }

    /**
     * Resolves the token-url for the configured realm.
     */
    protected String getTokenUrl() {
        return getOIDCUrl() + "token";
    }

    /**
     * Resolves the JWKS-url for the configured realm.
     */
    protected String getJWKSUrl() {
        return getOIDCUrl() + "certs";
    }

}
