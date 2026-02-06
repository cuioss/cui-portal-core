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
package de.cuioss.portal.core.test.tests;

import jakarta.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for testing the consistency and structure of a Portal module by
 * verifying CDI container initialization and beans.xml configuration.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Automated CDI container initialization testing</li>
 *   <li>beans.xml presence verification</li>
 *   <li>Customizable Weld container configuration</li>
 *   <li>BeanManager availability testing</li>
 *   <li>Module structure validation</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic module test:
 * <pre>
 * public class MyModuleTest extends BaseModuleConsistencyTest {
 *     // Uses default configuration
 * }
 * </pre>
 *
 * Customized container configuration:
 * <pre>
 * public class CustomModuleTest extends BaseModuleConsistencyTest {
 *     &#64;Override
 *     protected Weld modifyWeldContainer(Weld weld) {
 *         return weld.enableDevMode()
 *                   .addBeanClass(CustomProducer.class)
 *                   .addAlternative(MockService.class);
 *     }
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Uses Weld SE for container initialization</li>
 *   <li>Verifies beans.xml presence in META-INF/</li>
 *   <li>Ensures BeanManager is accessible</li>
 *   <li>Supports development mode configuration</li>
 *   <li>Allows bean class additions and alternatives</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Create one test class per module</li>
 *   <li>Override modifyWeldContainer() for custom configurations</li>
 *   <li>Keep container modifications minimal</li>
 *   <li>Use dev-mode only when necessary</li>
 *   <li>Document any special container configurations</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see WeldContainer
 * @see BeanManager
 * @see Weld
 */
public class BaseModuleConsistencyTest {

    @Test
    protected void shouldStartUpContainer() {
        try (var weld = modifyWeldContainer(new Weld()).initialize()) {
            assertNotNull(weld.select(BeanManager.class),
                    "Unable to acquire an instance of javax.enterprise.inject.spi.BeanManager");
        }
    }

    @Test
    protected void moduleShouldProvideBeansXml() {
        assertTrue(Path.of("src", "main", "resources", "META-INF", "beans.xml").toFile().exists(),
                "It is best-practice that each module provides a beans.xml");
    }

    /**
     * Use the callback to modify the Weld-testcontainer, like setting dev-mode or
     * adding beans.
     *
     * @param weld to be modified
     * @return the modified weld
     */
    protected Weld modifyWeldContainer(Weld weld) {
        return weld;
    }

}
