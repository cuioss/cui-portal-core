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

import de.cuioss.tools.io.MorePaths;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.UUID;

public final class TestFileHandler {

    private static final CuiLogger LOGGER = new CuiLogger(TestFileHandler.class);

    private static final Path SOURCE_FILE = Path.of("src/test/resources/META-INF/test.properties");

    @Getter
    private Path baseDir;

    @Getter
    private Path file1;

    @Getter
    private Path file2;

    @Getter
    private Path nonExistingFile;

    /**
     * Combines the calls {@link #setupTestFileDirectory()}, creates two new files
     * and copies the content of {@link #SOURCE_FILE} into it and marks it for
     * deletion on exit.
     */
    public void setup() throws IOException {
        LOGGER.info("creating test data");

        baseDir = setupTestFileDirectory();
        nonExistingFile = baseDir.resolve("not.there");
        file1 = setupTestFile(baseDir.resolve("test.properties"));
        file2 = setupTestFile(baseDir.resolve("test2.properties"));
    }

    /**
     * Combines the calls {@link #setupTestFileDirectory()} and
     * {@link #setupTestFile(Path)}
     */
    public void cleanup() {
        LOGGER.info("cleanup test files");
        deleteTestFile(file1);
        deleteTestFile(file2);
        deleteTestFileDirectory();
    }

    /**
     * Sets the lastModifiedTime of {@param targetFile} to current + 1000ms
     */
    public static void touchTargetFile(Path targetFile) throws IOException {
        final var future = System.currentTimeMillis() + 1000;
        Files.setLastModifiedTime(targetFile, FileTime.fromMillis(future));
    }

    /**
     * Create a new random directory under {@link #SOURCE_FILE} and marks it for
     * deletion on exit. It can be accessed via {@link #getBaseDir()}.
     *
     * @return the path referencing the created file.
     */
    public Path setupTestFileDirectory() throws IOException {
        final var dir = Path.of("target/test-files", UUID.randomUUID().toString());

        if (!Files.exists(dir)) {
            LOGGER.info("creating {}", dir.toAbsolutePath());
            Files.createDirectories(dir).toFile().deleteOnExit();
        }

        LOGGER.info("created test file dir: {}", dir.toAbsolutePath());
        return dir;
    }

    private Path setupTestFile(Path newFile) throws IOException {
        if (!newFile.toFile().exists()) {
            LOGGER.info("creating: {}", newFile.toAbsolutePath());
            Files.createFile(newFile);
            newFile.toFile().deleteOnExit();
        } else {
            LOGGER.warn("file already exists: {}", newFile.toAbsolutePath());
        }

        LOGGER.info("copying over {} to {}", SOURCE_FILE, newFile.toAbsolutePath());
        Files.copy(new BufferedInputStream(Files.newInputStream(SOURCE_FILE)), newFile,
                StandardCopyOption.REPLACE_EXISTING);
        new BufferedInputStream(Files.newInputStream(SOURCE_FILE)).close();

        return newFile;
    }

    /**
     * Deletes the given file quietly if it exists
     */
    private static void deleteTestFile(Path targetFile) {
        if (Files.exists(targetFile)) {
            LOGGER.info("deleting: {}", targetFile.toAbsolutePath());
            MorePaths.deleteQuietly(targetFile);
        } else {
            LOGGER.warn("file does not exist: {}", targetFile.toAbsolutePath());
        }
    }

    /**
     * Tries to delete {@link #getBaseDir()}
     */
    private void deleteTestFileDirectory() {
        if (Files.exists(getBaseDir())) {
            LOGGER.info("deleting: {}", getBaseDir().toAbsolutePath());
            MorePaths.deleteQuietly(getBaseDir());
        } else {
            LOGGER.warn("dir does not exist: {}", getBaseDir().toAbsolutePath());
        }
    }
}
