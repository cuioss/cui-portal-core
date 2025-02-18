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
package de.cuioss.portal.configuration.schedule;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for monitoring file system changes in the Portal configuration system.
 * This service enables dynamic configuration updates by watching specified files
 * and directories for modifications.
 * <p>
 * Key features:
 * <ul>
 *   <li>Register multiple files and directories for monitoring</li>
 *   <li>Event-based notification system using {@link FileChangedEvent}</li>
 *   <li>Thread-safe path registration and unregistration</li>
 *   <li>Support for both file and directory monitoring</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 * &#64;Inject
 * &#64;PortalFileWatcherService
 * private FileWatcherService watcher;
 *
 * public void watchConfigFile() {
 *     Path configPath = Paths.get("/path/to/config.properties");
 *     watcher.register(configPath);
 * }
 * </pre>
 *
 * @author Oliver Wolff
 */
public interface FileWatcherService {

    /**
     * Registers one or more paths to be monitored for changes.
     * When changes are detected, a {@link FileChangedEvent} will be fired
     * with the modified path as the payload.
     * <p>
     * For directories, changes to files within the directory will trigger events.
     * For files, modifications to the file itself will trigger events.
     *
     * @param paths one or more paths to be monitored, must not be null
     * @throws IllegalArgumentException if any path is null or does not exist
     */
    void register(Path... paths);

    /**
     * Unregisters paths from being monitored.
     * This method is idempotent - paths that are not currently registered
     * will be silently ignored.
     *
     * @param paths one or more paths to stop monitoring
     */
    void unregister(Path... paths);

    /**
     * Retrieves all paths currently being monitored by this service.
     * The returned list includes both individual files and directories.
     *
     * @return an immutable list of all registered paths, never null
     */
    List<Path> getRegisteredPaths();
}
