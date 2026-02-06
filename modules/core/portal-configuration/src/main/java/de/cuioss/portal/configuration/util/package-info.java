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
/**
 * Provides utility classes and helper components for the Portal Configuration module.
 * These utilities handle common configuration tasks such as property filtering,
 * placeholder resolution, and configuration value processing.
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Configuration Helpers</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigurationHelper} - Core utilities
 *       for configuration handling, property filtering, and value resolution</li>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigurationPlaceholderHelper} - 
 *       Handles placeholder resolution in configuration values</li>
 * </ul>
 * 
 * <h3>Placeholder Support</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigPlaceholder} - Represents
 *       configuration placeholders with optional default values</li>
 *   <li>Format: {@code ${key}} or {@code ${key:defaultValue}}</li>
 *   <li>Supports nested placeholders up to 5 levels deep</li>
 * </ul>
 * 
 * <h3>Exception Handling</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigKeyNestingException} - 
 *       Thrown when placeholder nesting exceeds the maximum depth</li>
 * </ul>
 * 
 * <h2>Common Use Cases</h2>
 * <ul>
 *   <li>Filtering configuration properties by prefix</li>
 *   <li>Resolving placeholders in configuration values</li>
 *   <li>Processing configuration values with defaults</li>
 *   <li>Handling environment variable references</li>
 * </ul>
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.util;
