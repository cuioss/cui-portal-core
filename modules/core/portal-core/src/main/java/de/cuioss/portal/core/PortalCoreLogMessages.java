package de.cuioss.portal.core;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-core module.
 * All messages follow the format: PortalCore-[identifier]: [message]
 */
@UtilityClass
public final class PortalCoreLogMessages {

    /**
     * Module prefix for all log messages
     */
    public static final String PREFIX = "PortalCore";

    /**
     * Error level message when request processing fails
     * Message: PortalCore-200: Could not process Request, due to %s
     * Parameters:
     * 1. Error reason
     */
    public static final LogRecord SERVLET_REQUEST_PROCESSING_ERROR = LogRecordModel.builder()
            .template("Could not process Request, due to %s")
            .prefix(PREFIX)
            .identifier(200)
            .build();

    /**
     * Warning level message when user authentication is required but missing
     * Message: PortalCore-100: Could not process Request, because the user must be logged in for this request
     */
    public static final LogRecord SERVLET_USER_NOT_LOGGED_IN = LogRecordModel.builder()
            .template("Could not process Request, because the user must be logged in for this request")
            .prefix(PREFIX)
            .identifier(100)
            .build();

    /**
     * Warning level message when user lacks required roles
     * Message: PortalCore-101: Could not process Request, because of the condition '%s' is not met for user '%s'
     * Parameters:
     * 1. Required roles description
     * 2. User information
     */
    public static final LogRecord SERVLET_USER_MISSING_ROLES = LogRecordModel.builder()
            .template("Could not process Request, because of the condition '%s' is not met for user '%s'")
            .prefix(PREFIX)
            .identifier(101)
            .build();

    /**
     * Debug level message when servlet is disabled
     * Message: PortalCore-500: Could not process Request, disabled by configuration
     */
    public static final LogRecord SERVLET_DISABLED_BY_CONFIGURATION = LogRecordModel.builder()
            .template("Could not process Request, disabled by configuration")
            .prefix(PREFIX)
            .identifier(500)
            .build();

    /**
     * Trace level message for precondition checking
     * Message: PortalCore-600: Checking call preconditions
     */
    public static final LogRecord SERVLET_CHECKING_PRECONDITIONS = LogRecordModel.builder()
            .template("Checking call preconditions")
            .prefix(PREFIX)
            .identifier(600)
            .build();

    /**
     * Trace level message for successful precondition check
     * Message: PortalCore-601: All preconditions are ok, generating payload
     */
    public static final LogRecord SERVLET_PRECONDITIONS_OK = LogRecordModel.builder()
            .template("All preconditions are ok, generating payload")
            .prefix(PREFIX)
            .identifier(601)
            .build();

    // Lifecycle Messages
    public static final LogRecord LIFECYCLE_CONTEXT_INITIALIZING = LogRecordModel.builder()
            .template("Initializing Context for %s")
            .prefix(PREFIX)
            .identifier(1)
            .build();

    public static final LogRecord LIFECYCLE_CONTEXT_SHUTDOWN = LogRecordModel.builder()
            .template("Shutting down '%s'")
            .prefix(PREFIX)
            .identifier(2)
            .build();

    public static final LogRecord LIFECYCLE_INITIALIZER_DEBUG = LogRecordModel.builder()
            .template("ServletLifecycleListener called for '%s', initializing with order: %s")
            .prefix(PREFIX)
            .identifier(500)
            .build();

    public static final LogRecord LIFECYCLE_INITIALIZING_COMPONENT = LogRecordModel.builder()
            .template("Initializing '%s' for '%s'")
            .prefix(PREFIX)
            .identifier(501)
            .build();

    public static final LogRecord LIFECYCLE_INITIALIZE_COMPLETE = LogRecordModel.builder()
            .template("Initialize successfully called for all elements for '%s'")
            .prefix(PREFIX)
            .identifier(502)
            .build();

    public static final LogRecord LIFECYCLE_DESTROYING = LogRecordModel.builder()
            .template("Executing applicationDestroyListener for '%s'")
            .prefix(PREFIX)
            .identifier(503)
            .build();

    public static final LogRecord LIFECYCLE_FINALIZER_DEBUG = LogRecordModel.builder()
            .template("ServletLifecycleListener called for '%s', finalizing with order: %s")
            .prefix(PREFIX)
            .identifier(504)
            .build();

    public static final LogRecord LIFECYCLE_DESTROYING_COMPONENT = LogRecordModel.builder()
            .template("Destroying '%s' for '%s'")
            .prefix(PREFIX)
            .identifier(505)
            .build();

    public static final LogRecord LIFECYCLE_DESTROY_ERROR = LogRecordModel.builder()
            .template("Error while destroying '%s' for '%s': %s")
            .prefix(PREFIX)
            .identifier(100)
            .build();

    public static final LogRecord LIFECYCLE_FINALIZE_COMPLETE = LogRecordModel.builder()
            .template("Finalize successfully called for all elements for '%s'")
            .prefix(PREFIX)
            .identifier(506)
            .build();

    // ExternalHostname Messages
    public static final LogRecord HOSTNAME_RESOLVED = LogRecordModel.builder()
            .template("Resolved hostname: %s")
            .prefix(PREFIX)
            .identifier(507)
            .build();
}
