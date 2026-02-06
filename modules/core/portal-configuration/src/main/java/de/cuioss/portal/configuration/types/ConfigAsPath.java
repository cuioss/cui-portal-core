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

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting configuration properties as {@link Path} instances.
 * This qualifier provides type-safe injection of file system paths with
 * configurable validation options and flexible path resolution.
 * <p>
 * Features:
 * <ul>
 *   <li>Automatic path resolution</li>
 *   <li>Configurable path validation</li>
 *   <li>Support for multiple path formats</li>
 *   <li>Strict validation mode</li>
 *   <li>Null-safe operation</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with strict validation
 * &#64;Inject
 * &#64;ConfigAsPath(name = "app.data.dir")
 * private Path dataDirectory;
 * 
 * // Allow non-accessible paths
 * &#64;Inject
 * &#64;ConfigAsPath(
 *     name = "app.output.file",
 *     failOnNotAccessible = false
 * )
 * private Path outputFile;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # Absolute paths
 * app.data.dir=/var/lib/app/data
 * app.logs.dir=/var/log/app
 * 
 * # Relative paths (relative to working directory)
 * app.config.file=config/settings.json
 * app.temp.dir=./temp
 * 
 * # Windows-style paths (automatically normalized)
 * app.windows.path=C:\\Program Files\\App\\data
 * </pre>
 * <p>
 * Path Resolution:
 * <ul>
 *   <li>Absolute paths are used as-is</li>
 *   <li>Relative paths are resolved against working directory</li>
 *   <li>Symbolic links are followed</li>
 *   <li>Path separators are normalized for the platform</li>
 * </ul>
 * <p>
 * Validation Options:
 * <ul>
 *   <li>Existence Check: Verifies path exists</li>
 *   <li>Access Check: Verifies path is accessible</li>
 *   <li>Format Check: Validates path syntax</li>
 * </ul>
 * <p>
 * Error Handling:
 * <ul>
 *   <li>Missing/empty property → IllegalArgumentException</li>
 *   <li>Invalid path syntax → IllegalArgumentException</li>
 *   <li>Inaccessible path with failOnNotAccessible=true → IllegalArgumentException</li>
 *   <li>Inaccessible path with failOnNotAccessible=false → Returns unvalidated Path</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsPath {

    /**
     * The configuration property key that specifies the file system path.
     * <p>
     * The value should be a string representing a valid file system path.
     * Supported formats:
     * <ul>
     *   <li>Absolute paths: "/path/to/file"</li>
     *   <li>Relative paths: "path/to/file"</li>
     *   <li>Windows paths: "C:\\path\\to\\file"</li>
     *   <li>UNC paths: "\\\\server\\share\\file"</li>
     * </ul>
     * <p>
     * Example configurations:
     * <pre>
     * # Unix-style paths
     * app.path=/var/lib/app/data
     * app.path=./relative/path
     * app.path=../parent/path
     * 
     * # Windows-style paths
     * app.path=C:\\Program Files\\App
     * app.path=.\\relative\\path
     * </pre>
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * Controls path validation behavior during injection.
     * <p>
     * When enabled (true):
     * <ul>
     *   <li>Verifies path exists in the file system</li>
     *   <li>Checks read permissions on the path</li>
     *   <li>Validates parent directory accessibility</li>
     *   <li>Throws exception if validation fails</li>
     * </ul>
     * When disabled (false):
     * <ul>
     *   <li>Only validates path syntax</li>
     *   <li>Allows non-existent paths</li>
     *   <li>Useful for paths created at runtime</li>
     *   <li>No filesystem access checks</li>
     * </ul>
     *
     * @return {@code true} to enable strict validation,
     *         {@code false} to allow non-accessible paths
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;
}
