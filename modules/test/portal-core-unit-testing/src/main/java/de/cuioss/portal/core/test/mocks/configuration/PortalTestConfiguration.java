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

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static java.util.Collections.synchronizedMap;

/**
 * Mock implementation of the Portal configuration system for unit testing.
 * This class provides a high-priority configuration source that overrides all other
 * configuration elements in a test environment.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Dynamic configuration updates during tests</li>
 *   <li>High-priority overrides (above standard config sources)</li>
 *   <li>Project stage management</li>
 *   <li>Property removal support</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 *
 * <h2>Setup</h2>
 * Add {@link EnablePortalConfiguration} to your test class:
 * <pre>
 * &#64;EnablePortalConfiguration
 * class ConfigurationTest {
 *     &#64;Inject
 *     &#64;PortalConfigurationSource
 *     private PortalTestConfiguration configuration;
 * }
 * </pre>
 *
 * <h2>Usage Examples</h2>
 *
 * Single property update:
 * <pre>
 * configuration.update("portal.some.key", "value");
 * </pre>
 *
 * Multiple property updates:
 * <pre>
 * // Method 1: Key-value pairs
 * configuration.update("key1", "value1", "key2", "value2");
 *
 * // Method 2: Map-based
 * Map<String, String> properties = new HashMap<>();
 * properties.put("key1", "value1");
 * properties.put("key2", "value2");
 * configuration.update(properties);
 * </pre>
 *
 * Project stage management:
 * <pre>
 * // Set to development mode
 * configuration.development();
 *
 * // Set to production mode
 * configuration.production();
 *
 * // Set custom stage
 * configuration.setPortalProjectStage(ProjectStage.STAGING);
 * </pre>
 *
 * Property removal:
 * <pre>
 * // Remove single property
 * configuration.remove("portal.some.key");
 *
 * // Remove all properties
 * configuration.removeAll();
 *
 * // Clear configuration (for test setup)
 * configuration.clear();
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Thread-safe using synchronized collections</li>
 *   <li>High priority (PORTAL_ASSEMBLY_LEVEL + 20) to override other sources</li>
 *   <li>Property removal is handled by setting empty string value</li>
 *   <li>Implements MicroProfile Config {@link ConfigSource} interface</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see EnablePortalConfiguration
 * @see ConfigSource
 * @see ProjectStage
 */
@ApplicationScoped
@Priority(PortalTestConfiguration.PRIORITY)
@EqualsAndHashCode(of = "properties")
@ToString(of = "properties")
public class PortalTestConfiguration implements ConfigSource {

    private static final CuiLogger LOGGER = new CuiLogger(PortalTestConfiguration.class);

    public static final int PRIORITY = PortalPriorities.PORTAL_ASSEMBLY_LEVEL + 20;

    private static final Map<String, String> properties = synchronizedMap(new HashMap<>());

    /**
     * Configures the given map
     */
    public void update(final Map<String, String> deltaMap) {
        properties.putAll(deltaMap);
    }

    /**
     * Shorthand for calling {@link #update(Map)} without the need for creating a
     * map
     *
     * @param key   of the entry
     * @param value of the entry
     */
    public void update(final String key, final String value) {
        update(immutableMap(key, value));
    }

    /**
     * Shorthand for calling {@link #update(Map)} without the need for creating a
     * map
     *
     * @param key1   of the entry1
     * @param value1 of the entry1
     * @param key2   of the entry2
     * @param value2 of the entry2
     */
    public void update(final String key1, final String value1, final String key2, final String value2) {
        update(immutableMap(key1, value1, key2, value2));
    }

    /**
     * Clears the local storage and the delta map. Usually used in
     * {@link org.junit.jupiter.api.BeforeEach}.
     *
     * @see #removeAll()
     */
    public void clear() {
        properties.clear();
    }

    /**
     * If the key exists in the local storage: Marks it as removed in the delta map
     * and removes it from the local storage.
     *
     * @param key to be removed
     */
    public void remove(final String key) {
        properties.remove(key);
    }

    /**
     * Marks all current properties as removed in the delta map. Removes all
     * properties from the local storage.
     */
    public void removeAll() {
        properties.clear();
    }

    /**
     * @param projectStage to be set
     */
    public void setPortalProjectStage(final de.cuioss.portal.common.stage.ProjectStage projectStage) {
        update(PortalConfigurationKeys.PORTAL_STAGE, projectStage.name().toLowerCase());
    }

    /**
     * Shorthand for calling {@link #setPortalProjectStage(ProjectStage)} with
     * {@link ProjectStage#DEVELOPMENT}
     */
    public void development() {
        setPortalProjectStage(ProjectStage.DEVELOPMENT);
    }

    /**
     * Shorthand for calling {@link #setPortalProjectStage(ProjectStage)} with
     * {@link ProjectStage#PRODUCTION}
     */
    public void production() {
        setPortalProjectStage(ProjectStage.PRODUCTION);
    }

    @Override
    public Map<String, String> getProperties() {
        return immutableMap(properties);
    }

    /**
     * @return {@link Class#getSimpleName()}
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Do not overwrite this function. Use {@link #getPortalPriority()} instead.
     * <p>
     * As Portal priorities are within the range 0-150, the final value will fit
     * nicely within the MP config source ordinals 100-250. Hence, all portal config
     * sources sit below the "EnvConfigSource".
     *
     * @return {@link ConfigSource#DEFAULT_ORDINAL} + {@link #getPortalPriority()}.
     */
    @Override
    public int getOrdinal() {
        return getConfigOrdinalFromSource().orElse(ConfigSource.DEFAULT_ORDINAL + getPortalPriority());
    }

    @Override
    public String getValue(final String key) {
        return getProperties().get(key);
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    /**
     * Provide a portal priority to reflect the importance of this config source.
     * Higher values have higher importance. Don't exceed a value of
     * <code>199</code> as this would conflict with the default config sources
     * provided by SmallRye.
     *
     * @return {@link PortalPriorities#PORTAL_CORE_LEVEL}
     */
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_CORE_LEVEL;
    }

    protected Optional<Integer> getConfigOrdinalFromSource() {
        final var configOrdinal = this.getValue(ConfigSource.CONFIG_ORDINAL);
        if (null != configOrdinal) {
            try {
                final var ordinal = Integer.parseInt(configOrdinal);
                LOGGER.trace("config_ordinal of {}={}", getName(), ordinal);
                return Optional.of(ordinal);
            } catch (final NumberFormatException nfe) {
                LOGGER.trace(nfe, "config_ordinal is not a number. source={}", getName());
            }
        }
        return Optional.empty();
    }
}
