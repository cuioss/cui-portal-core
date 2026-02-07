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
package de.cuioss.portal.authentication;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-authentication-api module.
 * All messages follow the format: PortalAuth-[identifier]: [message]
 * 
 * <h2>Message Categories</h2>
 * <h3>AUTH Category - Authentication Events</h3>
 * <ul>
 *   <li>INFO (001-099): Login/Logout events</li>
 *   <li>WARN (100-199): Authentication warnings</li>
 *   <li>ERROR (200-299): Authentication failures</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>
 * import static de.cuioss.portal.authentication.PortalAuthenticationLogMessages.AUTH;
 * 
 * // With parameters:
 * LOGGER.info(AUTH.INFO.LOGIN_SUCCESS.format(username));
 * LOGGER.warn(AUTH.WARN.LOGIN_FAILED.format(username));
 * 
 * // With exception:
 * LOGGER.error(exception, AUTH.ERROR.AUTHENTICATION_ERROR.format(errorMessage));
 * </pre>
 */
@UtilityClass
public final class PortalAuthenticationLogMessages {

    public static final String PREFIX = "PortalAuth";

    @UtilityClass
    public static final class AUTH {

        @UtilityClass
        public static final class INFO {
            /**
             * ID: 001
             * Logged when a user successfully authenticates.
             * Parameter: username
             */
            public static final LogRecord LOGIN_SUCCESS = LogRecordModel.builder()
                    .template("User '%s' successfully logged in")
                    .prefix(PREFIX)
                    .identifier(1)
                    .build();

            /**
             * ID: 002
             * Logged when a user logs out.
             * Parameter: username
             */
            public static final LogRecord LOGOUT = LogRecordModel.builder()
                    .template("User '%s' logged out")
                    .prefix(PREFIX)
                    .identifier(2)
                    .build();
        }

        @UtilityClass
        public static final class WARN {
            /**
             * ID: 100
             * Logged when a login attempt fails.
             * Parameter: username
             */
            public static final LogRecord LOGIN_FAILED = LogRecordModel.builder()
                    .template("Login failed for user '%s'")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();

            /**
             * ID: 101
             * Logged when invalid credentials are provided.
             * Parameter: username
             */
            public static final LogRecord INVALID_CREDENTIALS = LogRecordModel.builder()
                    .template("Invalid credentials provided for user '%s'")
                    .prefix(PREFIX)
                    .identifier(101)
                    .build();
        }

        @UtilityClass
        public static final class ERROR {
            /**
             * ID: 200
             * Logged when a general authentication error occurs.
             * Parameter: error message
             */
            public static final LogRecord AUTHENTICATION_ERROR = LogRecordModel.builder()
                    .template("Authentication error occurred: %s")
                    .prefix(PREFIX)
                    .identifier(200)
                    .build();
        }
    }
}
