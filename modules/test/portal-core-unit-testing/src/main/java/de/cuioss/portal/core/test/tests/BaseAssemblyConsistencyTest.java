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
 * Base class for testing the consistency and structure of Portal assembly modules.
 * Specializes in verifying web application assembly configuration and CDI container
 * initialization. This is a web-focused variant of {@link BaseModuleConsistencyTest}.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Web application structure validation</li>
 *   <li>CDI container initialization testing</li>
 *   <li>WEB-INF/beans.xml verification</li>
 *   <li>BeanManager availability testing</li>
 *   <li>Assembly module structure validation</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic assembly test:
 * <pre>
 * public class MyWebAppTest extends BaseAssemblyConsistencyTest {
 *     // Uses default configuration
 * }
 * </pre>
 *
 * Custom assembly test:
 * <pre>
 * public class CustomWebAppTest extends BaseAssemblyConsistencyTest {
 *     &#64;Test
 *     public void shouldHaveCustomConfiguration() {
 *         Path configPath = Path.of("src", "main", "webapp", "WEB-INF", "custom-config.xml");
 *         assertTrue(configPath.toFile().exists(),
 *             "Custom configuration file should exist");
 *     }
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Verifies beans.xml in WEB-INF directory</li>
 *   <li>Uses Weld SE for container testing</li>
 *   <li>Ensures proper web application structure</li>
 *   <li>Validates CDI configuration</li>
 *   <li>Tests BeanManager availability</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Create one test class per web assembly</li>
 *   <li>Keep web application structure standard-compliant</li>
 *   <li>Place beans.xml in WEB-INF directory</li>
 *   <li>Add custom tests for assembly-specific requirements</li>
 *   <li>Document any deviations from standard structure</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see BaseModuleConsistencyTest
 * @see WeldContainer
 * @see BeanManager
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
