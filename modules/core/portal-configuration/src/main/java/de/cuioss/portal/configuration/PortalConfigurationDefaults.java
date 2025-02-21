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

import de.cuioss.portal.common.bundle.ResourceBundleWrapper;
import lombok.experimental.UtilityClass;

/**
 * Defines default values for the Portal configuration system. These defaults
 * provide sensible starting points and fallback values for various configuration
 * settings.
 * 
 * <h2>Purpose</h2>
 * <ul>
 *   <li>Provide default configuration values</li>
 *   <li>Ensure consistent fallback behavior</li>
 *   <li>Document standard configuration values</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * These defaults can be used in several ways:
 * <pre>
 * // Direct reference in code
 * String bundlePath = PortalConfigurationDefaults.CUSTOM_BUNDLE_PATH;
 * 
 * // As configuration default
 * &#64;Inject
 * &#64;ConfigProperty(name = "some.key", 
 *                  defaultValue = PortalConfigurationDefaults.CUSTOM_BUNDLE_PATH)
 * private String configValue;
 * </pre>
 * 
 * <h2>Customization</h2>
 * While these defaults provide a starting point, they can be overridden through:
 * <ul>
 *   <li>Application configuration files</li>
 *   <li>Environment variables</li>
 *   <li>System properties</li>
 * </ul>
 * 
 * @author Oliver Wolff
 * @see PortalConfigurationKeys
 */
@UtilityClass
public class PortalConfigurationDefaults {

    /**
     * Path to an optional resource bundle that can be used to override or extend
     * the Portal's built-in messages. This bundle will be integrated into the
     * Portal's unified {@link ResourceBundleWrapper}.
     * 
     * <h3>Usage</h3>
     * <pre>
     * # Application properties
     * portal.resource.bundle.path=i18n.custom-messages
     * 
     * # Bundle structure
     * i18n/
     *   custom-messages_en.properties
     *   custom-messages_de.properties
     * </pre>
     * 
     * Any messages defined in this bundle will take precedence over the Portal's
     * default messages, allowing for customization without modifying core files.
     */
    public static final String CUSTOM_BUNDLE_PATH = "i18n.custom-messages";

}
