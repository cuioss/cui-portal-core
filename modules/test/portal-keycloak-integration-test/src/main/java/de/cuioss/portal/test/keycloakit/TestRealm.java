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

        /**
         * The name of the realm-configured test-user.
         */
        public static final String NAME = "admin";

        /**
         * The password of the realm-configured test-user.
         */
        @SuppressWarnings("squid:S2068") // owolff: These credentials are for testing only
        public static final String PASSWORD = "adminPass";
    }

    public enum client {

        /**
         * The id of the OIDC-Client.
         */
        public static final String ID = "test_client";

        /**
         * The secret of the OIDC-Client.
         */
        @SuppressWarnings("squid:S2068") // owolff: These credentials are for testing only
        public static final String PASSWORD = "yTKslWLtf4giJcWCaoVJ20H8sy6STexM";
    }

    /**
     * Values for the realm-configured test-user
     */
    public enum testUser {


        /**
         * The name of the realm-configured test-user.
         */
        public static final String NAME = "testUser";

        /**
         * The password of the realm-configured test-user.
         */
        @SuppressWarnings("squid:S2068") // owolff: These credentials are for testing only
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

        @SuppressWarnings("squid:S2068") // owolff: These credentials are for testing only
        public static final String PASSWORD = "changeit";
        public static final String KEYSTORE_PATH = "/tls.jks";
        public static final String PUBLIC_CERT = "/tls.crt";
    }


}
