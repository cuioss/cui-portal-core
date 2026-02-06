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
package de.cuioss.portal.core.test.junit5;

import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration
class PortalTestConfigurationExtensionTest {

    @Inject
    private PortalTestConfiguration configuration;

    @Nested
    @DisplayName("Basic Configuration Tests")
    class BasicConfigurationTest {

        @Inject
        @ConfigProperty(name = "key1")
        private Provider<String> attribute;

        @Test
        @DisplayName("Should handle simple key-value configuration")
        void shouldHandleSimpleConfig() {
            configuration.update("key1", "value1");
            assertEquals("value1", attribute.get());
        }

        @Test
        @DisplayName("Should handle configuration with colons")
        void shouldHandleConfigWithColons() {
            configuration.update("key1", "value1:zzz");
            assertEquals("value1:zzz", attribute.get());
        }
    }

    @Nested
    @DisplayName("Dynamic Configuration Tests")
    class DynamicConfigurationTest {

        @Test
        @DisplayName("Should handle configuration changes")
        void shouldHandleConfigChanges() {
            configuration.update("dynamic.key", "initial");
            assertEquals("initial", configuration.getValue("dynamic.key"));

            configuration.update("dynamic.key", "updated");
            assertEquals("updated", configuration.getValue("dynamic.key"));
        }

        @Test
        @DisplayName("Should handle configuration removal")
        void shouldHandleConfigRemoval() {
            configuration.update("temp.key", "value");
            assertTrue(configuration.getPropertyNames().contains("temp.key"));

            configuration.remove("temp.key");
            assertFalse(configuration.getPropertyNames().contains("temp.key"));
        }
    }

    @Test
    @DisplayName("Configuration instance should be available")
    void shouldProvideConfiguration() {
        assertNotNull(configuration);
    }
}
