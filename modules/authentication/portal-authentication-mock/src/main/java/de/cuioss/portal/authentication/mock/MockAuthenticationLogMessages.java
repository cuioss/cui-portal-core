package de.cuioss.portal.authentication.mock;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines the log messages for the mock authentication module
 */
@UtilityClass
public class MockAuthenticationLogMessages {

    private static final String PREFIX = "MOCK-AUTH";

    public static final class INFO {

        private INFO() {
        }

        /** User '%s' successfully logged in */
        public static final LogRecord USER_LOGIN = LogRecordModel.builder()
                .template("User '%s' successfully logged in")
                .prefix(PREFIX)
                .identifier(1)
                .build();

        /** User '%s' logged out */
        public static final LogRecord USER_LOGOUT = LogRecordModel.builder()
                .template("User '%s' logged out")
                .prefix(PREFIX)
                .identifier(2)
                .build();

        /** Retrieved authentication context for user '%s' */
        public static final LogRecord RETRIEVED_CONTEXT = LogRecordModel.builder()
                .template("Retrieved authentication context for user '%s'")
                .prefix(PREFIX)
                .identifier(3)
                .build();
    }

    public static final class WARN {

        private WARN() {
        }

        /** Invalid login attempt for user '%s' */
        public static final LogRecord INVALID_LOGIN = LogRecordModel.builder()
                .template("Invalid login attempt for user '%s'")
                .prefix(PREFIX)
                .identifier(101)
                .build();
    }

    public static final class DEBUG {

        private DEBUG() {
        }

        /** Created default user info builder with roles=%s, groups=%s */
        public static final LogRecord DEFAULT_USER_INFO = LogRecordModel.builder()
                .template("Created default user info builder with roles=%s, groups=%s")
                .prefix(PREFIX)
                .identifier(501)
                .build();
    }
}
