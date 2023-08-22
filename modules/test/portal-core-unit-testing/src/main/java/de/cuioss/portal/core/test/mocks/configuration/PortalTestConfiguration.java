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

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static de.cuioss.tools.base.Preconditions.checkArgument;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.application.ProjectStage;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.source.AbstractPortalConfigSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.tools.collect.MapBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock variant of configuration, overwriting all other configuration elements.
 * <em>Caution:</em> The described Configuration only works in context of junit
 * 5
 * <h2>Test Setup</h2>
 * <p>
 * In order to activate you add {@link EnablePortalConfiguration} to your test
 * class
 * <p>
 * Now you can inject the {@link PortalTestConfiguration}
 *
 * <pre>
 * <code>
 *   &#64;Inject
 *   &#64;PortalConfigurationSource
 *   private PortalTestConfiguration configuration;
 * </code>
 * </pre>
 *
 * and use it like:
 *
 * <pre>
 * <code>
 *   configuration.put("key1", "value1");
 *   configuration.put("key2", "value2");
 *   configuration.fireEvent();
 * </code>
 * </pre>
 *
 * or like
 *
 * <pre>
 * <code>
 *   configuration.fireEvent("key1", "value1");
 *   configuration.fireEvent("key1", "value1", "key2", "value2");
 * </code>
 * </pre>
 *
 * To explicitly remove/erase a property use {@link #remove(String)} or
 * {@link #removeAll()}. This sets the given property value to an empty string,
 * which in MP-Config terms defines the removal of a property.
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@Priority(PortalTestConfiguration.PRIORITY)
@PortalConfigurationSource
@EqualsAndHashCode(of = "properties", callSuper = true)
@ToString(of = "properties")
// Keep in sync with de.icw.portal.authentication.tests.PortalTestConfiguration
public class PortalTestConfiguration extends AbstractPortalConfigSource {

    public static final int PRIORITY = PortalPriorities.PORTAL_ASSEMBLY_LEVEL + 20;

    private static final Map<String, String> properties = synchronizedMap(new HashMap<>());

    /** Contains the delta config since the last fireEvent */
    private static final Map<String, String> delta = synchronizedMap(new HashMap<>());

    @Inject
    @ConfigurationSourceChangeEvent
    private Event<Map<String, String>> configurationChangeEvent;

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    /**
     * Fires the current status as {@link ConfigurationSourceChangeEvent}
     */
    public void fireEvent() {
        if (!delta.isEmpty()) {
            configurationChangeEvent.fire(MapBuilder.copyFrom(delta).toImmutableMap());
            handlePortalConfigDir();
            delta.clear();
        }
    }

    /**
     * Fires the given map directly as {@link ConfigurationSourceChangeEvent}
     *
     * @param deltaMap the deltaMap implicitly fired as
     *                 {@link ConfigurationSourceChangeEvent}
     */
    public void fireEvent(final Map<String, String> deltaMap) {
        deltaMap.forEach(this::put);
        fireEvent();
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
        properties.put(key, value);
        delta.put(key, value);
    }

    /**
     * Similar to {@link #fireEvent(Map)} but without firing an event.
     *
     * @param map to be added to this config source
     */
    public void putAll(final Map<String, String> map) {
        checkArgument(null != map, "map must not be null");
        map.forEach(this::put);
    }

    /**
     * Clears the local storage and the delta map. Usually used in
     * {@link org.junit.jupiter.api.BeforeEach}.
     *
     * @see #removeAll()
     */
    public void clear() {
        delta.clear();
        properties.clear();
        System.clearProperty(PortalConfigurationKeys.PORTAL_CONFIG_DIR);
    }

    /**
     * If the key exists in the local storage: Marks it as removed in the delta map
     * and removes it from the local storage.
     *
     * @param key
     */
    public void remove(final String key) {
        if (properties.containsKey(key)) {
            delta.put(key, "");
            properties.remove(key);
        }
    }

    /**
     * Marks all current properties as removed in the delta map. Removes all
     * properties from the local storage.
     */
    public void removeAll() {
        properties.forEach((k, v) -> delta.put(k, ""));
        properties.clear();
    }

    /**
     * @param projectStage
     */
    public void setPortalProjectStage(final de.cuioss.portal.configuration.application.ProjectStage projectStage) {
        fireEvent(PortalConfigurationKeys.PORTAL_STAGE, projectStage.name().toLowerCase());
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
    public int getPortalPriority() {
        return PRIORITY;
    }

    @Override
    public Map<String, String> getProperties() {
        return immutableMap(properties);
    }

    private void handlePortalConfigDir() {
        if (delta.containsKey(PORTAL_CONFIG_DIR)) {
            final var newValue = delta.get(PORTAL_CONFIG_DIR);
            if ("".equals(newValue)) {
                System.clearProperty(PORTAL_CONFIG_DIR);
            } else {
                System.setProperty(PORTAL_CONFIG_DIR, delta.get(PORTAL_CONFIG_DIR));
            }
        }
    }
}
