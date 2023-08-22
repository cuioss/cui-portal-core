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
package de.cuioss.portal.configuration.impl.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.configuration.impl.source.HighReloadableConfigSource;
import de.cuioss.portal.configuration.impl.source.LowReloadableConfigSource;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import de.cuioss.tools.collect.MapBuilder;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ PortalConfigProducer.class })
class ConfigChangeObserverTest implements ShouldBeNotNull<ConfigChangeObserver> {

    private static final String CHILD_KEY = "child.key";
    private static final String CHILD_CHILD_KEY = "child.child.key";
    private static final String CHILD_KEY_W_DEFAULT = "child.key.with.default";
    private static final String PARENT_KEY = "parent.key";

    private static final Path ARBITRARY_PROPERTIES_PATH = Paths
            .get("target/test-classes/META-INF/config-change-observer-test/arbitrary.properties");

    @Inject
    @PortalInitializer
    @Getter
    private ConfigChangeObserver underTest;

    @Inject
    @ConfigProperty(name = PARENT_KEY)
    private Provider<String> parentValue;

    @Inject
    @ConfigProperty(name = CHILD_KEY)
    private Provider<String> valueWoDefault;

    @Inject
    @ConfigProperty(name = CHILD_KEY_W_DEFAULT)
    private Provider<String> valueWithDefault;

    private Map<String, String> deltaMap;

    private final HighReloadableConfigSource highTestConfigSource = (HighReloadableConfigSource) getConfigSource(
            HighReloadableConfigSource.NAME);

    private final LowReloadableConfigSource lowTestConfigSource = (LowReloadableConfigSource) getConfigSource(
            LowReloadableConfigSource.NAME);

    @Test
    void deltaMapContainsAffectedKeys() {
        deltaMap = null;
        lowTestConfigSource.addProperty(CHILD_KEY, "${" + PARENT_KEY + "}");
        lowTestConfigSource.addProperty(CHILD_KEY_W_DEFAULT, "${" + PARENT_KEY + ":default}");
        lowTestConfigSource.addProperty(CHILD_CHILD_KEY, "${" + CHILD_KEY + "}");

        highTestConfigSource.addReloadedProperty(PARENT_KEY, "parent.value");
        highTestConfigSource.setPath(ARBITRARY_PROPERTIES_PATH.toString());

        underTest.fileChangeListener(Paths.get(highTestConfigSource.getPath()));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> null != deltaMap);

        assertNotNull(deltaMap, "delta should have been fired");
        assertEquals(4, deltaMap.size(), "expecting 4 changed/affected keys");
        assertTrue(deltaMap.containsKey(PARENT_KEY));
        assertTrue(deltaMap.containsKey(CHILD_KEY));
        assertTrue(deltaMap.containsKey(CHILD_KEY_W_DEFAULT));
        assertTrue(deltaMap.containsKey(CHILD_CHILD_KEY), "Indirections should be handled too!");
    }

    @Test
    void deltaMapOfPlaceholderConfigLoop() {
        deltaMap = null;
        lowTestConfigSource.addProperty(CHILD_KEY, "${" + CHILD_CHILD_KEY + "}");
        lowTestConfigSource.addProperty(CHILD_KEY_W_DEFAULT, "${" + CHILD_KEY + ":default}");
        lowTestConfigSource.addProperty(CHILD_CHILD_KEY, "${" + CHILD_KEY + "}");
        lowTestConfigSource.addProperty("unaffected.key", "unaffected.value");

        highTestConfigSource.addReloadedProperty(CHILD_KEY, "parent.child.value");
        highTestConfigSource.addReloadedProperty(CHILD_CHILD_KEY, "parent.child.child.value");
        highTestConfigSource.setPath(ARBITRARY_PROPERTIES_PATH.toString());

        underTest.fileChangeListener(Paths.get(highTestConfigSource.getPath()));

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> null != deltaMap);

        assertNotNull(deltaMap, "delta should have been fired");
        assertEquals(3, deltaMap.size(), "expecting 3 changed/affected keys");
        assertTrue(deltaMap.containsKey(CHILD_KEY), deltaMap.toString());
        assertTrue(deltaMap.containsKey(CHILD_CHILD_KEY), deltaMap.toString());
        assertTrue(deltaMap.containsKey(CHILD_KEY_W_DEFAULT), deltaMap.toString());
    }

    private static ConfigSource getConfigSource(final String name) {
        for (ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
            if (name.equals(configSource.getName())) {
                return configSource;
            }
        }

        return null;
    }

    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        this.deltaMap = MapBuilder.copyFrom(deltaMap).toImmutableMap();
    }
}
