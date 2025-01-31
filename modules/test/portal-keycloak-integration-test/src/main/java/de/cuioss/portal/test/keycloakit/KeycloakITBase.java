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

    @UtilityClass
    public static final class TokenTypes {

        public static final String ACCESS = "access_token";
        public static final String REFRESH = "refresh_token";
        public static final String ID_TOKEN = "id_token";
    }

    @Container
    KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile(TestRealm.REALM_CONFIGURATION).
            withAdminUsername(TestRealm.Administrator.NAME).withAdminPassword(TestRealm.Administrator.PASSWORD)
            .useTls();

    /**
     * Creates a map for post-requests containing all necessary parameters for retrieving an access- /id / refresh-token
     *
     * @param requestedScopes colon separated list of requested scopes
     */
    protected Map<String, String> parameterForScopedToken(String requestedScopes) {
        return Map.of(
                "username", TestRealm.TestUser.NAME,
                "password", TestRealm.TestUser.PASSWORD,
                "grant_type", "password",
                "scope", requestedScopes,
                "client_id", TestRealm.Client.ID,
                "client_secret", TestRealm.Client.PASSWORD
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

    /**
     * Resolves the Issuer-url for the configured realm.
     */
    protected String getIssuer() {
        return keycloak.getAuthServerUrl() + "/realms/" + TestRealm.REALM_NAME;
    }

}
