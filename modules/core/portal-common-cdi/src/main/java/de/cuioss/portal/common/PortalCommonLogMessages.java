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
package de.cuioss.portal.common;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines structured log messages for the portal-common-cdi module using the
 * {@link LogRecord} API.
 * 
 * <h2>Message Format</h2>
 * All messages follow the format: {@code PortalCommon-[identifier]: [message]}
 * 
 * <h2>Message Categories</h2>
 * Messages are categorized by log level with specific identifier ranges:
 * <ul>
 *   <li>001-099: INFO level messages</li>
 *   <li>100-199: WARN level messages</li>
 *   <li>200-299: ERROR level messages</li>
 *   <li>300-399: FATAL level messages</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>
 * // For messages without parameters
 * LOGGER.warn(WARN.EMPTY_KEY::format);
 * 
 * // For messages with parameters
 * LOGGER.warn(WARN.KEY_NOT_FOUND.format(key, bundles));
 * 
 * // For messages with exception
 * LOGGER.error(exception, ERROR.BUNDLE_LOAD.format(bundle, locale));
 * </pre>
 * 
 * @see de.cuioss.tools.logging.LogRecord
 */
@UtilityClass
public final class PortalCommonLogMessages {

    /** Prefix used for all log messages in this module */
    public static final String PREFIX = "PortalCommon";

    /**
     * Warning level messages with identifiers 100-199.
     */
    @UtilityClass
    public static final class WARN {
        /** 
         * Logged when a requested resource bundle key is not found.
         * Parameters: key name, available bundles
         */
        public static final LogRecord KEY_NOT_FOUND = LogRecordModel.builder()
                .template("No key '%s' defined within any of the configured bundles: %s")
                .prefix(PREFIX)
                .identifier(100)
                .build();

        /** 
         * Logged when a bundle cannot be loaded for a specific locale.
         * Parameters: bundle name, locale, error message
         */
        public static final LogRecord LOAD_FAILED = LogRecordModel.builder()
                .template("Unable to load bundle '%s' for locale '%s': %s")
                .prefix(PREFIX)
                .identifier(101)
                .build();

        /** 
         * Logged when a duplicate resource path is detected.
         * Parameters: resource path
         */
        public static final LogRecord DUPLICATE_PATH = LogRecordModel.builder()
                .template("Duplicate resource path detected for %s")
                .prefix(PREFIX)
                .identifier(102)
                .build();

        /** 
         * Logged when a bundle is ignored due to a missing path.
         * Parameters: bundle name
         */
        public static final LogRecord MISSING_PATH = LogRecordModel.builder()
                .template("Ignoring bundle %s due to missing path")
                .prefix(PREFIX)
                .identifier(103)
                .build();

        /** 
         * Logged when no valid resource bundles are found.
         */
        public static final LogRecord NO_VALID_BUNDLES = LogRecordModel.builder()
                .template("No valid resource bundles found")
                .prefix(PREFIX)
                .identifier(104)
                .build();

        /** 
         * Logged when an empty key is ignored.
         */
        public static final LogRecord BUNDLE_IGNORING_EMPTY_KEY = LogRecordModel.builder()
                .template("Ignoring Empty Key")
                .prefix(PREFIX)
                .identifier(105)
                .build();
    }

    /**
     * Error level messages with identifiers 200-299.
     */
    @UtilityClass
    public static final class ERROR {
        /** 
         * Logged when an invalid stage name is encountered.
         * Parameters: invalid stage name, fallback stage name
         */
        public static final LogRecord INVALID_STAGE = LogRecordModel.builder()
                .template("Invalid stage name '%s', falling back to %s")
                .prefix(PREFIX)
                .identifier(200)
                .build();
    }
}
