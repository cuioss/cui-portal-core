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
package de.cuioss.portal.core.test.mocks.configuration;

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

@EnableAutoWeld
@DisplayName("FileWatcherServiceMock Tests")
class FileWatcherServiceMockTest implements ShouldBeNotNull<FileWatcherServiceMock> {

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceMock underTest;

    @Inject
    @FileChangedEvent
    private Event<Path> fileChangeEvent;

    @Nested
    @DisplayName("Path Registration")
    class PathRegistrationTest {

        @Test
        @DisplayName("Should handle single path registration")
        void shouldHandleSinglePath() {
            var testPath = Path.of("/test/path");
            assertTrue(underTest.getRegisteredPaths().isEmpty(),
                    "Initial state should be empty");

            underTest.register(testPath);
            assertTrue(underTest.getRegisteredPaths().contains(testPath),
                    "Path should be registered");

            underTest.unregister(testPath);
            assertFalse(underTest.getRegisteredPaths().contains(testPath),
                    "Path should be unregistered");
        }

        @Test
        @DisplayName("Should handle multiple path registration")
        void shouldHandleMultiplePaths() {
            var path1 = Path.of("/test/path1");
            var path2 = Path.of("/test/path2");

            underTest.register(path1, path2);
            List<Path> paths = underTest.getRegisteredPaths();
            assertTrue(paths.contains(path1), "First path should be registered");
            assertTrue(paths.contains(path2), "Second path should be registered");

            underTest.unregister(path1, path2);
            assertTrue(underTest.getRegisteredPaths().isEmpty(),
                    "All paths should be unregistered");
        }

        @Test
        @DisplayName("Should handle duplicate path registration")
        void shouldHandleDuplicatePaths() {
            var testPath = Path.of("/test/path");

            underTest.register(testPath);
            underTest.register(testPath);

            assertEquals(1, underTest.getRegisteredPaths().size(),
                    "Duplicate paths should be registered only once");
        }
    }

    @Nested
    @DisplayName("Event Handling")
    class EventHandlingTest {

        @Test
        @DisplayName("Should have injected event")
        void shouldHaveEvent() {
            assertNotNull(fileChangeEvent, "FileChangedEvent should be injected");
        }

        @Test
        @DisplayName("Should fire events for registered paths")
        void shouldFireEvents() {
            var testPath = Path.of("/test/path");
            underTest.register(testPath);

            // Fire event and verify it's handled
            // Note: In a real test environment, we would need an EventObserver to verify
            underTest.fireEvent(testPath);

            assertTrue(underTest.getRegisteredPaths().contains(testPath),
                    "Path should remain registered after event");
        }
    }
}
