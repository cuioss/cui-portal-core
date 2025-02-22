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
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting configuration properties as immutable {@link Set} instances.
 * This qualifier provides type-safe injection of unique configuration values with
 * automatic deduplication and flexible formatting options.
 * <p>
 * Features:
 * <ul>
 *   <li>Automatic deduplication</li>
 *   <li>String trimming</li>
 *   <li>Configurable separator</li>
 *   <li>Default value support</li>
 *   <li>Empty set for null values</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with default separator (,)
 * &#64;Inject
 * &#64;ConfigAsSet(name = "app.allowed.roles")
 * private Set&lt;String&gt; allowedRoles;
 * 
 * // Custom separator with default value
 * &#64;Inject
 * &#64;ConfigAsSet(
 *     name = "app.mime.types",
 *     separator = ';',
 *     defaultValue = "text/html;application/json"
 * )
 * private Set&lt;String&gt; mimeTypes;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Basic comma-separated values
 * app.allowed.roles=admin,user,guest,user  # Duplicate 'user' removed
 * 
 * # Custom separator (;)
 * app.mime.types=text/html;application/json;image/png
 * 
 * # Multi-line configuration
 * app.categories=electronics,\
 *               books,\
 *               clothing
 * 
 * # Values with spaces (trimmed)
 * app.status=  active , inactive , pending   # Spaces removed
 * </pre>
 * <p>
 * Value Processing:
 * <ul>
 *   <li>Automatic deduplication of values</li>
 *   <li>Leading/trailing whitespace trimmed</li>
 *   <li>Empty elements excluded</li>
 *   <li>Case sensitivity preserved</li>
 *   <li>Order not guaranteed</li>
 * </ul>
 * <p>
 * Common Use Cases:
 * <ul>
 *   <li>Role/permission lists</li>
 *   <li>Allowed file types</li>
 *   <li>Feature flags</li>
 *   <li>Supported locales</li>
 *   <li>IP address whitelists</li>
 * </ul>
 * <p>
 * Error Handling:
 * <ul>
 *   <li>Null/empty property → Empty set or default value</li>
 *   <li>Invalid format → IllegalArgumentException</li>
 *   <li>All empty elements → Empty set</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsSet {

    /**
     * The configuration property key that specifies the set elements.
     * <p>
     * The value should be a string containing unique elements separated by
     * the specified separator character. The elements will be:
     * <ul>
     *   <li>Split by the separator</li>
     *   <li>Trimmed of whitespace</li>
     *   <li>Deduplicated</li>
     *   <li>Empty elements removed</li>
     * </ul>
     * <p>
     * Example configurations:
     * <pre>
     * # Basic usage
     * app.roles=admin,user,guest
     * 
     * # With duplicates (automatically removed)
     * app.status=active,pending,active,done
     * 
     * # With spaces (automatically trimmed)
     * app.types= type1 , type2 , type3
     * </pre>
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * The character used to separate set elements in the configuration value.
     * <p>
     * Common separator choices:
     * <ul>
     *   <li>',' (default) - Standard CSV separator</li>
     *   <li>';' - Alternative when values may contain commas</li>
     *   <li>'|' - Useful when values may contain other separators</li>
     *   <li>':' - Common for path-like values</li>
     * </ul>
     * <p>
     * Example usage:
     * <pre>
     * separator=','  →  "a,b,c"   →  ["a", "b", "c"]
     * separator=';'  →  "a;b;c"   →  ["a", "b", "c"]
     * separator='|'  →  "a|b|c"   →  ["a", "b", "c"]
     * </pre>
     *
     * @return the separator character, defaults to
     *         {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    @Nonbinding
    char separator() default PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;

    /**
     * Default value to use when the configuration property is not found.
     * <p>
     * The default value string will be:
     * <ul>
     *   <li>Split using the specified separator</li>
     *   <li>Have each element trimmed</li>
     *   <li>Deduplicated</li>
     *   <li>Empty elements removed</li>
     * </ul>
     * <p>
     * Example:
     * <pre>
     * &#64;ConfigAsSet(
     *     name = "app.roles",
     *     defaultValue = "user,guest"  // Used if app.roles not found
     * )
     * </pre>
     *
     * @return the default value string, empty string if none
     */
    @Nonbinding
    String defaultValue() default "";
}
