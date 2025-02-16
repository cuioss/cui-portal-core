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

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides some convenient methods for tracing detecting file-changes.
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class FileDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger LOGGER = new CuiLogger(FileDescriptor.class);

    private long modificationDate;

    /**
     * @param path must not be null and derive an existing file
     */
    FileDescriptor(final Path path) {
        super(path);
    }

    @Override
    public void update() {
        try {
            modificationDate = retrieveModificationDate();
        } catch (final IOException e) {
            LOGGER.warn(e, WARN.UNABLE_TO_READ_FILE.format(getPath()));
        }
    }

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

    private long retrieveModificationDate() throws IOException {
        return Files.getLastModifiedTime(getPath()).toMillis();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
