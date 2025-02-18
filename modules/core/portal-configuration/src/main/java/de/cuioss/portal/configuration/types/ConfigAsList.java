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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * CDI qualifier for injecting configuration properties as immutable lists of strings.
 * This qualifier provides type-safe injection of separated configuration values
 * into a {@code List<String>}.
 * <p>
 * Features:
 * <ul>
 *   <li>Immutable list injection</li>
 *   <li>Automatic string trimming</li>
 *   <li>Configurable separator character</li>
 *   <li>Optional default value support</li>
 *   <li>Empty list for null/empty properties</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with default separator (,)
 * &#64;Inject
 * &#64;ConfigAsList(name = "app.allowed.roles")
 * private List<String> allowedRoles;
 * 
 * // Custom separator with default value
 * &#64;Inject
 * &#64;ConfigAsList(
 *     name = "app.search.paths",
 *     separator = ':',
 *     defaultValue = "/home:/usr/local"
 * )
 * private List<String> searchPaths;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Using default separator (,)
 * app.allowed.roles=admin,user,guest
 * 
 * # Using custom separator (:)
 * app.search.paths=/opt/app:/var/lib:/usr/local
 * 
 * # Multi-line configuration also supported
 * app.categories=electronics,\
 *               books,\
 *               clothing
 * </pre>
 * <p>
 * Value Processing:
 * <ul>
 *   <li>Each element is trimmed of leading/trailing whitespace</li>
 *   <li>Empty elements are excluded from the final list</li>
 *   <li>Null or empty property results in empty list</li>
 *   <li>Default value is processed using same rules</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsList {

    /**
     * The configuration property key that contains the list elements.
     * <p>
     * The value should be a string containing multiple elements separated
     * by the specified separator character. Each element will be trimmed
     * and added to the resulting list.
     * <p>
     * Example with default separator:
     * {@code app.items=item1,item2,item3}
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * The character used to separate list elements in the configuration value.
     * <p>
     * Common separator choices:
     * <ul>
     *   <li>',' (default) - Standard CSV separator</li>
     *   <li>';' - Alternative when values may contain commas</li>
     *   <li>':' - Common for path-like values</li>
     *   <li>'|' - Useful when values may contain other separators</li>
     * </ul>
     * <p>
     * Example usage:
     * <pre>
     * separator=','  →  "a,b,c"     →  ["a", "b", "c"]
     * separator=':'  →  "/a:/b:/c"  →  ["/a", "/b", "/c"]
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
     *   <li>Exclude empty elements</li>
     *   <li>Result in empty list if blank</li>
     * </ul>
     * <p>
     * Example:
     * <pre>
     * &#64;ConfigAsList(
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
