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
     * Debug level message when adding a bundle
     * Message: PortalCommonCDI-003: Adding bundle %s
     * Parameters:
     * 1. Bundle path
     */
    public static final LogRecord ADDING_BUNDLE = LogRecordModel.builder()
            .template("Adding bundle %s")
            .prefix(PREFIX)
            .identifier(3)
            .build();

    /**
     * Debug level message showing resulting bundles
     * Message: PortalCommonCDI-004: Resulting bundles: %s
     * Parameters:
     * 1. Comma-separated list of bundle paths
     */
    public static final LogRecord RESULTING_BUNDLES = LogRecordModel.builder()
            .template("Resulting bundles: %s")
            .prefix(PREFIX)
            .identifier(4)
            .build();

    /**
     * Warn level message when a key is not found in any bundle
     * Message: PortalCommonCDI-505: No key '%s' defined within any of the configured bundles: %s
     * Parameters:
     * 1. Key
     * 2. Bundle paths
     */
    public static final LogRecord BUNDLE_KEY_NOT_FOUND = LogRecordModel.builder()
            .template("No key '%s' defined within any of the configured bundles: %s")
            .prefix(PREFIX)
            .identifier(505)
            .build();

    /**
     * Debug level message when locale changes
     * Message: PortalCommonCDI-006: Locale changed to '%s', clearing bundle cache
     * Parameters:
     * 1. New locale
     */
    public static final LogRecord LOCALE_CHANGED = LogRecordModel.builder()
            .template("Locale changed to '%s', clearing bundle cache")
            .prefix(PREFIX)
            .identifier(6)
            .build();

    /**
     * Debug level message when resolving bundles
     * Message: PortalCommonCDI-007: Resolved %d resource bundles for locale '%s'
     * Parameters:
     * 1. Number of bundles
     * 2. Locale
     */
    public static final LogRecord BUNDLES_RESOLVED = LogRecordModel.builder()
            .template("Resolved %d resource bundles for locale '%s'")
            .prefix(PREFIX)
            .identifier(7)
            .build();

    /**
     * Warning level message when a bundle fails to load
     * Message: PortalCommonCDI-501: Failed to load bundle %s '%s' for locale '%s'
     * Parameters:
     * 1. Class name
     * 2. Bundle path
     * 3. Locale
     */
    public static final LogRecord BUNDLE_LOAD_FAILED = LogRecordModel.builder()
            .template("Failed to load bundle %s '%s' for locale '%s'")
            .prefix(PREFIX)
            .identifier(501)
            .build();

    /**
     * Warning level message when a duplicate resource path is detected
     * Message: PortalCommonCDI-502: Duplicate resource path detected for %s
     * Parameters:
     * 1. Class name
     */
    public static final LogRecord DUPLICATE_RESOURCE_PATH = LogRecordModel.builder()
            .template("Duplicate resource path detected for %s")
            .prefix(PREFIX)
            .identifier(502)
            .build();

    /**
     * Warning level message when ignoring a bundle due to missing path
     * Message: PortalCommonCDI-503: Ignoring bundle %s due to missing path
     * Parameters:
     * 1. Class name
     */
    public static final LogRecord IGNORING_BUNDLE = LogRecordModel.builder()
            .template("Ignoring bundle %s due to missing path")
            .prefix(PREFIX)
            .identifier(503)
            .build();

    /**
     * Error message for unknown project stage configuration
     * Message: PortalCommonCDI-500: Unknown project stage '%s' detected! Set the property 'portal.configuration.stage' to one of: development, test, configuration, production
     * Parameters:
     * 1. Stage name
     */
    public static final LogRecord UNKNOWN_PROJECT_STAGE = LogRecordModel.builder()
            .template("Unknown project stage '%s' detected! Set the property 'portal.configuration.stage' to one of: development, test, configuration, production")
            .prefix(PREFIX)
            .identifier(500)
            .build();

    /**
     * Debug message when falling back to context classloader
     * Message: PortalCommonCDI-010: Simple class loading for resource '%s' and type '%s' did not work, using context-classloader
     * Parameters:
     * 1. Resource path
     * 2. Class name
     */
    public static final LogRecord RESOURCE_LOADER_FALLBACK = LogRecordModel.builder()
            .template("Simple class loading for resource '%s' and type '%s' did not work, using context-classloader")
            .prefix(PREFIX)
            .identifier(10)
            .build();

    /**
     * Debug message when resolving PortalResourceBundleBean
     * Message: PortalCommonCDI-011: Resolving PortalResourceBundleBean from CDI-Context
     */
    public static final LogRecord RESOLVING_BUNDLE_BEAN = LogRecordModel.builder()
            .template("Resolving PortalResourceBundleBean from CDI-Context")
            .prefix(PREFIX)
            .identifier(11)
            .build();

    /**
     * Warning level message when no valid bundles are found
     * Message: PortalCommonCDI-504: No valid resource bundles found
     */
    public static final LogRecord NO_VALID_BUNDLES = LogRecordModel.builder()
            .template("No valid resource bundles found")
            .prefix(PREFIX)
            .identifier(504)
            .build();
}
