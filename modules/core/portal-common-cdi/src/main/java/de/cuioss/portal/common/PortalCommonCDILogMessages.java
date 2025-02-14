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
 * Centralized log messages for the portal-common-cdi module.
 * All messages follow the format: PortalCommonCDI-[identifier]: [message]
 * Message Identifiers:
 * - 001-99: INFO
 * - 100-199: WARN
 * - 200-299: ERROR
 * - 300-399: FATAL
 * - 500-599: DEBUG
 * - 600-699: TRACE
 */
@UtilityClass
public final class PortalCommonCDILogMessages {

    public static final String PREFIX = "PortalCommonCDI";

    @UtilityClass
    public static final class BUNDLE {

        @UtilityClass
        public static final class WARN {
            public static final LogRecord KEY_NOT_FOUND = LogRecordModel.builder()
                    .template("No key '%s' defined within any of the configured bundles: %s")
                    .prefix(PREFIX)
                    .identifier(100)
                    .build();

            public static final LogRecord LOAD_FAILED = LogRecordModel.builder()
                    .template("Unable to load bundle '%s' for locale '%s': %s")
                    .prefix(PREFIX)
                    .identifier(101)
                    .build();

            public static final LogRecord DUPLICATE_PATH = LogRecordModel.builder()
                    .template("Duplicate resource path detected for %s")
                    .prefix(PREFIX)
                    .identifier(102)
                    .build();

            public static final LogRecord MISSING_PATH = LogRecordModel.builder()
                    .template("Ignoring bundle %s due to missing path")
                    .prefix(PREFIX)
                    .identifier(103)
                    .build();

            public static final LogRecord NO_VALID_BUNDLES = LogRecordModel.builder()
                    .template("No valid resource bundles found")
                    .prefix(PREFIX)
                    .identifier(104)
                    .build();
        }
    }

    @UtilityClass
    public static final class STAGE {

        @UtilityClass
        public static final class ERROR {
            public static final LogRecord INVALID_STAGE = LogRecordModel.builder()
                    .template("Invalid stage name '%s', falling back to %s")
                    .prefix(PREFIX)
                    .identifier(200)
                    .build();
        }
    }
}
