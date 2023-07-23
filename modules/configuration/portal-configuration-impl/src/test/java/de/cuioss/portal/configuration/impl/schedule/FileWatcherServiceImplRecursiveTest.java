package de.cuioss.portal.configuration.impl.schedule;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.awaitility.Awaitility;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;

@EnablePortalConfigurationLocal
@EnableAutoWeld
@EnableTestLogger(rootLevel = TestLogLevel.TRACE)
class FileWatcherServiceImplRecursiveTest {

    private static final CuiLogger LOGGER = new CuiLogger(FileWatcherServiceImplRecursiveTest.class);

    private final TestFileHandler testFileHandler = new TestFileHandler();

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceImpl underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    private Path newPathToRegister;
    private Path pathFromEvent;

    @BeforeEach
    void beforeTest() throws IOException {
        configuration.clear();
        configuration.initializeConfigurationSystem();
        testFileHandler.setup();
        underTest.initialize();
    }

    @AfterEach
    void afterTest() {
        testFileHandler.cleanup();
        underTest.destroy();
    }

    @Test
    @Disabled("owolff: For some reason the async calls block after the migration, needs further investigation")
    void checkMultipleTimes() throws IOException {
        underTest.initialize();
        assertTrue(underTest.isUpAndRunning());
        assertNull(pathFromEvent);
        underTest.register(testFileHandler.getFile1());
        underTest.register(testFileHandler.getFile2());
        assertNull(pathFromEvent);
        newPathToRegister = testFileHandler.getFile1();

        // change file
        TestFileHandler.touchTargetFile(testFileHandler.getFile1());

        // wait for FileChangedEvent
        await().atMost(3, TimeUnit.SECONDS).until(() -> null != pathFromEvent);
        assertNotNull(pathFromEvent, "did not receive file change event");

        LOGGER.info("Comparing two files: {} vs {}", testFileHandler.getFile1(), pathFromEvent);
        assertTrue(Files.isSameFile(testFileHandler.getFile1(), pathFromEvent));

        TestFileHandler.touchTargetFile(testFileHandler.getFile2());

        assertDoesNotThrow(
                () -> Awaitility.await().atMost(3, TimeUnit.SECONDS).alias("files-equal")
                        .until(() -> isSameFile(testFileHandler.getFile2(), pathFromEvent)),
                "Files should be the same: " + "file_a=" + testFileHandler.getFile2().toAbsolutePath() + ", file_b="
                        + pathFromEvent.toAbsolutePath());
    }

    private static boolean isSameFile(Path path1, Path path2) {
        try {
            return Files.isSameFile(path1, path2);
        } catch (IOException e) {
            LOGGER.debug("not the same file", e);
        }
        return false;
    }

    void fileChangeListener(@Observes @FileChangedEvent final Path newPath) {
        LOGGER.info("received file change event for path: {}", newPath);
        pathFromEvent = newPath;
        underTest.register(newPathToRegister);
        underTest.initialize();
    }
}
