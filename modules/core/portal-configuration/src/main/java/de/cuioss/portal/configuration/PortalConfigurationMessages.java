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
