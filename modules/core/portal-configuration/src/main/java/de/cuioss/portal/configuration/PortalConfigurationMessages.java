package de.cuioss.portal.configuration;

import static de.cuioss.tools.string.MoreStrings.lenientFormat;

import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines the log messages for the portal configuration module.
 */
@UtilityClass
public class PortalConfigurationMessages {

    private static final String PREFIX = "PortalConfig";

    @UtilityClass
    public static final class INFO {
        public static final LogRecord PROJECT_STAGE_PRODUCTION_DETECTED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(1)
            .template("Running in Production-Mode")
            .build();

        public static final LogRecord FILE_WATCH_STARTED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(10)  // Portal-010
            .template("Watching for file changes at path: %s")
            .build();
    }

    @UtilityClass
    public static final class WARN {
        public static final LogRecord PROJECT_STAGE_DEVELOPMENT_DETECTED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(100)
            .template("Project stage 'development' detected. Set the property '" + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.")
            .build();

        public static final LogRecord PROJECT_STAGE_TEST_DETECTED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(101)
            .template("Project stage 'test' detected. Set the property '" + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.")
            .build();

        public static final LogRecord INVALID_LOCALE = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(102)
            .template("Invalid configuration found for Locale: %s")
            .build();

        public static final LogRecord INVALID_WATCH_KEY = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(103)
            .template("Invalid element found, watchKey='%s', ignoring")
            .build();

        public static final LogRecord PATH_INVALID = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(142)  // Portal-142
            .template("Path '%s' %s, therefore it can not be watched")
            .build();

        public static final LogRecord UNABLE_TO_READ_FILE = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(143)
            .template("Unable to read metadata for file %s")
            .build();

        public static final LogRecord UNABLE_TO_READ_DIRECTORY = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(144)
            .template("Directory %s could not be read")
            .build();
    }

    @UtilityClass
    public static final class ERROR {
        public static final LogRecord INVALID_CONTENT_FOR_LONG = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(200)
            .template("Invalid content for '%s%s', expected a number but was '%s'")
            .build();

        public static final LogRecord INVALID_CONTENT_FOR_TIME_UNIT = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(201)
            .template("Invalid content for '%s%s', expected one of %s but was '%s'")
            .build();

        public static final LogRecord INVALID_CONTENT_FOR_BOOLEAN = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(202)
            .template("Invalid content for '%s%s', expected a boolean but was '%s'")
            .build();

        public static final LogRecord INVALID_NUMBER = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(206)
            .template("Invalid content for '%s', expected a number but was '%s'")
            .build();

        public static final LogRecord UNABLE_TO_SCHEDULE_PATH = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(517)  // Portal-517
            .template("Unable to schedule given Path for tracking for changes, due to '%s'")
            .build();

        public static final LogRecord FILE_SYSTEM_ACCESS_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(203)
            .template("Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature")
            .build();

        public static final LogRecord FILE_SYSTEM_POLLING_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(204)
            .template("Error while polling / accessing the file-system")
            .build();

        public static final LogRecord FILE_EVENT_HANDLING_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(205)
            .template("Handling fileChangedEvent failed for file %s")
            .build();

        public static final LogRecord CONNECTION_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(207)
            .template("Error while connecting to %s")
            .build();

        public static final LogRecord CONNECTION_TIMEOUT_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(208)
            .template("Timeout while connecting to %s")
            .build();

        public static final LogRecord CONNECTION_REFUSED_ERROR = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(209)
            .template("Connection to %s refused")
            .build();
    }

    @UtilityClass
    public static final class DEBUG {
        public static final LogRecord CONFIG_VALUE_NOT_FOUND = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(500)
            .template("Could not resolve config value for key %s")
            .build();
    }

    @UtilityClass
    public static final class TRACE {
        public static final LogRecord CONFIG_PROPERTIES = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(600)
            .template("configProperties (%s): %s")
            .build();

        public static final LogRecord RECORD_STATS = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(601)
            .template("recordStats: %s")
            .build();

        public static final LogRecord CACHE_CONFIG = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(602)
            .template("CacheConfig: %s")
            .build();
    }
}
