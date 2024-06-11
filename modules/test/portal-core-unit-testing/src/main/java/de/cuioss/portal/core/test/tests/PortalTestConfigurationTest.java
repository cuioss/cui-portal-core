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
package de.cuioss.portal.core.test.tests;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * There are several use-cases to test for the test config source.
 * <ul>
 * <li>It starts with an empty property map.</li>
 * <li>It only fires the delta to its previous config</li>
 * <li>It allows to explicitly set an empty property</li>
 * <li>It allows clearing of its local storage as a re-init for a new unit
 * test</li>
 * <li>Previously added and then removed properties are present in the delta
 * with an empty string value</li>
 * </ul>
 *
 * @author Sven Haag
 */
@EnableAutoWeld
public class PortalTestConfigurationTest {

    private static final String KEY1 = "key1";
    private static final String VALUE1 = "value1";

    private static final String KEY2 = "key2";
    private static final String VALUE2 = "value2";

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration underTest;

    private Map<String, String> configSourceChanges;

    @BeforeEach
    void beforeTest() {
        configSourceChanges = null;
        underTest.clear();

        assertNotNull(underTest.getProperties());
        assertTrue(underTest.getProperties().isEmpty());
        assertUnmodifiable(underTest.getProperties());
    }

    @AfterEach
    void afterTest() {
        assertUnmodifiable(underTest.getProperties());
    }

    @Test
    void addProperty() {
        underTest.fireEvent(KEY1, VALUE1);
        assertNotNull(configSourceChanges);
        assertEquals(1, configSourceChanges.size());
        assertEquals(VALUE1, configSourceChanges.get(KEY1));

        // should only fire delta!
        underTest.fireEvent(KEY2, VALUE2);
        assertNotNull(configSourceChanges);
        assertEquals(1, configSourceChanges.size());
        assertEquals(VALUE2, configSourceChanges.get(KEY2));
    }

    @Test
    void addMultipleProperties() {
        underTest.put(KEY1, VALUE1);
        underTest.put(KEY2, VALUE2);
        underTest.fireEvent();

        assertNotNull(configSourceChanges);
        assertEquals(2, configSourceChanges.size());
    }

    @Test
    void removeProperty() {
        // let's add a new property in the first place
        underTest.put(KEY1, VALUE1);
        underTest.fireEvent();
        assertNotNull(configSourceChanges);
        assertEquals(1, configSourceChanges.size());

        // then remove it
        underTest.remove(KEY1);
        underTest.fireEvent();
        assertNotNull(configSourceChanges);
        assertEquals("", configSourceChanges.get(KEY1), "key1 should be indicated as removed via an empty string");
    }

    @Test
    void explicitRemoval() {
        underTest.fireEvent(KEY1, "");
        assertEquals("", underTest.getProperties().get(KEY1));
        assertNotNull(configSourceChanges);
        assertEquals(1, configSourceChanges.size());
    }

    @Test
    void marksAllRemoved() {
        underTest.put(KEY1, VALUE1);
        underTest.put(KEY2, VALUE2);
        underTest.removeAll();
        underTest.fireEvent();

        // should receive a delta map with 2 empty keys
        assertNotNull(configSourceChanges);
        assertEquals(2, configSourceChanges.size());
    }

    @SuppressWarnings("squid:S1872")
    void assertUnmodifiable(final Map<String, String> map) {
        assertNotNull(map, "Could not assert unmodifiable map. map is null!");
        // access inner class for verification
        assertTrue(map.getClass().getSimpleName().equals("UnmodifiableMap"));
    }
}
