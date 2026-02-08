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

import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.SCHEDULER_FILE_SCAN_ENABLED;
import static org.junit.jupiter.api.Assertions.*;

@EnablePortalConfigurationLocal
@EnableAutoWeld
class FileWatcherServiceImplDisabledTest {

    private final TestFileHandler testFileHandler = new TestFileHandler();

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceImpl underTest;

    @Inject
    private PortalTestConfigurationLocal configuration;

    private static final Path POM = Path.of("pom.xml");

    @BeforeEach
    void beforeTest() throws IOException {
        // clear all registered paths
        underTest.unregister(underTest.getRegisteredPaths().toArray(new Path[0]));
        testFileHandler.setup();
    }

    @AfterEach
    void afterTest() {
        testFileHandler.cleanup();
    }

    @Nested
    class RegistrationTests {
        @Test
        void shouldRegisterAndUnregisterCorrectly() {
            underTest.register(testFileHandler.getFile1());
            assertEquals(testFileHandler.getFile1().toAbsolutePath(), underTest.getRegisteredPaths().getFirst());

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
        void shouldHandleDirectories() {
            final var directory = testFileHandler.getFile1().getParent().toAbsolutePath();
            underTest.register(directory);
            assertTrue(underTest.getRegisteredPaths().contains(directory));
        }
    }

    @Nested
    class InitializationTests {
        @Test
        void shouldIgnoreInitIfConfiguredSo() {
            configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "false");
            configuration.fireEvent();
            underTest.initialize();
            assertFalse(underTest.isUpAndRunning());
        }

        @Test
        void shouldGracefullyHandleShutDownWithoutInit() {
            configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "false");
            configuration.fireEvent();
            underTest.initialize();
            assertFalse(underTest.isUpAndRunning());
            underTest.destroy();
        }

        @Test
        void shouldShutdownExecutorWhenDisabled() {
            // First enable to create the executor
            configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "true");
            configuration.fireEvent();
            underTest.initialize();
            assertTrue(underTest.isUpAndRunning());

            // Now disable - should shutdown executor
            configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "false");
            configuration.fireEvent();
            underTest.initialize();
            assertFalse(underTest.isUpAndRunning());

            // Cleanup
            underTest.destroy();
        }

        @Test
        void shouldHandleDestroyWithNullExecutor() {
            // Don't initialize (executor remains null)
            assertFalse(underTest.isUpAndRunning());
            // destroy should handle null executor gracefully
            assertDoesNotThrow(() -> underTest.destroy());
        }

        @Test
        void shouldHandleMultipleInitializeCalls() {
            configuration.put(SCHEDULER_FILE_SCAN_ENABLED, "true");
            configuration.fireEvent();
            underTest.initialize();
            assertTrue(underTest.isUpAndRunning());

            // Second initialize should not fail
            underTest.initialize();
            assertTrue(underTest.isUpAndRunning());

            underTest.destroy();
        }
    }
}
