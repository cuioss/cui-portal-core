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

import de.cuioss.tools.io.FileLoader;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting file resources as {@link FileLoader} instances.
 * This qualifier provides type-safe injection of file resources with support
 * for existence validation and error handling.
 * <p>
 * Features:
 * <ul>
 *   <li>Type-safe file resource injection</li>
 *   <li>Configurable validation behavior</li>
 *   <li>Automatic resource loading</li>
 *   <li>Error handling options</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage - fails if file not found
 * &#64;Inject
 * &#64;ConfigAsFileLoader(name = "app.config.file")
 * private FileLoader configFile;
 * 
 * // Optional file - won't fail if not found
 * &#64;Inject
 * &#64;ConfigAsFileLoader(
 *     name = "app.template.file",
 *     failOnNotAccessible = false
 * )
 * private FileLoader templateFile;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Configuration properties
 * app.config.file=/path/to/config.json
 * app.template.file=classpath:/templates/email.html
 * </pre>
 * <p>
 * Error Handling:
 * <ul>
 *   <li>Throws {@link IllegalArgumentException} if property is missing/empty</li>
 *   <li>Throws {@link IllegalArgumentException} if file not accessible (when failOnNotAccessible=true)</li>
 *   <li>Returns non-accessible FileLoader instance (when failOnNotAccessible=false)</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsFileLoader {

    /**
     * The configuration property key that specifies the file path.
     * <p>
     * The value associated with this key should be a valid file path.
     * Supported formats:
     * <ul>
     *   <li>Absolute paths: "/path/to/file"</li>
     *   <li>Relative paths: "path/to/file"</li>
     *   <li>Classpath resources: "classpath:/path/to/file"</li>
     * </ul>
     *
     * @return the configuration key for the file path
     */
    @Nonbinding
    String name();

    /**
     * Controls validation behavior when the specified file is not accessible.
     * <p>
     * When true:
     * <ul>
     *   <li>Throws {@link IllegalArgumentException} if file cannot be accessed</li>
     *   <li>Ensures file exists before injection</li>
     *   <li>Guarantees file is available when injected</li>
     * </ul>
     * When false:
     * <ul>
     *   <li>Returns FileLoader instance even if file is not accessible</li>
     *   <li>Allows for delayed/conditional file access</li>
     *   <li>Useful for optional resources</li>
     * </ul>
     *
     * @return {@code true} to throw exception on inaccessible files,
     *         {@code false} to allow non-accessible files
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;
}
