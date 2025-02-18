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
package de.cuioss.portal.core;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log message management for the portal-core module. This utility class
 * provides a structured approach to handling log messages, ensuring consistency
 * and maintainability across the portal.
 *
 * <p><strong>Message organization:</strong></p>
 * <ul>
 *   <li>Messages are grouped by component (SERVLET, LIFECYCLE, etc.)</li>
 *   <li>Each group contains subgroups by log level (ERROR, WARN, INFO, DEBUG)</li>
 *   <li>Each message has a unique identifier within its group</li>
 * </ul>
 *
 * <p><strong>Message identifiers by package:</strong></p>
 * <pre>
 * SERVLET:
 *   DEBUG: 500-509
 *   WARN:  100-109
 *   ERROR: 200-209
 *   TRACE: 600-609
 *
 * LIFECYCLE:
 *   INFO:  001-009
 *   DEBUG: 510-519
 *   WARN:  110-119
 *
 * HOSTNAME:
 *   DEBUG: 520-529
 * </pre>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * LOGGER.error(SERVLET.ERROR.REQUEST_PROCESSING_ERROR.format(e.getMessage()));
 * LOGGER.info(LIFECYCLE.INFO.CONTEXT_INITIALIZING.format(contextPath));
 * </pre>
 *
 * <p><strong>Implementation notes:</strong></p>
 * <ul>
 *   <li>All messages use the "PortalCore" prefix</li>
 *   <li>Messages support parameterized formatting</li>
 *   <li>Each message has a unique numeric identifier</li>
 *   <li>Messages are immutable once created</li>
 * </ul>
 *
 * @see LogRecord
 * @see LogRecordModel
 * @since 1.0
 */
@UtilityClass
public final class PortalCoreLogMessages {

    private static final String PREFIX = "PortalCore";

    /**
     * Servlet-related log messages for tracking request processing, authentication,
     * and authorization events. Messages are organized by severity level.
     *
     * <p><strong>Message ranges:</strong></p>
     * <ul>
     *   <li>ERROR (200-209): Critical processing failures</li>
     *   <li>WARN (100-109): Authentication and authorization issues</li>
     *   <li>DEBUG (500-509): Request processing details</li>
     *   <li>TRACE (600-609): Detailed request tracing</li>
     * </ul>
     *
     * @since 1.0
     */
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

    /**
     * Lifecycle-related log messages for tracking application startup, shutdown,
     * and initialization events. Messages are organized by severity level.
     *
     * <p><strong>Message ranges:</strong></p>
     * <ul>
     *   <li>INFO (001-009): Normal lifecycle events</li>
     *   <li>DEBUG (510-519): Detailed initialization steps</li>
     *   <li>WARN (110-119): Non-critical lifecycle issues</li>
     * </ul>
     *
     * @since 1.0
     */
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

    /**
     * Hostname-related log messages for tracking hostname resolution and
     * configuration events. Messages are organized by severity level.
     *
     * <p><strong>Message ranges:</strong></p>
     * <ul>
     *   <li>DEBUG (520-529): Hostname resolution details</li>
     * </ul>
     *
     * @since 1.0
     */
    @UtilityClass
    public static final class HOSTNAME {
        // No messages defined yet
    }
}
