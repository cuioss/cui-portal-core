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

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

/**
 * Tracks changes to a directory by monitoring its content and the modification timestamps
 * of all files and subdirectories within it.
 * This class provides methods to detect when
 * any files or subdirectories have been added, removed, or modified.
 *
 * @author Matthias Walliczek
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class DirectoryDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger LOGGER = new CuiLogger(DirectoryDescriptor.class);

    private List<String> content;

    private long lastModifiedHash;

    /**
     * Creates a new DirectoryDescriptor for the given path.
     *
     * @param path absolute or relative path to an existing directory, must not be null
     * @throws NullPointerException if path is null
     */
    DirectoryDescriptor(final Path path) {
        super(path);
    }

    /**
     * Recursively builds a list of all files and subdirectories within the given directory,
     * while also computing a hash of their modification timestamps.
     *
     * @param directory the directory to scan
     * @return list of paths (as strings) for all files and directories found
     */
    private List<String> fileList(final Path directory) {
        List<String> fileNames = mutableList();
        try (var directoryStream = Files.newDirectoryStream(directory)) {
            for (Path currentPath : directoryStream) {
                lastModifiedHash += currentPath.toFile().lastModified();
                fileNames.add(currentPath.toString());
                if (currentPath.toFile().isDirectory()) {
                    fileNames.addAll(fileList(currentPath));
                }
            }
        } catch (IOException ex) {
            LOGGER.warn(ex, WARN.UNABLE_TO_READ_DIRECTORY, directory);
        }
        return fileNames;
    }

    /**
     * Updates the internal state by scanning the directory structure and computing
     * new content and modification timestamp hashes.
     * This method resets the stored modification hash before performing the update.
     */
    @Override
    public void update() {
        lastModifiedHash = 0L;
        content = fileList(getPath());
    }

    /**
     * Checks if the directory or any of its contents have been modified by comparing
     * both the list of files/directories and their modification timestamps.
     *
     * @return true if any files/directories have been added, removed, or modified
     * since the last update, false if everything is unchanged
     */
    @Override
    public boolean isUpdated() {
        var oldLastModifiedHash = lastModifiedHash;
        lastModifiedHash = 0L;
        var newContent = fileList(getPath());

        return !newContent.equals(content) || oldLastModifiedHash != lastModifiedHash;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
