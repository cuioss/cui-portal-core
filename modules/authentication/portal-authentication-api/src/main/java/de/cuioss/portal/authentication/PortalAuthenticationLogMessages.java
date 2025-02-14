package de.cuioss.portal.authentication;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-authentication-api module.
 * All messages follow the format: PortalAuth-[identifier]: [message]
 * 
 * Message Identifiers by Package:
 * AUTH:
 *   - INFO:  001-099 (Login/Logout events)
 *   - WARN:  100-199 (Authentication warnings)
 *   - ERROR: 200-299 (Authentication failures)
 */
@UtilityClass
public final class PortalAuthenticationLogMessages {

    public static final String PREFIX = "PortalAuth";

    @UtilityClass
    public static final class AUTH {
        
        @UtilityClass
        public static final class INFO {
            public static final LogRecord LOGIN_SUCCESS = LogRecordModel.builder()
                    .template("User '%s' successfully logged in")
                    .prefix(PREFIX)
                    .identifier(1)
                    .build();

            public static final LogRecord LOGOUT = LogRecordModel.builder()
                    .template("User '%s' logged out")
                    .prefix(PREFIX)
                    .identifier(2)
                    .build();
        }

        @UtilityClass
        public static final class WARN {
            public static final LogRecord LOGIN_FAILED = LogRecordModel.builder()
                    .template("Login failed for user '%s'")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();

            public static final LogRecord INVALID_CREDENTIALS = LogRecordModel.builder()
                    .template("Invalid credentials provided for user '%s'")
                    .prefix(PREFIX)
                    .identifier(101)
                    .build();
        }

        @UtilityClass
        public static final class ERROR {
            public static final LogRecord AUTHENTICATION_ERROR = LogRecordModel.builder()
                    .template("Authentication error occurred: %s")
                    .prefix(PREFIX)
                    .identifier(200)
                    .build();
        }
    }
}
