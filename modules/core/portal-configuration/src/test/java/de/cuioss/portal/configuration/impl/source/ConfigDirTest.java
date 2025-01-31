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
package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CUSTOMIZATION_DIR;
import static org.junit.jupiter.api.Assertions.*;

import io.smallrye.config.inject.ConfigProducer;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@EnableAutoWeld
@AddBeanClasses(ConfigProducer.class)
class ConfigDirTest {

    @Inject
    @ConfigProperty(name = PORTAL_CONFIG_DIR)
    private Provider<Optional<String>> configDir;

    @Inject
    @ConfigProperty(name = PORTAL_CUSTOMIZATION_DIR)
    private Provider<Optional<String>> customizationDir;

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty(PORTAL_CONFIG_DIR);
        System.clearProperty(PORTAL_CUSTOMIZATION_DIR);
    }

    @Test
    void defaultConfigDirPresent() {
        final var value = configDir.get();
        assertTrue(value.isPresent(), "portal default config dir should be present");

        System.setProperty(PORTAL_CONFIG_DIR, "elsewhere/");
        assertEquals("elsewhere/", configDir.get().get());
    }

    @Test
    void emptyConfigDirProperty() {
        System.setProperty(PORTAL_CONFIG_DIR, "");
        final var value = configDir.get();
        assertFalse(value.isPresent());
    }

}
