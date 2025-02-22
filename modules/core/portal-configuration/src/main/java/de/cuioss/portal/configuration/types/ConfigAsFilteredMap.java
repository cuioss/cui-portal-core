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
import java.util.Map;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting filtered configuration properties as an immutable {@link Map}.
 * This qualifier enables injection of configuration subsets by filtering properties
 * based on a common prefix.
 * <p>
 * Features:
 * <ul>
 *   <li>Prefix-based property filtering</li>
 *   <li>Optional prefix stripping from keys</li>
 *   <li>Returns immutable map</li>
 *   <li>Type-safe string key-value pairs</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage - keep prefix
 * &#64;Inject
 * &#64;ConfigAsFilteredMap(startsWith = "portal.theme.")
 * private Map&lt;String, String&gt; themeProperties;
 * 
 * // Strip prefix from keys
 * &#64;Inject
 * &#64;ConfigAsFilteredMap(
 *     startsWith = "portal.mail.",
 *     stripPrefix = true
 * )
 * private Map&lt;String, String&gt; mailConfig;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Configuration properties
 * portal.theme.name=dark
 * portal.theme.primary-color=#000000
 * portal.theme.secondary-color=#ffffff
 * portal.mail.host=smtp.example.com
 * portal.mail.port=587
 * 
 * # Results with prefix (themeProperties):
 * {
 *   "portal.theme.name": "dark",
 *   "portal.theme.primary-color": "#000000",
 *   "portal.theme.secondary-color": "#ffffff"
 * }
 * 
 * # Results with stripped prefix (mailConfig):
 * {
 *   "host": "smtp.example.com",
 *   "port": "587"
 * }
 * </pre>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsFilteredMap {

    /**
     * The prefix to filter configuration properties.
     * <p>
     * Only properties whose keys start with this prefix will be included
     * in the resulting map.
     * The comparison is case-sensitive.
     * <p>
     * For example, if startsWith="app.config.", then:
     * <ul>
     *   <li>"app.config.key1" - included</li>
     *   <li>"app.config.nested.key2" - included</li>
     *   <li>"app.other.key3" - excluded</li>
     * </ul>
     *
     * @return the prefix to filter properties by
     */
    @Nonbinding
    String startsWith();

    /**
     * Controls whether to remove the prefix from the map keys.
     * <p>
     * When {@code true}, the prefix specified by {@link #startsWith()} will be
     * removed from all keys in the resulting map. This is useful when you want
     * to work with shorter, context-free keys.
     * <p>
     * For example, with prefix="app.config." and property "app.config.key1=value":
     * <ul>
     *   <li>stripPrefix=false → {"app.config.key1": "value"}</li>
     *   <li>stripPrefix=true → {"key1": "value"}</li>
     * </ul>
     *
     * @return {@code true} to remove prefix from keys, {@code false} to keep full keys
     */
    @Nonbinding
    boolean stripPrefix() default false;
}
