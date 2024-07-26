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
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnablePortalConfiguration
class PortalTestConfigurationTest implements ShouldBeNotNull<PortalTestConfiguration> {

    @Inject
    @Getter
    private PortalTestConfiguration underTest;

    @Test
    void shouldHandleProjectStage() {
        assertNull(underTest.getValue(PORTAL_STAGE));
        underTest.development();
        assertConfigPresent(PORTAL_STAGE, ProjectStage.DEVELOPMENT.name().toLowerCase());
        underTest.production();
        assertConfigPresent(PORTAL_STAGE, ProjectStage.PRODUCTION.name().toLowerCase());
    }

    @Test
    void shouldAddAndRemoveConfig() {
        var key = letterStrings(3, 8).next();
        var value = letterStrings(3, 8).next();

        assertNull(underTest.getValue(key));
        assertConfigNotPresent(key);

        underTest.put(key, value);
        underTest.fireEvent();
        assertConfigPresent(key, value);

        underTest.remove(key);
        underTest.fireEvent();
        assertConfigNotPresent(key);
    }

    @Test
    void shouldHandleFireEventDirectly() {
        var key = letterStrings(3, 8).next();
        var value = letterStrings(3, 8).next();

        assertNull(underTest.getValue(key));
        assertConfigNotPresent(key);

        underTest.fireEvent(key, value);
        assertConfigPresent(key, value);

        underTest.removeAll();
        underTest.fireEvent();
        assertConfigNotPresent(key);
        underTest.removeAll();
    }

    @Test
    void shouldHandleFireMultipleEventDirectly() {
        var key = letterStrings(3, 8).next();
        var key2 = letterStrings(3, 8).next();
        var value = letterStrings(3, 8).next();
        var value2 = letterStrings(3, 8).next();

        assertNull(underTest.getValue(key));
        assertConfigNotPresent(key);

        underTest.fireEvent(key, value, key2, value2);
        assertConfigPresent(key, value);
        assertConfigPresent(key2, value2);

        underTest.removeAll();
        underTest.fireEvent();
        assertConfigNotPresent(key);
        assertConfigNotPresent(key2);
    }

    @Test
    void shouldHandleFireMultiplePut() {
        var key = letterStrings(3, 8).next();
        var key2 = letterStrings(3, 8).next();
        var value = letterStrings(3, 8).next();
        var value2 = letterStrings(3, 8).next();

        assertNull(underTest.getValue(key));
        assertConfigNotPresent(key);

        underTest.putAll(immutableMap(key, value, key2, value2));
        underTest.fireEvent();
        assertConfigPresent(key, value);
        assertConfigPresent(key2, value2);

        underTest.removeAll();
        underTest.fireEvent();
        assertConfigNotPresent(key);
        assertConfigNotPresent(key2);

        underTest.fireEvent(immutableMap(key, value, key2, value2));
        assertConfigPresent(key, value);
        assertConfigPresent(key2, value2);

        assertThrows(IllegalArgumentException.class, () -> underTest.putAll(null));
    }

    void assertConfigPresent(String key, String value) {
        assertEquals(value, underTest.getValue(key));
        var resolved = ConfigurationHelper.resolveConfigProperty(key);
        assertTrue(resolved.isPresent(), "Resolved configuration property '" + key + "' is not present");
        assertEquals(value, resolved.get(), "Resolved configuration property '" + key + "' is not resolved");
    }

    void assertConfigNotPresent(String key) {
        assertNull(underTest.getValue(key));
        var resolved = ConfigurationHelper.resolveConfigProperty(key);
        assertFalse(resolved.isPresent(), "Resolved configuration property '" + key + "' is not present");
    }
}
