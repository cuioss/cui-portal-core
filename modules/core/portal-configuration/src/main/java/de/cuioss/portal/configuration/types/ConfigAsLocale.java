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
package de.cuioss.portal.configuration.types;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting configuration properties as {@link Locale} instances.
 * This qualifier provides type-safe injection of locale configurations with
 * flexible fallback options and comprehensive format support.
 * <p>
 * Features:
 * <ul>
 *   <li>Automatic locale parsing</li>
 *   <li>System locale fallback option</li>
 *   <li>Multiple format support</li>
 *   <li>Strict validation mode</li>
 *   <li>Null-safe operation</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with system fallback
 * &#64;Inject
 * &#64;ConfigAsLocale(name = "app.default.locale")
 * private Locale defaultLocale;
 * 
 * // Strict locale validation
 * &#64;Inject
 * &#64;ConfigAsLocale(
 *     name = "app.ui.locale",
 *     defaultToSystem = false
 * )
 * private Locale uiLocale;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Language only
 * app.default.locale=en
 * 
 * # Language and country with hyphen (BCP 47)
 * app.ui.locale=en-US
 * 
 * # Language and country with underscore
 * app.ui.locale=en_US
 * 
 * # Full locale with variant
 * app.ui.locale=en_US_WIN
 * </pre>
 * <p>
 * Supported Formats:
 * <ul>
 *   <li>ISO Language Code: "en", "de", "fr"</li>
 *   <li>BCP 47 Language Tags: "en-US", "de-DE", "zh-Hans-CN"</li>
 *   <li>Java Locale String: "en_US", "de_DE"</li>
 *   <li>Full Locale: "en_US_WIN", "th_TH_TH_#u-nu-thai"</li>
 * </ul>
 * <p>
 * Error Handling:
 * <ul>
 *   <li>Invalid format with defaultToSystem=true → Returns system locale</li>
 *   <li>Invalid format with defaultToSystem=false → Throws IllegalArgumentException</li>
 *   <li>Missing property with defaultToSystem=true → Returns system locale</li>
 *   <li>Missing property with defaultToSystem=false → Throws IllegalArgumentException</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsLocale {

    /**
     * The configuration property key that specifies the locale.
     * <p>
     * The value should be a string representing a valid locale in one of the
     * supported formats:
     * <ul>
     *   <li>ISO Language Code: "en"</li>
     *   <li>BCP 47 Language Tag: "en-US"</li>
     *   <li>Java Locale String: "en_US"</li>
     *   <li>Full Locale: "en_US_WIN"</li>
     * </ul>
     * <p>
     * Example configurations:
     * <pre>
     * app.locale=en          # English
     * app.locale=en-US      # US English (BCP 47)
     * app.locale=en_US      # US English (Java)
     * app.locale=en_US_WIN  # US English (Windows variant)
     * </pre>
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * Controls the fallback behavior when locale parsing fails.
     * <p>
     * When enabled (true):
     * <ul>
     *   <li>Returns system locale for invalid formats</li>
     *   <li>Returns system locale for missing properties</li>
     *   <li>Ensures a valid locale is always injected</li>
     * </ul>
     * When disabled (false):
     * <ul>
     *   <li>Throws IllegalArgumentException for invalid formats</li>
     *   <li>Throws IllegalArgumentException for missing properties</li>
     *   <li>Ensures strict configuration compliance</li>
     * </ul>
     *
     * @return {@code true} to use system locale as fallback,
     *         {@code false} for strict validation
     */
    @Nonbinding
    boolean defaultToSystem() default true;
}
