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

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;

/**
 * Tracks changes to a single file by monitoring its last modification timestamp.
 * This class provides methods to detect when a file has been modified.
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class FileDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger LOGGER = new CuiLogger(FileDescriptor.class);

    private long modificationDate;

    /**
     * Creates a new FileDescriptor for the given path.
     *
     * @param path absolute or relative path to an existing file, must not be null
     * @throws NullPointerException if path is null
     */
    FileDescriptor(final Path path) {
        super(path);
    }

    /**
     * Updates the internal modification timestamp by reading the file's current
     * last modified time.
     * If the file cannot be read, a warning is logged and
     * the timestamp remains unchanged.
     */
    @Override
    public void update() {
        try {
            modificationDate = retrieveModificationDate();
        } catch (final IOException e) {
            LOGGER.warn(e, WARN.UNABLE_TO_READ_FILE.format(getPath()));
        }
    }

    /**
     * Checks if the file has been modified by comparing its current modification
     * timestamp with the stored timestamp.
     *
     * @return true if the file has been modified since the last update, false if
     * unchanged or if the file cannot be read
     */
    @Override
    public boolean isUpdated() {
        var newdate = 0L;
        try {
            newdate = retrieveModificationDate();
        } catch (final IOException e) {
            LOGGER.warn(e, WARN.UNABLE_TO_READ_FILE.format(getPath()));
        }

        return newdate > modificationDate;
    }

    /**
     * Retrieves the file's last modification timestamp in milliseconds.
     *
     * @return timestamp in milliseconds when the file was last modified
     * @throws IOException if the file's attributes cannot be read
     */
    private long retrieveModificationDate() throws IOException {
        return Files.getLastModifiedTime(getPath()).toMillis();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
