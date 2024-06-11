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

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.configuration.impl.schedule.FileWatcherServiceImpl;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import io.smallrye.config.inject.ConfigProducer;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AddBeanClasses({ConfigProducer.class, PortalConfigProducer.class, FileWatcherServiceImpl.class,
    PortalConfigurationMock.class,
    InstallationFileConfigSource.class, InstallationProductionFileConfigSource.class})
@EnableAutoWeld
@EnableTestLogger(debug = {AbstractInstallationConfigSource.class})
class PortalInstallationConfigurationTest {

    private static final Path CONFIG_DIR = Paths.get("target/test-classes/META-INF/installation-configuration-test");
    private static final String PROPERTY_VALUE_PROD = "property_value_b";

    final TypedGenerator<String> keyValueGenerator = Generators.letterStrings(10, 15);

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
    }

    @AfterAll
    static void afterAll() {
        System.clearProperty(PORTAL_CONFIG_DIR);
    }

    @Test
    void productionFileTakesPrecedence() {
        assertEquals(PROPERTY_VALUE_PROD, keyValue.get());
    }


}
