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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileDescriptorTest {

    private final TestFileHandler testFileHandler = new TestFileHandler();

    @BeforeEach
    void initTestFiles() throws IOException {
        testFileHandler.setup();
    }

    @AfterEach
    void removeTestFiles() {
        testFileHandler.cleanup();
    }

    @Test
    void shouldGracefullyHandleNonExistingFiles() {
        final var descriptor = new FileDescriptor(testFileHandler.getNonExistingFile());

        assertFalse(descriptor.isUpdated());
        descriptor.update();
    }

    @Test
    void shouldFulfillObjectContract() {
        final var descriptor1 = new FileDescriptor(testFileHandler.getNonExistingFile());
        final var descriptor2 = new FileDescriptor(testFileHandler.getFile1());

        assertNotEquals(descriptor1, descriptor2);

        assertEquals(descriptor1.hashCode(), descriptor1.hashCode());

        assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());

        assertNotNull(descriptor1.toString());
    }

    @Test
    void shouldHandleDirectory() {
        final var descriptor = new FileDescriptor(testFileHandler.getBaseDir());

        assertFalse(descriptor.isUpdated());
        descriptor.update();
    }

    @Test
    void shouldHandleSingleFile() throws IOException {
        final var descriptor = new FileDescriptor(testFileHandler.getFile1());

        assertTrue(Files.isSameFile(testFileHandler.getFile1(), descriptor.getPath()));

        assertFalse(descriptor.isUpdated());

        TestFileHandler.touchTargetFile(testFileHandler.getFile1());

        assertTrue(descriptor.isUpdated());

        descriptor.update();

        testFileHandler.cleanup();
    }
}
