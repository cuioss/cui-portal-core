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
package de.cuioss.portal.metrics;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines the log messages for the portal metrics module.
 * All messages follow the format: Portal-Metrics-[identifier]: [message]
 * 
 * <h2>Message Categories</h2>
 * <ul>
 *   <li>INFO (001-099): General metrics operations</li>
 *   <li>WARN (100-199): Warning conditions</li>
 *   <li>ERROR (200-299): Error conditions</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>
 * import static de.cuioss.portal.metrics.PortalMetricsLogMessages.INFO;
 * 
 * // With parameters:
 * LOGGER.info(INFO.METRICS_APP_NAME.format(appName));
 * LOGGER.info(INFO.CACHE_METRICS_REGISTERED.format(count, cacheName));
 * </pre>
 */
@UtilityClass
public class PortalMetricsLogMessages {

    private static final String PREFIX = "Portal-Metrics";

    @UtilityClass
    public static final class INFO {
        /**
         * ID: 001
         * Logged when the metrics application name is resolved.
         * Parameter: application name
         */
        public static final LogRecord METRICS_APP_NAME = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(1)
                .template("Metrics App-Name: %s")
                .build();

        /**
         * ID: 002
         * Logged when cache metrics are registered.
         * Parameters: 
         * 1. Number of metrics registered
         * 2. Cache name
         */
        public static final LogRecord CACHE_METRICS_REGISTERED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(2)
                .template("Registered %s metrics for cache '%s'")
                .build();

        /**
         * ID: 003
         * Logged when cache metrics are removed.
         * Parameter: cache name
         */
        public static final LogRecord CACHE_METRICS_REMOVED = LogRecordModel.builder()
                .prefix(PREFIX)
                .identifier(3)
                .template("Removed metrics for cache '%s'")
                .build();
    }
}
