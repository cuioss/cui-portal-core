package de.cuioss.portal.configuration.impl.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
