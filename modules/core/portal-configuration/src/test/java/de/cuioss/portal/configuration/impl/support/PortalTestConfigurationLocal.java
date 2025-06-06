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
package de.cuioss.portal.configuration.impl.support;

import de.cuioss.portal.common.priority.PortalPriorities;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.types.ConfigAsList;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.weld.junit5.auto.AddBeanClasses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;

/**
 * Mock variant of configuration, overwriting all other configuration elements.
 * <h2>Test Setup</h2>
 * <p>
 * In order to activate the needed classes you must add them by using
 * {@link AddBeanClasses}:
 *
 * <pre>
 * <code>
 * &#64;AddBeanClasses({ de.cuioss.portal.configuration.impl.source.PortalConfigSource.class })
 * </code>
 * </pre>
 * </p>
 * <p>
 * If you need the portal specific extension like {@link ConfigAsList} you need
 * to add
 * {@code de.cuioss.portal.configuration.impl.producer.PortalConfigProducer} to
 * {@link AddBeanClasses} as well.
 * </p>
 * <p>
 * If you want to add the portal-default configuration you need to add
 * {@code de.cuioss.portal.configuration.impl.source.PortalDefaultConfiguration}
 * to {@link AddBeanClasses} as well.
 * </p>
 * Now you can inject the {@link PortalTestConfigurationLocal}
 *
 * <pre>
 * <code>
 *   &#64;Inject
 *   &#64;PortalConfigurationSource
 *   private PortalTestConfigurationLocal configuration;
 * </code>
 * </pre>
 * <p>
 * Initialize it like
 *
 * <pre>
 * <code>
 *  &#64;BeforeEach
 *   public void beforeTest() {
 *      configuration.initializeConfigurationSystem();
 *   }
 * </code>
 * </pre>
 * <p>
 * and use it like:
 *
 * <pre>
 * <code>
 *   configuration.put("key1", "value1");
 *   configuration.put("key1", "value1");
 *   configuration.fireEvent();
 * </code>
 * </pre>
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@Priority(PortalTestConfigurationLocal.PRIORITY)
@EqualsAndHashCode(of = "configurationMap")
@ToString
public class PortalTestConfigurationLocal implements ConfigSource {

    public static final int PRIORITY = PortalPriorities.PORTAL_ASSEMBLY_LEVEL + 10;

    /**
     * needs to be static to work for both CDI and SPI MicroProfile config source
     */
    private static final Map<String, String> configurationMap = Collections.synchronizedMap(new HashMap<>());

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    /**
     * Tests config for "test.value.for.change")
     */
    public void fireEvent() {
        ConfigProvider.getConfig().getConfigValue("test.value.for.change");
    }

    /**
     * Updates the map
     */
    public void fireEvent(final Map<String, String> deltaMap) {
        configurationMap.putAll(deltaMap);
        ConfigProvider.getConfig().getConfigValue("test.value.for.change");
    }

    /**
     * Shorthand for calling {@link #fireEvent(Map)} without the need for creating a
     * map
     *
     * @param key   of the entry
     * @param value of the entry
     */
    public void fireEvent(final String key, final String value) {
        fireEvent(immutableMap(key, value));
    }

    /**
     * Shorthand for calling {@link #fireEvent(Map)} without the need for creating a
     * map
     *
     * @param key1   of the entry1
     * @param value1 of the entry1
     * @param key2   of the entry2
     * @param value2 of the entry2
     */
    public void fireEvent(final String key1, final String value1, final String key2, final String value2) {
        fireEvent(immutableMap(key1, value1, key2, value2));
    }

    /**
     * Adds a key / value pair
     *
     * @param key
     * @param value
     */
    public void put(final String key, final String value) {
        configurationMap.put(key, value);
    }

    /**
     * Clears the contained {@link Map}
     */
    public void clear() {
        configurationMap.clear();
    }

    /**
     * Remove a key if present.
     *
     * @param key
     */
    public void remove(final String key) {
        configurationMap.remove(key);
    }

    /**
     * @param projectStage
     */
    public void setPortalProjectStage(final ProjectStage projectStage) {
        put(PortalConfigurationKeys.PORTAL_STAGE, projectStage.name().toLowerCase());
        fireEvent();
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
        LOGGER.trace("Requested Properties: {}", configurationMap);
        return immutableMap(configurationMap);
    }

    @Override
    public Set<String> getPropertyNames() {
        LOGGER.trace("Requested Property Names: {}", configurationMap);
        return configurationMap.keySet();
    }

    @Override
    public int getOrdinal() {
        return PRIORITY;
    }

    @Override
    public String getValue(final String key) {
        return configurationMap.get(key);
    }

    @Override
    public String getName() {
        return "PortalTestConfigurationLocal[config-impl]";
    }

    private static final CuiLogger LOGGER = new CuiLogger(PortalTestConfigurationLocal.class);

}
