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
package de.cuioss.portal.configuration;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines the structured log messages for the Portal configuration module.
 * This class follows the CUI logging framework pattern, organizing messages
 * by severity level and providing unique identifiers for each message.
 * 
 * <h2>Message Structure</h2>
 * Messages are organized into categories by severity:
 * <ul>
 *   <li>INFO (001-099): Informational messages about normal operation</li>
 *   <li>WARN (100-199): Warning messages about potential issues</li>
 *   <li>ERROR (200-299): Error messages about operational failures</li>
 *   <li>FATAL (300-399): Critical system failures</li>
 * </ul>
 * 
 * <h2>Message Format</h2>
 * Each message follows the format:
 * <pre>
 * PortalConfig-NNN: Message text
 * </pre>
 * Where:
 * <ul>
 *   <li>PortalConfig: Module prefix</li>
 *   <li>NNN: Three-digit identifier</li>
 *   <li>Message text: The actual message, possibly with parameters</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * // Logging an info message
 * LOGGER.info(INFO.PROJECT_STAGE_PRODUCTION_DETECTED::format);
 * 
 * // Logging a warning with parameters
 * LOGGER.warn(WARN.INVALID_CONNECTION_TYPE.format(
 *     actualType,
 *     expectedTypes
 * ));
 * 
 * // Logging an error with exception
 * LOGGER.error(e, ERROR.CONFIGURATION_ERROR.format(
 *     configKey
 * ));
 * </pre>
 * 
 * <h2>Message Categories</h2>
 * <ul>
 *   <li>Project Stage Messages: System running mode</li>
 *   <li>Configuration Messages: Configuration loading and validation</li>
 *   <li>File Watch Messages: File system monitoring</li>
 *   <li>Connection Messages: Connection configuration and setup</li>
 *   <li>Locale Messages: Localization configuration</li>
 * </ul>
 * 
 * @author Oliver Wolff
 * @see de.cuioss.tools.logging.LogRecord
 * @see de.cuioss.tools.logging.LogRecordModel
 */
@UtilityClass
public class PortalConfigurationMessages {

    private static final String PREFIX = "PortalConfig";

    /**
     * Info level messages (001-099)
     */
    @UtilityClass
    public static final class INFO {
        /** Running in Production-Mode (001) */
        public static final LogRecord PROJECT_STAGE_PRODUCTION_DETECTED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(1)
                .template("Running in Production-Mode")
                .build();

        /** System configuration information (003) */
        public static final LogRecord SYSTEM_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(3)
                .template("JVM Configuration:\n%s")
                .build();

        /** Environment configuration information (004) */
        public static final LogRecord ENV_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(4)
                .template("Environment Configuration:\n%s")
                .build();

        /** Portal configuration information (005) */
        public static final LogRecord PORTAL_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(5)
                .template("Portal Configuration:\n%s")
                .build();

        /** Watching for file changes at path (020) */
        public static final LogRecord FILE_WATCH_STARTED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(20)
                .template("Watching for file changes at path: %s")
                .build();
    }

    /**
     * Warning level messages (100-199)
     */
    @UtilityClass
    public static final class WARN {
        /** Unable to determine AuthenticationType for a connection (100) */
        public static final LogRecord UNABLE_TO_DETERMINE_AUTH_TYPE = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(100)
                .template("Unable to determine AuthenticationType for connection='%s' and properties, returning AuthenticationType.NONE")
                .build();

        /** Development mode detected (101) */
        public static final LogRecord PROJECT_STAGE_DEVELOPMENT_DETECTED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(101)
                .template("Project stage 'development' detected. Set the property '" + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.")
                .build();

        /** Invalid connection type configuration (102) */
        public static final LogRecord INVALID_CONNECTION_TYPE = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(102)
                .template("Invalid configuration found for .type, actual '%s', expected one of '%s', defaulting to 'ConnectionType.UNDEFINED'")
                .build();

        /** Test mode detected (110) */
        public static final LogRecord PROJECT_STAGE_TEST_DETECTED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(110)
                .template("Project stage 'test' detected. Set the property '" + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.")
                .build();

        /** Invalid locale configuration (120) */
        public static final LogRecord INVALID_LOCALE = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(120)
                .template("Invalid configuration found for Locale: %s")
                .build();

        /** Invalid watch key (130) */
        public static final LogRecord INVALID_WATCH_KEY = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(130)
                .template("Invalid element found, watchKey='%s', ignoring")
                .build();

        /** Invalid path (140) */
        public static final LogRecord PATH_INVALID = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(140)
                .template("Path '%s' %s, therefore it can not be watched")
                .build();

        /** Unable to read file (141) */
        public static final LogRecord UNABLE_TO_READ_FILE = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(141)
                .template("Unable to read metadata for file %s")
                .build();

        /** Unable to read directory (142) */
        public static final LogRecord UNABLE_TO_READ_DIRECTORY = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(142)
                .template("Directory %s could not be read")
                .build();

        /** Unable to construct connection metadata (150) */
        public static final LogRecord UNABLE_TO_CONSTRUCT = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(150)
                .template("Unable to construct ConnectionMetadata, due to %s")
                .build();

        /** Missing base name (160) */
        public static final LogRecord MISSING_BASENAME = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(160)
                .template("Configuration setting for baseName is missing.")
                .build();

        /** Missing configuration (161) */
        public static final LogRecord MISSING_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(161)
                .template("Missing configuration for %s detected.")
                .build();

        /** Missing basic auth configuration (170) */
        public static final LogRecord MISSING_BASIC_AUTH_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(170)
                .template("Configuration for basic authentication is incomplete. Missing: %s")
                .build();

        /** Missing token configuration (171) */
        public static final LogRecord MISSING_TOKEN_CONFIG = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(171)
                .template("Configuration for token based authentication is incomplete. Missing: %s")
                .build();
    }

    /**
     * Error level messages (200-299)
     */
    @UtilityClass
    public static final class ERROR {
        /** Invalid number configuration (200) */
        public static final LogRecord INVALID_NUMBER = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(200)
                .template("Invalid content for '%s%s', expected a number but was '%s'")
                .build();

        /** Invalid time unit configuration (210) */
        public static final LogRecord INVALID_TIME_UNIT = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(210)
                .template("Invalid content for '%s%s', expected one of %s but was '%s'")
                .build();

        /** Error converting enum value (212) */
        public static final LogRecord ENUM_CONVERSION = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(212)
                .template("Could not convert input value '%s' to enum of type: %s. Reason: %s")
                .build();

        /** Invalid boolean configuration (220) */
        public static final LogRecord INVALID_BOOLEAN = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(220)
                .template("Invalid content for '%s%s', expected a boolean but was '%s'")
                .build();

        /** Invalid number value (230) */
        public static final LogRecord INVALID_NUMBER_VALUE = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(230)
                .template("Invalid content for '%s', expected a number but was '%s'")
                .build();

        /** File system access error (240) */
        public static final LogRecord FILE_SYSTEM_ACCESS_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(240)
                .template("Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature")
                .build();

        /** File system polling error (241) */
        public static final LogRecord FILE_SYSTEM_POLLING_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(241)
                .template("Error while polling / accessing the file-system")
                .build();

        /** File event handling error (242) */
        public static final LogRecord FILE_EVENT_HANDLING_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(242)
                .template("Handling fileChangedEvent failed for file %s")
                .build();

        /** Connection error (250) */
        public static final LogRecord CONNECTION_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(250)
                .template("Connection error for %s: %s")
                .build();

        /** Connection timeout error (251) */
        public static final LogRecord CONNECTION_TIMEOUT_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(251)
                .template("Connection timeout for %s: %s")
                .build();

        /** Connection refused error (252) */
        public static final LogRecord CONNECTION_REFUSED_ERROR = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(252)
                .template("Connection refused for %s: %s")
                .build();

        /** Missing connection metadata (260) */
        public static final LogRecord MISSING_CONNECTION_METADATA = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(260)
                .template("Missing connection metadata for %s")
                .build();

        /** Invalid connection metadata (261) */
        public static final LogRecord INVALID_CONNECTION_METADATA = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(261)
                .template("Invalid connection metadata for %s")
                .build();

        /** Unable to schedule path (270) */
        public static final LogRecord UNABLE_TO_SCHEDULE_PATH = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(270)
                .template("Unable to schedule given Path for tracking for changes, due to '%s'")
                .build();

        /** SSL context creation failed (280) */
        public static final LogRecord SSL_CONTEXT_CREATION_FAILED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(280)
                .template("Unable to create SSLContext for connection '%s', due to '%s', defaulting to default ssl configuration")
                .build();
    }
}
