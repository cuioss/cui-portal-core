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
package de.cuioss.portal.core.test.mocks.configuration;

import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.FileWatcherService;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.ToString;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

/**
 * Mock implementation of {@link FileWatcherService} for testing file system monitoring.
 * This mock provides a simplified version that tracks registered paths and allows
 * manual triggering of file change events.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Path registration and tracking</li>
 *   <li>Manual event triggering</li>
 *   <li>CDI alternative implementation</li>
 *   <li>Application-scoped consistency</li>
 *   <li>Thread-safe path management</li>
 * </ul>
 *
 * <h2>Setup</h2>
 * Activate the mock in your test class:
 * <pre>
 * &#64;EnablePortalConfiguration
 * &#64;ActivatedAlternatives(FileWatcherServiceMock.class)
 * class FileWatcherTest {
 *     &#64;Inject
 *     private FileWatcherService watcherService;
 * }
 * </pre>
 *
 * <h2>Usage Examples</h2>
 *
 * Path registration:
 * <pre>
 * &#64;Inject
 * private FileWatcherServiceMock watcherService;
 *
 * void testPathRegistration() {
 *     Path testPath = Paths.get("/test/path");
 *     watcherService.register(testPath);
 *     assertTrue(watcherService.getRegisteredPaths().contains(testPath));
 * }
 * </pre>
 *
 * Triggering file change events:
 * <pre>
 * &#64;Inject
 * private FileWatcherServiceMock watcherService;
 *
 * &#64;Inject
 * &#64;FileChangedEvent
 * private Event&lt;Path&gt; fileChangeEvent;
 *
 * void testFileChangeEvent() {
 *     Path changedFile = Paths.get("/changed/file");
 *     watcherService.register(changedFile);
 *     
 *     // Trigger event manually
 *     watcherService.fireEvent(changedFile);
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Uses {@link HashSet} for thread-safe path storage</li>
 *   <li>Implements {@link PortalFileWatcherService} interface</li>
 *   <li>Provides direct access to registered paths</li>
 *   <li>Supports varargs for bulk path operations</li>
 *   <li>Events must be manually triggered via {@link #fireEvent}</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see FileWatcherService
 * @see FileChangedEvent
 * @see Path
 */
@ApplicationScoped
@PortalFileWatcherService
@ToString
public class FileWatcherServiceMock implements FileWatcherService {

    private final Set<Path> registeredPaths = new HashSet<>();

    private final Event<Path> fileChangeEvent;

    @Inject
    FileWatcherServiceMock(@FileChangedEvent Event<Path> fileChangeEvent) {
        this.fileChangeEvent = fileChangeEvent;
    }

    @Override
    public void register(Path... paths) {
        registeredPaths.addAll(Arrays.asList(paths));
    }

    @Override
    public void unregister(Path... paths) {
        Arrays.asList(paths).forEach(registeredPaths::remove);
    }

    @Override
    public List<Path> getRegisteredPaths() {
        return immutableList(registeredPaths);
    }

    /**
     * Shorthand for firing a @FileChangedEvent
     *
     * @param path
     */
    public void fireEvent(Path path) {
        fileChangeEvent.fire(path);
    }
}
