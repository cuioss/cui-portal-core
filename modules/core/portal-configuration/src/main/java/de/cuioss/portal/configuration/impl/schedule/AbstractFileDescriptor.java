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
package de.cuioss.portal.configuration.impl.schedule;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Objects.requireNonNull;

import de.cuioss.tools.io.MorePaths;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Optional;

/**
 * Interface to observe changes in file systems.
 *
 * @author Matthias Walliczek
 *
 */
@EqualsAndHashCode
@ToString
abstract class AbstractFileDescriptor {

    private static final CuiLogger log = new CuiLogger(AbstractFileDescriptor.class);

    /**
     * The factory-method create() will always return a real-path for this field.
     */
    @Getter
    private final Path path;

    /**
     * @param path must not be null and derive an existing file
     */
    AbstractFileDescriptor(final Path path) {
        this.path = requireNonNull(path, "path");
        update();
    }

    /**
     * @return true if the entity is updated.
     */
    abstract boolean isUpdated();

    /**
     * Updates the information, whether the specified file changed.
     */

    abstract void update();

    /**
     * @return boolean indicating whether instance is wrapping a directory
     */
    abstract boolean isDirectory();

    void addWatchKey(WatchService watcherService, Map<WatchKey, Path> watchedPaths) {
        var toBeWatched = getPath();
        if (!isDirectory()) {
            toBeWatched = toBeWatched.getParent();
        }
        var absolute = toBeWatched.toAbsolutePath();
        if (watchedPaths.containsValue(absolute)) {
            log.debug("Path '{}' already registered, ignoring", absolute);
            return;
        }
        try {
            watchedPaths.put(absolute.register(watcherService, ENTRY_MODIFY, ENTRY_DELETE, ENTRY_DELETE), absolute);
            log.info("Portal-010: Watching for file changes at path: {}", absolute);
        } catch (IOException e) {
            log.error("Portal-517: Unable to schedule given Path for tracking for changes, due to '{}'", e.getMessage(),
                    e);
        }
    }

    /**
     * Creates an {@link FileDescriptor}, depending on the input either a
     * {@link DirectoryDescriptor} or {@link FileDescriptor}
     *
     * @param path may be null
     * @return an {@link Optional} {@link AbstractFileDescriptor}
     */
    static Optional<AbstractFileDescriptor> create(Path path) {
        if (null == path) {
            log.warn("Portal-142: Path is null, therefore it can not be watched");
            return Optional.empty();
        }
        final var pathFile = MorePaths.getRealPathSafely(path).toFile();
        if (!pathFile.exists()) {
            log.warn("Portal-142: Path '{}' does not exist, therefore it can not be watched",
                    pathFile.getAbsolutePath());
            return Optional.empty();
        }
        if (!pathFile.canRead()) {
            log.warn("Portal-142: Path '{}' can not be read, therefore it can not be watched",
                    pathFile.getAbsolutePath());
            return Optional.empty();
        }
        if (pathFile.isDirectory()) {
            log.debug("Found valid directory, wrapping '{}'", pathFile.getAbsolutePath());
            return Optional.of(new DirectoryDescriptor(pathFile.toPath()));
        }
        log.debug("Found valid file, wrapping '{}'", pathFile.toPath());
        return Optional.of(new FileDescriptor(pathFile.toPath()));
    }
}
