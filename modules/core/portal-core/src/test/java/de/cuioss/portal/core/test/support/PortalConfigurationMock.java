package de.cuioss.portal.core.test.support;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.ConfigurationStorage;
import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationChangeInterceptor;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.PortalConfigurationStorage;
import de.cuioss.portal.configuration.application.ProjectStage;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.portal.configuration.source.AbstractPortalConfigSource;
import de.cuioss.portal.configuration.types.ConfigAsList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock variant of configuration, overwriting all other configuration elements.
 * <h2>Test Setup</h2>
 * <p>
 * In order to activate the needed classes you must add them by using
 * {@link AddBeanClasses}:
 *
 * <pre>
 * <code>
 * &#64;AdditionalClasses({ de.cuioss.portal.configuration.impl.producer.PortalConfigurationProducer.class })
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
 * <p>
 * If your configuration listens to {@link PortalConfigurationChangeEvent}s and
 * are annotated with {@link PortalConfigurationChangeInterceptor} you need to
 * add
 * {@code de.cuioss.portal.configuration.impl.PortalConfigurationChangeInterceptorImpl}
 * to {@link AddBeanClasses} as well.
 * </p>
 * Now you can inject the {@link PortalConfigurationMock}
 *
 * <pre>
 * <code>
 *   &#64;Inject
 *   &#64;PortalConfigurationSource
 *   private PortalConfigurationMock configuration;
 * </code>
 * </pre>
 *
 * Initialize it like
 *
 * <pre>
 * <code>
 *  &#64;Before
 *   public void beforeTest() {
 *      configuration.initializeConfigurationSystem();
 *   }
 * </code>
 * </pre>
 *
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
@PortalConfigurationSource
@PortalConfigurationStorage
@ApplicationScoped
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL)
@EqualsAndHashCode(of = "configurationMap", callSuper = false)
@ToString
public class PortalConfigurationMock extends AbstractPortalConfigSource implements ConfigurationStorage {

    private static final Map<String, String> configurationMap = new HashMap<>();

    @Inject
    @ConfigurationSourceChangeEvent
    private Event<Map<String, String>> configurationChangeEvent;

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @Override
    public Map<String, String> getConfigurationMap() {
        return immutableMap(configurationMap);
    }

    @Override
    public Map<String, String> getProperties() {
        return immutableMap(configurationMap);
    }

    /**
     * Fires the current status as {@link ConfigurationSourceChangeEvent}
     */
    public void fireEvent() {
        configurationChangeEvent.fire(configurationMap);
    }

    /**
     * Fires the given map directly as {@link ConfigurationSourceChangeEvent}
     *
     * @param deltaMap the deltaMap implicitly fired as
     *                 {@link ConfigurationSourceChangeEvent}
     */
    public void fireEvent(Map<String, String> deltaMap) {
        configurationMap.putAll(deltaMap);
        configurationChangeEvent.fire(deltaMap);
    }

    /**
     * Shorthand for calling {@link #fireEvent(Map)} without the need for creating a
     * map
     *
     * @param key   of the entry
     * @param value of the entry
     *
     */
    public void fireEvent(String key, String value) {
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
     *
     */
    public void fireEvent(String key1, String value1, String key2, String value2) {
        fireEvent(immutableMap(key1, value1, key2, value2));
    }

    /**
     * Initializes the configuration system explicitly. This is usually done by a
     * servlet-event but must be explicitly done if used in unit-tests
     */
    public void initializeConfigurationSystem() {
        final List<ApplicationInitializer> initializers = mutableList(applicationInitializers);
        Collections.sort(initializers);
        for (final ApplicationInitializer applicationInitializer : initializers) {
            applicationInitializer.initialize();
        }
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
    public void setPortalProjectStage(final de.cuioss.portal.configuration.application.ProjectStage projectStage) {
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
    public void updateConfigurationMap(final Map<String, String> map) {
        clear();
        configurationMap.putAll(map);
    }

    @Override
    public void updateConfigurationProperty(final String key, final String value) {
        put(key, value);
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_ASSEMBLY_LEVEL;
    }
}
