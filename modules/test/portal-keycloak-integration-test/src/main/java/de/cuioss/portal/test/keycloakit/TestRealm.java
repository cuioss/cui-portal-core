package de.cuioss.portal.test.keycloakit;

import lombok.experimental.UtilityClass;

/**
 * Provides Configuration for the test-realm as constants
 */
@UtilityClass
public class TestRealm {

    /**
     * The test-realm needed for configuring the keycloak
     */
    public static final String REALM_CONFIGURATION = "/oauth_integration_tests-realm.json";

    /**
     * The name of the test-realm
     */
    public static final String REALM_NAME = "oauth_integration_tests";

    public enum administrator {
        ;
        /**
         * The name of the realm-configured test-user.
         */
        public static final String NAME = "admin";

        /**
         * The password of the realm-configured test-user.
         */
        public static final String PASSWORD = "adminPass";
    }

    public enum client {
        ;
        /**
         * The id of the OIDC-Client.
         */
        public static final String ID = "test_client";

        /**
         * The secret of the OIDC-Client.
         */
        public static final String SECRET = "yTKslWLtf4giJcWCaoVJ20H8sy6STexM";
    }

    /**
     * Values for the realm-configured test-user
     */
    public enum testUser {

        ;
        /**
         * The name of the realm-configured test-user.
         */
        public static final String NAME = "testUser";

        /**
         * The password of the realm-configured test-user.
         */
        public static final String PASSWORD = "drowssap";

        /**
         * The email-address of the realm-configured test-user.
         */
        public static final String EMAIL = "testUser@example.com";

        /**
         * The first-name of the realm-configured test-user.
         */
        public static final String FIRST_NAME = "Test";

        /**
         * The last-name of the realm-configured test-user.
         */
        public static final String LAST_NAME = "User";
    }

    /**
     * Constants for the TLS-Configuration provided by the testcontainers-keycloak
     */
    public enum providedKeyStore {
        ;
        public static final String PASSWORD = "changeit";
        public static final String KEYSTORE_PATH = "/tls.jks";
    }


}
