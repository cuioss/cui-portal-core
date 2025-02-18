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

import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoWeld
@EnablePortalConfiguration
@DisplayName("PortalTestConfiguration Tests")
class PortalTestConfigurationTest implements ShouldBeNotNull<PortalTestConfiguration> {

    @Inject
    @Getter
    private PortalTestConfiguration underTest;

    @BeforeEach
    void setUp() {
        underTest.removeAll();
    }

    @Nested
    @DisplayName("Project Stage Management")
    class ProjectStageTest {

        @Test
        @DisplayName("Should handle development stage")
        void shouldHandleDevelopmentStage() {
            assertNull(underTest.getValue(PORTAL_STAGE));
            underTest.development();
            assertConfigPresent(PORTAL_STAGE, ProjectStage.DEVELOPMENT.name().toLowerCase());
        }

        @Test
        @DisplayName("Should handle production stage")
        void shouldHandleProductionStage() {
            assertNull(underTest.getValue(PORTAL_STAGE));
            underTest.production();
            assertConfigPresent(PORTAL_STAGE, ProjectStage.PRODUCTION.name().toLowerCase());
        }

        @Test
        @DisplayName("Should handle stage transitions")
        void shouldHandleStageTransitions() {
            underTest.development();
            assertConfigPresent(PORTAL_STAGE, ProjectStage.DEVELOPMENT.name().toLowerCase());

            underTest.production();
            assertConfigPresent(PORTAL_STAGE, ProjectStage.PRODUCTION.name().toLowerCase());

            underTest.development();
            assertConfigPresent(PORTAL_STAGE, ProjectStage.DEVELOPMENT.name().toLowerCase());
        }

        @Test
        @DisplayName("Should handle explicit stage setting")
        void shouldHandleExplicitStageSet() {
            underTest.setPortalProjectStage(ProjectStage.DEVELOPMENT);
            assertConfigPresent(PORTAL_STAGE, ProjectStage.DEVELOPMENT.name().toLowerCase());

            underTest.setPortalProjectStage(ProjectStage.PRODUCTION);
            assertConfigPresent(PORTAL_STAGE, ProjectStage.PRODUCTION.name().toLowerCase());
        }
    }

    @Nested
    @DisplayName("Configuration Operations")
    class ConfigurationOperationsTest {

        @Test
        @DisplayName("Should handle single key operations")
        void shouldHandleSingleKeyOperations() {
            var key = letterStrings(3, 8).next();
            var value = letterStrings(3, 8).next();

            assertNull(underTest.getValue(key));
            assertConfigNotPresent(key);

            underTest.update(key, value);
            assertConfigPresent(key, value);

            underTest.remove(key);
            assertConfigNotPresent(key);
        }

        @Test
        @DisplayName("Should handle multiple key operations")
        void shouldHandleMultipleKeyOperations() {
            var key1 = letterStrings(3, 8).next();
            var key2 = letterStrings(3, 8).next();
            var value1 = letterStrings(3, 8).next();
            var value2 = letterStrings(3, 8).next();

            underTest.update(key1, value1, key2, value2);
            assertConfigPresent(key1, value1);
            assertConfigPresent(key2, value2);

            underTest.removeAll();
            assertConfigNotPresent(key1);
            assertConfigNotPresent(key2);
        }

        @Test
        @DisplayName("Should handle map updates")
        void shouldHandleMapUpdates() {
            var key1 = letterStrings(3, 8).next();
            var key2 = letterStrings(3, 8).next();
            var value1 = letterStrings(3, 8).next();
            var value2 = letterStrings(3, 8).next();

            var configMap = immutableMap(key1, value1, key2, value2);
            underTest.update(configMap);

            assertConfigPresent(key1, value1);
            assertConfigPresent(key2, value2);
        }

        @Test
        @DisplayName("Should handle value updates")
        void shouldHandleValueUpdates() {
            var key = letterStrings(3, 8).next();
            var value1 = letterStrings(3, 8).next();
            var value2 = letterStrings(3, 8).next();

            underTest.update(key, value1);
            assertConfigPresent(key, value1);

            underTest.update(key, value2);
            assertConfigPresent(key, value2);
        }

        @Test
        @DisplayName("Should handle clear operation")
        void shouldHandleClear() {
            var key1 = letterStrings(3, 8).next();
            var key2 = letterStrings(3, 8).next();
            var value1 = letterStrings(3, 8).next();
            var value2 = letterStrings(3, 8).next();

            underTest.update(key1, value1, key2, value2);
            assertConfigPresent(key1, value1);
            assertConfigPresent(key2, value2);

            underTest.clear();
            assertConfigNotPresent(key1);
            assertConfigNotPresent(key2);
            assertTrue(underTest.getPropertyNames().isEmpty(),
                    "Property names should be empty after clear");
        }
    }

    @Nested
    @DisplayName("ConfigSource Implementation")
    class ConfigSourceTest {

        @Test
        @DisplayName("Should provide correct name")
        void shouldProvideName() {
            assertEquals("PortalTestConfiguration", underTest.getName(),
                    "Name should be class simple name");
        }

        @Test
        @DisplayName("Should have correct ordinal")
        void shouldHaveCorrectOrdinal() {
            var ordinal = underTest.getOrdinal();
            assertTrue(ordinal > ConfigSource.DEFAULT_ORDINAL,
                    "Ordinal should be greater than DEFAULT_ORDINAL");
            assertTrue(ordinal < 200,
                    "Ordinal should not exceed 199 to avoid conflicts with SmallRye");
        }

        @Test
        @DisplayName("Should provide property names")
        void shouldProvidePropertyNames() {
            var key = letterStrings(3, 8).next();
            var value = letterStrings(3, 8).next();

            assertTrue(underTest.getPropertyNames().isEmpty(),
                    "Initial property names should be empty");

            underTest.update(key, value);
            assertTrue(underTest.getPropertyNames().contains(key),
                    "Property names should contain added key");
            assertEquals(1, underTest.getPropertyNames().size(),
                    "Property names should have exactly one entry");
        }

        @Test
        @DisplayName("Should provide properties map")
        void shouldProvidePropertiesMap() {
            var key = letterStrings(3, 8).next();
            var value = letterStrings(3, 8).next();

            assertTrue(underTest.getProperties().isEmpty(),
                    "Initial properties should be empty");

            underTest.update(key, value);
            Map<String, String> properties = underTest.getProperties();
            assertEquals(value, properties.get(key),
                    "Properties should contain correct value");
            assertEquals(1, properties.size(),
                    "Properties should have exactly one entry");

            // Verify immutability
            assertThrows(UnsupportedOperationException.class,
                    () -> properties.put(key, value),
                    "Properties map should be immutable");
        }

        @Test
        @DisplayName("Should handle config ordinal from source")
        void shouldHandleConfigOrdinalFromSource() {
            var defaultOrdinal = underTest.getOrdinal();
            var customOrdinal = 150;

            underTest.update(ConfigSource.CONFIG_ORDINAL, String.valueOf(customOrdinal));
            assertEquals(customOrdinal, underTest.getOrdinal(),
                    "Should use custom ordinal when set");

            underTest.remove(ConfigSource.CONFIG_ORDINAL);
            assertEquals(defaultOrdinal, underTest.getOrdinal(),
                    "Should revert to default ordinal when custom is removed");

            // Invalid ordinal
            underTest.update(ConfigSource.CONFIG_ORDINAL, "invalid");
            assertEquals(defaultOrdinal, underTest.getOrdinal(),
                    "Should use default ordinal when custom is invalid");
        }
    }

    void assertConfigPresent(String key, String value) {
        assertEquals(value, underTest.getValue(key),
                "Config value should be present and match");
        var resolved = ConfigurationHelper.resolveConfigProperty(key);
        assertTrue(resolved.isPresent(),
                "Resolved configuration property '" + key + "' should be present");
        assertEquals(value, resolved.get(),
                "Resolved configuration property '" + key + "' should match expected value");
    }

    void assertConfigNotPresent(String key) {
        assertNull(underTest.getValue(key),
                "Config value should not be present");
        var resolved = ConfigurationHelper.resolveConfigProperty(key);
        assertFalse(resolved.isPresent(),
                "Resolved configuration property '" + key + "' should not be present");
    }
}
