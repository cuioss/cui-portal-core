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

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Provides some convenient methods for tracing detecting directory-changes.
 *
 * @author Matthias Walliczek
 *
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class DirectoryDescriptor extends AbstractFileDescriptor {

    private static final CuiLogger log = new CuiLogger(DirectoryDescriptor.class);

    private List<String> content;

    private long lastModifiedHash;

    /**
     * @param path must not be null and derive an existing directory
     */
    DirectoryDescriptor(final Path path) {
        super(path);
    }

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
            log.warn(ex, "Directory %s could not be read", directory);
        }
        return fileNames;
    }

    @Override
    public void update() {
        lastModifiedHash = 0L;
        content = fileList(getPath());
    }

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
