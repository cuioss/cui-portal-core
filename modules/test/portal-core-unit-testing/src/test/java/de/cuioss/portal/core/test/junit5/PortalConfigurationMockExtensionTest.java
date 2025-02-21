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
package de.cuioss.portal.core.test.junit5;

import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = {
        "key1:value1",
        "key2:value2"
})
@DisplayName("Tests PortalConfigurationMockExtension")
class PortalConfigurationMockExtensionTest {

    @Inject
    private PortalTestConfiguration configuration;

    @Test
    @DisplayName("Should initialize configuration from annotation")
    void shouldInitializeConfigurationFromAnnotation() {
        assertNotNull(configuration, "Configuration should be injected");
        assertEquals("value1", configuration.getValue("key1"), "Should have key1 value");
        assertEquals("value2", configuration.getValue("key2"), "Should have key2 value");
    }

    @Test
    @DisplayName("Should handle configuration updates")
    void shouldHandleConfigurationUpdates() {
        // Add new value
        configuration.update("key3", "value3");
        assertEquals("value3", configuration.getValue("key3"),
                "Should store new value");

        // Update existing value
        configuration.update("key1", "updated");
        assertEquals("updated", configuration.getValue("key1"),
                "Should update existing value");
    }

    @Test
    @DisplayName("Should handle configuration removal")
    void shouldHandleConfigurationRemoval() {
        // Remove single key
        configuration.remove("key1");
        assertNull(configuration.getValue("key1"),
                "Removed key should return null");

        // Clear all configuration
        configuration.clear();
        assertNull(configuration.getValue("key2"),
                "All keys should be cleared");
    }

    @Test
    @DisplayName("Should handle project stage configuration")
    void shouldHandleProjectStageConfiguration() {
        // Set development stage
        configuration.development();
        assertEquals("development", configuration.getValue(PORTAL_STAGE),
                "Should set development stage");

        // Set production stage
        configuration.production();
        assertEquals("production", configuration.getValue(PORTAL_STAGE),
                "Should set production stage");

        // Clear stage
        configuration.remove(PORTAL_STAGE);
        assertNull(configuration.getValue(PORTAL_STAGE),
                "Stage should be cleared");
    }

    @Test
    @DisplayName("Should handle multiple key updates")
    void shouldHandleMultipleKeyUpdates() {
        // Update multiple keys at once
        configuration.update("key4", "value4", "key5", "value5");

        assertEquals("value4", configuration.getValue("key4"),
                "Should store first new value");
        assertEquals("value5", configuration.getValue("key5"),
                "Should store second new value");
    }
}
