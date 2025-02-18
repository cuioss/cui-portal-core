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
package de.cuioss.portal.core.test.tests.configuration;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

/**
 * MicroProfile ConfigSource implementation that provides the application context name
 * for unit testing environments. Specifically defines the {@code application.context.name}
 * property with a fixed value of "unit-test".
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Static configuration property provision</li>
 *   <li>Immutable property storage</li>
 *   <li>Thread-safe implementation</li>
 *   <li>Consistent context naming</li>
 *   <li>Test environment identification</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Direct property access:
 * <pre>
 * ConfigSource source = new ApplicationContextNameConfigSource();
 * String contextName = source.getValue("application.context.name");
 * assertEquals("unit-test", contextName);
 * </pre>
 *
 * Service loader registration:
 * <pre>
 * // In META-INF/services/org.eclipse.microprofile.config.spi.ConfigSource
 * de.cuioss.portal.core.test.tests.configuration.ApplicationContextNameConfigSource
 *
 * // Then in your test
 * Config config = ConfigProvider.getConfig();
 * String contextName = config.getValue("application.context.name", String.class);
 * assertEquals("unit-test", contextName);
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Implements MicroProfile {@link ConfigSource} interface</li>
 *   <li>Uses immutable map for property storage</li>
 *   <li>Provides single configuration property</li>
 *   <li>Returns class name as config source name</li>
 *   <li>Thread-safe by design</li>
 * </ul>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use in test environments only</li>
 *   <li>Register via service loader for automatic discovery</li>
 *   <li>Keep property value consistent across tests</li>
 *   <li>Document usage in test documentation</li>
 *   <li>Consider priority when multiple sources exist</li>
 * </ul>
 *
 * @author Sven Haag
 * @since 1.0
 * @see ConfigSource
 * @see org.eclipse.microprofile.config.Config
 */
public class ApplicationContextNameConfigSource implements ConfigSource {

    private static final Map<String, String> PROPERTIES = immutableMap("application.context.name", "unit-test");

    @Override
    public Map<String, String> getProperties() {
        return PROPERTIES;
    }

    @Override
    public Set<String> getPropertyNames() {
        return PROPERTIES.keySet();
    }

    @Override
    public String getValue(final String key) {
        return PROPERTIES.get(key);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
