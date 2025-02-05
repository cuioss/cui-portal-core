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
 * Debug level messages use 1-500 range
 * Warn level messages use 501-999 range
 */
@UtilityClass
public final class PortalCommonCDILogMessages {

    /**
     * Module prefix for all log messages
     */
    public static final String PREFIX = "PortalCommonCDI";

    /**
     * Debug level message when a ResourceBundle path is not defined for a class
     * Message: Portal-001: ResourceBundle path not defined for class: %s
     */
    public static final LogRecord BUNDLE_PATH_NOT_DEFINED = LogRecordModel.builder()
            .template("ResourceBundle path not defined for class: %s")
            .prefix(PREFIX)
            .identifier(1)
            .build();

    /**
     * Debug level message when a ResourceBundle is successfully loaded
     * Message: PortalCommonCDI-002: Successfully loaded %s '%s' for locale '%s'
     * Parameters:
     * 1. Class name
     * 2. Bundle path
     * 3. Locale
     */
    public static final LogRecord BUNDLE_LOADED = LogRecordModel.builder()
            .template("Successfully loaded %s '%s' for locale '%s'")
            .prefix(PREFIX)
            .identifier(2)
            .build();

    /**
     * Debug level message when a ResourceBundle fails to load
     * Message: PortalCommonCDI-003: Unable to load %s '%s' for locale '%s'
     * Parameters:
     * 1. Class name
     * 2. Bundle path
     * 3. Locale
     */
    public static final LogRecord BUNDLE_LOAD_FAILED = LogRecordModel.builder()
            .template("Unable to load %s '%s' for locale '%s'")
            .prefix(PREFIX)
            .identifier(3)
            .build();

}
