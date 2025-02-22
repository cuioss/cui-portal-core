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

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting a list of {@link Locale} instances from configuration.
 * This qualifier enables injection of multiple locale settings from a single
 * configuration property, with support for fallback to system locale and
 * custom separators.
 * <p>
 * Features:
 * <ul>
 *   <li>Multiple locale injection</li>
 *   <li>System locale fallback</li>
 *   <li>Configurable separator</li>
 *   <li>Flexible locale format support</li>
 *   <li>Returns immutable list</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with system fallback
 * &#64;Inject
 * &#64;ConfigAsLocaleList(name = "app.supported.locales")
 * private List&lt;Locale&gt; supportedLocales;
 * 
 * // Strict parsing with custom separator
 * &#64;Inject
 * &#64;ConfigAsLocaleList(
 *     name = "app.available.languages",
 *     defaultToSystem = false,
 *     separator = '|'
 * )
 * private List&lt;Locale&gt; availableLanguages;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Using default separator (,)
 * app.supported.locales=en,de,fr,es
 * 
 * # Using language tags
 * app.supported.locales=en-US,de-DE,fr-FR
 * 
 * # Using custom separator (|)
 * app.available.languages=en_US|de_DE|fr_FR
 * </pre>
 * <p>
 * Supported Locale Formats:
 * <ul>
 *   <li>Language codes: "en", "de", "fr"</li>
 *   <li>Language tags: "en-US", "de-DE"</li>
 *   <li>Underscore format: "en_US", "de_DE"</li>
 *   <li>Full locale: "en_US_WIN", "de_DE_MAC"</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsLocaleList {

    /**
     * The configuration property key that contains the list of locales.
     * <p>
     * The value should be a string containing multiple locale identifiers
     * separated by the specified separator character. Each identifier will
     * be parsed into a {@link Locale} instance.
     * <p>
     * Example with default separator:
     * {@code app.locales=en,de,fr,es}
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * Controls fallback behavior when locale parsing fails.
     * <p>
     * When true:
     * <ul>
     *   <li>Returns system locale for invalid locale strings</li>
     *   <li>Ensures list always contains valid locales</li>
     *   <li>Prevents injection failures</li>
     * </ul>
     * When false:
     * <ul>
     *   <li>Throws {@link IllegalArgumentException} for invalid locales</li>
     *   <li>Ensures strict locale format compliance</li>
     *   <li>Fails fast on configuration errors</li>
     * </ul>
     *
     * @return {@code true} to use system locale as fallback,
     *         {@code false} to throw exception on parse errors
     */
    @Nonbinding
    boolean defaultToSystem() default true;

    /**
     * The character used to separate multiple locale identifiers in the
     * configuration value.
     * <p>
     * Common separator choices:
     * <ul>
     *   <li>',' (default) - Standard CSV separator</li>
     *   <li>';' - Alternative when locales may contain commas</li>
     *   <li>'|' - Useful for complex locale strings</li>
     * </ul>
     *
     * @return the separator character, defaults to
     *         {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    @Nonbinding
    char separator() default PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;
}
