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
package de.cuioss.portal.configuration.impl.schedule;

import de.cuioss.tools.io.MorePaths;
import de.cuioss.tools.logging.CuiLogger;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.util.Optional;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;

/**
 * Factory for creating {@link AbstractFileDescriptor} instances.
 * Extracted from {@link AbstractFileDescriptor} to break the package dependency
 * cycle between the abstract class and its concrete implementations.
 */
@UtilityClass
class FileDescriptors {

    private static final CuiLogger LOGGER = new CuiLogger(FileDescriptors.class);

    /**
     * Creates an {@link AbstractFileDescriptor}, depending on the input either a
     * {@link DirectoryDescriptor} or {@link FileDescriptor}.
     *
     * @param path may be null
     * @return an {@link Optional} {@link AbstractFileDescriptor}
     */
    static Optional<AbstractFileDescriptor> create(Path path) {
        if (null == path) {
            LOGGER.warn(WARN.PATH_INVALID, "null", "is null");
            return Optional.empty();
        }
        final var pathFile = MorePaths.getRealPathSafely(path).toFile();
        if (!pathFile.exists()) {
            LOGGER.warn(WARN.PATH_INVALID, pathFile.getAbsolutePath(), "does not exist");
            return Optional.empty();
        }
        if (!pathFile.canRead()) {
            LOGGER.warn(WARN.PATH_INVALID, pathFile.getAbsolutePath(), "can not be read");
            return Optional.empty();
        }
        if (pathFile.isDirectory()) {
            LOGGER.debug("Found valid directory, wrapping '%s'", pathFile.getAbsolutePath());
            return Optional.of(new DirectoryDescriptor(pathFile.toPath()));
        }
        LOGGER.debug("Found valid file, wrapping '%s'", pathFile.toPath());
        return Optional.of(new FileDescriptor(pathFile.toPath()));
    }
}
