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
package de.cuioss.portal.configuration.standalone.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.configuration.impl.schedule.ConfigChangeObserver;
import de.cuioss.portal.configuration.impl.schedule.FileWatcherServiceImpl;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import io.smallrye.config.inject.ConfigProducer;

@AddBeanClasses({ ConfigProducer.class, PortalConfigProducer.class, FileWatcherServiceImpl.class,
        PortalConfigurationMock.class, InstallationConfigSourcePathInitializer.class, ConfigChangeObserver.class,
        InstallationFileConfigSource.class, InstallationProductionFileConfigSource.class })
@EnableAutoWeld
@EnableTestLogger(debug = { AbstractInstallationConfigSource.class })
class PortalInstallationConfigurationTest {

    private static final Path CONFIG_DIR = Paths.get("target/test-classes/META-INF/installation-configuration-test");
    private static final Path APPLICATION_YML = CONFIG_DIR.resolve("application.yml");
    private static final Path APPLICATION_PRODUCTION_YML = CONFIG_DIR.resolve("application-production.yml");
    private static final Path APPLICATION_USER_YML = CONFIG_DIR.resolve("application-user.yml");
    private static final String PROPERTY_VALUE_PROD = "property_value_b";
    private static final Path PATH_NOT_THERE = Paths.get("/not/there");

    final TypedGenerator<String> keyValueGenerator = Generators.letterStrings(10, 15);

    private Map<String, String> payload;

    @Inject
    @FileChangedEvent
    private Event<Path> fileChangeEvent;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Inject
    @ConfigProperty(name = "test.yaml.property")
    private Provider<String> keyValue;

    @BeforeEach
    void initConfigSystem() {
        configuration.clear();

        System.setProperty(PORTAL_CONFIG_DIR, CONFIG_DIR.toString());
        // needed to trigger InstallationConfigSourcePathInitializer
        configuration.put(PORTAL_CONFIG_DIR, CONFIG_DIR.toString());

        configuration.initializeConfigurationSystem();
        configuration.fireEvent();
    }

    @AfterAll
    static void afterAll() {
        System.clearProperty(PORTAL_CONFIG_DIR);
    }

    @Test
    void productionFileTakesPrecedence() {
        assertEquals(PROPERTY_VALUE_PROD, keyValue.get());
    }

    @Test
    void shouldReactOnFileChangeEvents() {
        // InvalidPath should be ignored
        payload = null;
        fileChangeEvent.fire(PATH_NOT_THERE);
        assertNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_YML); // initial loading of config source
        assertNull(payload);
        appendArbitraryProperty(APPLICATION_YML); // do actual config change
        fileChangeEvent.fire(APPLICATION_YML);
        assertNotNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_PRODUCTION_YML); // initial loading of config source
        assertNull(payload);
        appendArbitraryProperty(APPLICATION_PRODUCTION_YML); // do actual config change
        fileChangeEvent.fire(APPLICATION_PRODUCTION_YML);
        assertNotNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_USER_YML);
        assertNull(payload); // because file does not exist
    }

    private void appendArbitraryProperty(final Path file) {
        final var yamlProperty = System.lineSeparator() + keyValueGenerator.next() + ": " + keyValueGenerator.next();
        assertDoesNotThrow(() -> {
            Files.write(file, yamlProperty.getBytes(), StandardOpenOption.APPEND);
        }, "Could not append to file");
    }

    void providerChangeEventListener(@Observes @ConfigurationSourceChangeEvent final Map<String, String> eventMap) {
        payload = eventMap;
    }
}
