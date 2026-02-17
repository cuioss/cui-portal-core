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
package de.cuioss.portal.core;

import de.cuioss.portal.core.test.support.PortalAuthenticationFacadeMock;
import jakarta.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests ensuring the consistency and proper configuration of the portal core module.
 */
@DisplayName("Module Consistency Tests")
class ModuleConsistencyTest {

    @Nested
    @DisplayName("CDI Container Tests")
    class CdiContainerTests {

        @Test
        @DisplayName("Should start up CDI container successfully")
        void shouldStartUpContainer() {
            try (var weld = new Weld()
                         .enableDiscovery()
                         .addBeanClass(PortalAuthenticationFacadeMock.class)
                         .initialize()) {

                assertNotNull(weld.select(BeanManager.class),
                        "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
            }
        }
    }

    @Nested
    @DisplayName("Module Configuration Tests")
    class ModuleConfigurationTests {

        @Test
        @DisplayName("Should provide beans.xml in META-INF")
        void moduleShouldProvideBeansXml() {
            assertTrue(Path.of("src", "main", "resources", "META-INF", "beans.xml")
                            .toFile()
                            .exists(),
                    "It is best-practice that each module provides a beans.xml");
        }
    }
}
