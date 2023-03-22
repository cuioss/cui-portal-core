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

class DirectoryDescriptorTest {

    private final TestFileHandler testFileHandler = new TestFileHandler();

    @BeforeEach
    void before() throws IOException {
        testFileHandler.setup();
    }

    @AfterEach
    void after() {
        testFileHandler.cleanup();
    }

    @Test
    void shouldHandleFactory() {
        assertFalse(AbstractFileDescriptor.create(null).isPresent());
        assertFalse(AbstractFileDescriptor.create(testFileHandler.getNonExistingFile()).isPresent());
        assertTrue(AbstractFileDescriptor.create(testFileHandler.getBaseDir()).isPresent());
        assertTrue(AbstractFileDescriptor.create(testFileHandler.getBaseDir()).isPresent());
    }

    @Test
    void shouldFulfillObjectContract() throws IOException {
        final var descriptor1 = AbstractFileDescriptor.create(testFileHandler.getBaseDir()).get();
        var newDirectory = testFileHandler.getBaseDir().resolve("newDirectory");
        Files.createDirectories(newDirectory);
        final var descriptor2 = AbstractFileDescriptor.create(newDirectory).get();

        assertNotEquals(descriptor1, descriptor2);

        assertNotEquals(descriptor1.hashCode(), descriptor2.hashCode());

        assertNotNull(descriptor1.toString());
    }

    @Test
    void shouldHandleDirectory() throws IOException {
        final var descriptor = AbstractFileDescriptor.create(testFileHandler.getBaseDir()).get();
        assertFalse(descriptor.isUpdated());
        descriptor.update();

        var newDirectory = testFileHandler.getBaseDir().resolve("newDirectory");
        Files.createDirectories(newDirectory);
        assertTrue(descriptor.isUpdated());

        descriptor.update();
        assertFalse(descriptor.isUpdated());

        var containedDirectory = newDirectory.resolve("nested");
        Files.createDirectories(containedDirectory);

        assertTrue(descriptor.isUpdated());
    }

    @Test
    void shouldHandleSingleFile() {
        final var descriptor = AbstractFileDescriptor.create(testFileHandler.getFile1()).get();

        assertEquals(testFileHandler.getFile1().toAbsolutePath(), descriptor.getPath());

        assertFalse(descriptor.isUpdated());
    }
}
