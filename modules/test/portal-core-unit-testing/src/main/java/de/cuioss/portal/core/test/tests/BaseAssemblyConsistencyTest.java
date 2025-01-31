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
package de.cuioss.portal.core.test.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

/**
 * Base class for testing the consistency / structure of an assembly module by
 * starting up a {@link WeldContainer} and checking whether the module provides
 * a beans.xml-file. This is a variant of {@link BaseModuleConsistencyTest}
 *
 * @author Oliver Wolff
 *
 */
public class BaseAssemblyConsistencyTest {

    @Test
    protected void shouldStartUpContainer() {
        try (var weld = new Weld().initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    protected void assemblyShouldProvideBeansXml() {
        assertTrue(Path.of("src", "main", "webapp", "WEB-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

}
