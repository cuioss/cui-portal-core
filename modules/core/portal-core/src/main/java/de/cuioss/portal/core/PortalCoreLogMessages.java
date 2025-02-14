package de.cuioss.portal.core;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-core module using a DSL-style approach.
 * Messages are organized by package, then by log level, making them easily discoverable
 * and maintainable.
 * <p>
 * Message Identifiers by Package:
 * SERVLET:
 * - DEBUG: 500-509
 * - WARN:  100-109
 * - ERROR: 200-209
 * - TRACE: 600-609
 * <p>
 * LIFECYCLE:
 * - INFO:  001-009
 * - DEBUG: 510-519
 * - WARN:  110-119
 * <p>
 * HOSTNAME:
 * - DEBUG: 520-529
 */
@UtilityClass
public final class PortalCoreLogMessages {

    private static final String PREFIX = "PortalCore";

    @UtilityClass
    public static final class SERVLET {
        @UtilityClass
        public static final class ERROR {

            public static final LogRecord REQUEST_PROCESSING_ERROR = LogRecordModel.builder()
                    .template("Could not process Request, due to %s")
                    .prefix(PREFIX)
                    .identifier(200)
                    .build();
        }

        @UtilityClass
        public static final class WARN {
            public static final LogRecord PORTAL_CORE_100 = LogRecordModel.builder()
                    .template("Could not process Request, because the user must be logged in for this request")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();

            public static final LogRecord PORTAL_CORE_101 = LogRecordModel.builder()
                    .template("Could not process Request, because of the condition '%s' is not met for user '%s'")
                    .prefix(PREFIX)
                    .identifier(101)
                    .build();

            public static final LogRecord USER_NOT_LOGGED_IN = LogRecordModel.builder()
                    .template("Could not process Request, because the user must be logged in for this request")
                    .prefix(PREFIX)
                    .identifier(102)
                    .build();

            public static final LogRecord USER_MISSING_ROLES = LogRecordModel.builder()
                    .template("Could not process Request, because of the condition '[%s]' is not met for user '%s'")
                    .prefix(PREFIX)
                    .identifier(103)
                    .build();
        }
    }

    @UtilityClass
    public static final class LIFECYCLE {
        @UtilityClass
        public static final class INFO {
            public static final LogRecord CONTEXT_INITIALIZING = LogRecordModel.builder()
                    .template("Initializing Context for %s")
                    .prefix(PREFIX)
                    .identifier(1)
                    .build();

            public static final LogRecord CONTEXT_SHUTDOWN = LogRecordModel.builder()
                    .template("Shutting down '%s'")
                    .prefix(PREFIX)
                    .identifier(2)
                    .build();
        }

        @UtilityClass
        public static final class WARN {

            public static final LogRecord DESTROY_ERROR = LogRecordModel.builder()
                    .template("Error during servlet context destroy")
                    .prefix(PREFIX)
                    .identifier(110)
                    .build();
        }
    }
}
