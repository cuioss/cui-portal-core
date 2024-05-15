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

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.portal.configuration.schedule.FileWatcherService;
import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;
import lombok.ToString;

/**
 * Mock implementation of {@link FileWatcherService} that is solely capable of
 * managing paths. It is defined as an {@link Alternative}, therefore you need
 * to activate it like
 *
 * <pre>
 *  &#64;ActivatedAlternatives(FileWatcherServiceMock.class)
 * </pre>
 *
 * In case your test needs {@link FileChangedEvent}s you can directly handle the
 * in you unit-test:
 *
 * <pre>
 *
 * &#64;Inject
 * &#64;FileChangedEvent
 * private Event&lt;Path&gt; fileChangeEvent;
 * </pre>
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalFileWatcherService
@ToString
public class FileWatcherServiceMock implements FileWatcherService {

    private final Set<Path> registeredPaths = new HashSet<>();

    @Inject
    @FileChangedEvent
    private Event<Path> fileChangeEvent;

    @Override
    public void register(Path... paths) {
        registeredPaths.addAll(Arrays.asList(paths));
    }

    @Override
    public void unregister(Path... paths) {
        registeredPaths.removeAll(Arrays.asList(paths));
    }

    @Override
    public List<Path> getRegisteredPaths() {
        return immutableList(registeredPaths);
    }

    /**
     * Shorthand for firing an @FileChangedEvent
     *
     * @param path
     */
    public void fireEvent(Path path) {
        fileChangeEvent.fire(path);
    }
}
