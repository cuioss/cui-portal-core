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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.schedule.PortalFileWatcherService;

import java.nio.file.Path;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class FileWatcherServiceMockTest implements ShouldBeNotNull<FileWatcherServiceMock> {

    @Inject
    @PortalFileWatcherService
    @Getter
    private FileWatcherServiceMock underTest;

    @Test
    void shouldHandlePaths() {
        assertTrue(underTest.getRegisteredPaths().isEmpty());
        underTest.register(Path.of("/"));
        assertFalse(underTest.getRegisteredPaths().isEmpty());
        underTest.unregister(Path.of("/"));
        assertTrue(underTest.getRegisteredPaths().isEmpty());
    }
}
