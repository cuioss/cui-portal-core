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
package de.cuioss.portal.metrics;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;

/**
 * Defines the log messages for the portal metrics module.
 */
public class PortalMetricsLogMessages {

    private static final String PREFIX = "Portal-Metrics";

    public static final LogRecord METRICS_APP_NAME = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(1)
            .template("Metrics App-Name: %s")
            .build();

    public static final LogRecord CACHE_METRICS_REGISTERED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(2)
            .template("Registered %s metrics for cache '%s'")
            .build();

    public static final LogRecord CACHE_METRICS_REMOVED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(3)
            .template("Removed metrics for cache '%s'")
            .build();
}
