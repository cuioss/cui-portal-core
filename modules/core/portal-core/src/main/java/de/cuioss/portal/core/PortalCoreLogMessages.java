package de.cuioss.portal.core;

import de.cuioss.tools.logging.LogLevel;
import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-core module using a DSL-style approach.
 * Messages are organized by package, then by log level, making them easily discoverable
 * and maintainable.
 * 
 * Usage example:
 * PortalCoreLogMessages.SERVLET.INFO.REQUEST_PROCESSING_ERROR.format(error)
 * PortalCoreLogMessages.LIFECYCLE.DEBUG.CONTEXT_INITIALIZING.format(contextPath)
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
            public static final LogRecord USER_NOT_LOGGED_IN = LogRecordModel.builder()
                    .template("Could not process Request, because the user must be logged in for this request")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();

            public static final LogRecord USER_MISSING_ROLES = LogRecordModel.builder()
                    .template("Could not process Request, because of the condition '%s' is not met for user '%s'")
                    .prefix(PREFIX)
                    .identifier(101)
                    .build();
        }

        @UtilityClass
        public static final class DEBUG {
            public static final LogRecord DISABLED_BY_CONFIGURATION = LogRecordModel.builder()
                    .template("Could not process Request, disabled by configuration")
                    .prefix(PREFIX)
                    .identifier(500)
                    .build();
        }

        @UtilityClass
        public static final class TRACE {
            public static final LogRecord CHECKING_PRECONDITIONS = LogRecordModel.builder()
                    .template("Checking call preconditions")
                    .prefix(PREFIX)
                    .identifier(600)
                    .build();

            public static final LogRecord PRECONDITIONS_OK = LogRecordModel.builder()
                    .template("All preconditions are ok, generating payload")
                    .prefix(PREFIX)
                    .identifier(601)
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
        public static final class DEBUG {
            public static final LogRecord INITIALIZER_DEBUG = LogRecordModel.builder()
                    .template("ServletLifecycleListener called for '%s', initializing with order: %s")
                    .prefix(PREFIX)
                    .identifier(500)
                    .build();

            public static final LogRecord INITIALIZING_COMPONENT = LogRecordModel.builder()
                    .template("Initializing '%s' for '%s'")
                    .prefix(PREFIX)
                    .identifier(501)
                    .build();

            public static final LogRecord INITIALIZE_COMPLETE = LogRecordModel.builder()
                    .template("Initialize successfully called for all elements for '%s'")
                    .prefix(PREFIX)
                    .identifier(502)
                    .build();

            public static final LogRecord DESTROYING = LogRecordModel.builder()
                    .template("Executing applicationDestroyListener for '%s'")
                    .prefix(PREFIX)
                    .identifier(503)
                    .build();

            public static final LogRecord FINALIZER_DEBUG = LogRecordModel.builder()
                    .template("ServletLifecycleListener called for '%s', finalizing with order: %s")
                    .prefix(PREFIX)
                    .identifier(504)
                    .build();

            public static final LogRecord DESTROYING_COMPONENT = LogRecordModel.builder()
                    .template("Destroying '%s' for '%s'")
                    .prefix(PREFIX)
                    .identifier(505)
                    .build();

            public static final LogRecord FINALIZE_COMPLETE = LogRecordModel.builder()
                    .template("Finalize successfully called for all elements for '%s'")
                    .prefix(PREFIX)
                    .identifier(506)
                    .build();
        }

        @UtilityClass
        public static final class WARN {
            public static final LogRecord DESTROY_ERROR = LogRecordModel.builder()
                    .template("Error while destroying '%s' for '%s': %s")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();
        }
    }

    @UtilityClass
    public static final class HOSTNAME {
        @UtilityClass
        public static final class DEBUG {
            public static final LogRecord RESOLVED = LogRecordModel.builder()
                    .template("Resolved hostname: %s")
                    .prefix(PREFIX)
                    .identifier(507)
                    .build();
        }
    }
}
