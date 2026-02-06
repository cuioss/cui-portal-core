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
package de.cuioss.portal.configuration.types;

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.tools.io.FileLoader;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting multiple file resources as a {@link List} of {@link FileLoader} instances.
 * This qualifier enables injection of multiple file paths from a single configuration property,
 * with support for custom separators and validation options.
 * <p>
 * Features:
 * <ul>
 *   <li>Multiple file resource injection</li>
 *   <li>Configurable path separator</li>
 *   <li>Validation options</li>
 *   <li>Returns immutable list</li>
 *   <li>Supports empty configurations</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage - fails if any file not found
 * &#64;Inject
 * &#64;ConfigAsFileLoaderList(name = "app.template.files")
 * private List&lt;FileLoader&gt; templateFiles;
 * 
 * // Custom separator and lenient validation
 * &#64;Inject
 * &#64;ConfigAsFileLoaderList(
 *     name = "app.config.files",
 *     separator = '|',
 *     failOnNotAccessible = false
 * )
 * private List&lt;FileLoader&gt; configFiles;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Using default separator (,)
 * app.template.files=/path/to/file1.html,/path/to/file2.html
 * 
 * # Using custom separator (|)
 * app.config.files=/path/to/config1.json|/path/to/config2.json
 * </pre>
 * <p>
 * File Path Support:
 * <ul>
 *   <li>Absolute paths: "/path/to/file"</li>
 *   <li>Relative paths: "path/to/file"</li>
 *   <li>Classpath resources: "classpath:/path/to/file"</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsFileLoaderList {

    /**
     * The configuration property key that contains the list of file paths.
     * <p>
     * The value should be a string containing multiple file paths separated
     * by the specified separator character. Each path will be converted
     * into a {@link FileLoader} instance.
     * <p>
     * Example with default separator:
     * {@code app.files=/path/file1.txt,/path/file2.txt}
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * Controls validation behavior when one or more files are not accessible.
     * <p>
     * When true:
     * <ul>
     *   <li>Throws {@link IllegalArgumentException} if any file cannot be accessed</li>
     *   <li>Ensures all files exist before injection</li>
     *   <li>Guarantees all files are available when injected</li>
     * </ul>
     * When false:
     * <ul>
     *   <li>Skips inaccessible files</li>
     *   <li>Returns FileLoader instances even for non-accessible files</li>
     *   <li>Useful when some files may be temporarily unavailable</li>
     * </ul>
     *
     * @return {@code true} to throw exception on inaccessible files,
     *         {@code false} to allow non-accessible files
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;

    /**
     * The character used to separate multiple file paths in the configuration value.
     * <p>
     * Common separator choices:
     * <ul>
     *   <li>',' (default) - Standard CSV separator</li>
     *   <li>';' - Alternative when paths may contain commas</li>
     *   <li>'|' - Useful when paths may contain commas or semicolons</li>
     * </ul>
     *
     * @return the separator character, defaults to
     *         {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    @Nonbinding
    char separator() default PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;
}
