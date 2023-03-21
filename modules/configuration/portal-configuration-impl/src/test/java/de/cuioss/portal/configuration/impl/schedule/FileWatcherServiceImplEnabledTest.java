package de.cuioss.portal.configuration.impl.schedule;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.SCHEDULER_FILE_SCAN_ENABLED;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfiguration;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import lombok.Getter;

@EnablePortalConfiguration
@EnableAutoWeld
class FileWatcherServiceImplEnabledTest {

    private final TestFileHandler testFileHandler = new TestFileHandler();

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceImpl underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    private static final Path POM = Paths.get("pom.xml");
    private Path pathFromEvent;

    @BeforeEach
    void beforeTest() throws IOException {
        configuration.clear();
        configuration.initializeConfigurationSystem();
        pathFromEvent = null;
        testFileHandler.setup();
        underTest.clear();
        underTest.initialize();
    }

    @AfterEach
    void afterTest() {
        testFileHandler.cleanup();
        underTest.destroy();
    }

    @Test
    void shouldRegisterAndUnregisterCorrectly() {
        underTest.register(testFileHandler.getFile1());
        assertEquals(testFileHandler.getFile1().toAbsolutePath(), underTest.getRegisteredPaths().iterator().next());

        // Should not register twice
        underTest.register(testFileHandler.getFile1());
        assertEquals(1, underTest.getRegisteredPaths().size());

        underTest.register(POM);
        assertEquals(2, underTest.getRegisteredPaths().size());

        underTest.unregister(testFileHandler.getFile1());
        assertEquals(1, underTest.getRegisteredPaths().size());

        underTest.unregister(POM);
        assertTrue(underTest.getRegisteredPaths().isEmpty());

        // Do not register not existing file
        underTest.register(testFileHandler.getNonExistingFile());
        assertTrue(underTest.getRegisteredPaths().isEmpty());

        // Gracefully handle unregister
        underTest.unregister(testFileHandler.getFile1());
        assertTrue(underTest.getRegisteredPaths().isEmpty());
    }

    @Test
    @Disabled("owolff: For some reason the async calls block after the migration, needs further investigation")

    void shouldDetectChanges() throws IOException {
        assertTrue(underTest.isUpAndRunning());
        underTest.register(testFileHandler.getFile1());
        underTest.register(testFileHandler.getFile2());
        triggerAndAssertFileChangeEvent(testFileHandler.getFile1());
        triggerAndAssertFileChangeEvent(testFileHandler.getFile2());
    }

    @Test
    @Disabled("owolff: For some reason the async calls block after the migration, needs further investigation")
    void shouldHandleDirectories() throws Exception {
        var directory = testFileHandler.getBaseDir().toAbsolutePath();
        underTest.register(directory);

        // change file
        TestFileHandler.touchTargetFile(testFileHandler.getFile1());

        assertFileChangeEvent(directory);
    }

    @Test
    void shouldHandleConfigurationChanges() {
        assertTrue(underTest.isUpAndRunning());
        configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "false");
        configuration.fireEvent();
        assertFalse(underTest.isUpAndRunning());
        configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "true");
        configuration.fireEvent();
        assertTrue(underTest.isUpAndRunning());
    }

    void fileChangeListener(@Observes @FileChangedEvent final Path newPath) {
        pathFromEvent = newPath;
    }

    private void triggerAndAssertFileChangeEvent(Path filePath) throws IOException {
        pathFromEvent = null;

        // trigger file change
        TestFileHandler.touchTargetFile(filePath);

        assertFileChangeEvent(filePath);
    }

    private void assertFileChangeEvent(final Path filePath) throws IOException {
        // wait for FileChangedEvent
        assertDoesNotThrow(() -> await().atMost(3, TimeUnit.SECONDS)
                .until(() -> null != pathFromEvent),
                "did not receive file change event");

        assertTrue(Files.isSameFile(filePath, pathFromEvent),
                "file paths are expected to be equal:\n\tfilePath=" + filePath.toAbsolutePath()
                        + "\n\tpathFromEvent=" + pathFromEvent + "\n");
    }
}
