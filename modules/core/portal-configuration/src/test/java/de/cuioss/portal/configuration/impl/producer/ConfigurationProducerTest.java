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
package de.cuioss.portal.configuration.impl.producer;

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.types.*;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@EnableAutoWeld
@EnablePortalConfigurationLocal
@EnableTestLogger(trace = {PortalConfigProducer.class, ConfigurationHelper.class})
class ConfigurationProducerTest {

    private static final String CONFIGURATION_KEY = "configurationKey";
    private static final String LIST_SINGLE_VALUE = "list";
    private static final String LIST_TWO_VALUES = "list, more";
    private static final String LIST_DEFAULT_VALUE = "defaultlist";

    private static final String FILE_LOCATION = "src/test/resources/META-INF/test.properties";

    private static final String PATH_NOT_THERE = "/not/there";

    @AfterEach
    void after() {
        configuration.clear();
        configuration.fireEvent();
    }

    @Inject
    @Getter
    private PortalConfigProducer underTest;

    @Inject
    private PortalTestConfigurationLocal configuration;

    @Inject
    @ConfigAsList(name = CONFIGURATION_KEY)
    private Provider<List<String>> injectedListProvider;

    @Inject
    @ConfigAsList(name = CONFIGURATION_KEY, defaultValue = LIST_DEFAULT_VALUE)
    private Provider<List<String>> injectedListWithDefaultProvider;

    @Inject
    @ConfigAsSet(name = CONFIGURATION_KEY)
    private Provider<Set<String>> injectedSetProvider;

    @Inject
    @ConfigAsSet(name = CONFIGURATION_KEY, defaultValue = LIST_DEFAULT_VALUE)
    private Provider<Set<String>> injectedSetWithDefaultProvider;

    @Inject
    @ConfigAsFileLoader(name = CONFIGURATION_KEY)
    private Provider<FileLoader> injectedFileLoader;

    @Inject
    @ConfigAsFileLoader(name = CONFIGURATION_KEY, failOnNotAccessible = false)
    private Provider<FileLoader> injectedFileLoaderTolerant;

    @Inject
    @ConfigAsPath(name = CONFIGURATION_KEY)
    private Provider<Path> injectedPath;

    @Inject
    @ConfigAsPath(name = CONFIGURATION_KEY, failOnNotAccessible = false)
    private Provider<Path> injectedPathTolerant;

    @Inject
    @ConfigAsFileLoaderList(name = CONFIGURATION_KEY)
    private Provider<List<FileLoader>> injectedFileLoaderList;

    @Inject
    @ConfigAsFileLoaderList(name = CONFIGURATION_KEY, failOnNotAccessible = false)
    private Provider<List<FileLoader>> injectedFileLoaderListTolerant;

    @Inject
    @ConfigPropertyNullable(name = "not.there")
    private Provider<String> nullableProperty;

    @Inject
    @ConfigAsCacheConfig(name = "my-cache")
    private Provider<CacheConfig> myCacheConfig;

    @Inject
    @ConfigAsCacheConfig(name = "my-cache2", recordStatistics = false)
    private Provider<CacheConfig> myCacheConfigWithDisabledStats;

    @Nested
    class FileLoaderTests {

        @Test
        void shouldProduceFileLoaderList() {
            configuration.put(CONFIGURATION_KEY, FILE_LOCATION);
            configuration.fireEvent();

            assertEquals(1, injectedFileLoaderList.get().size());
        }

        @Test
        void shouldProduceFileLoaderListOnInvalidPath() {
            configuration.put(CONFIGURATION_KEY, PATH_NOT_THERE);
            configuration.fireEvent();
            assertEquals(1, injectedFileLoaderListTolerant.get().size());
        }

        @Test
        void shouldFailToProduceFileLoaderOnEmptyPath() {
            assertThrows(IllegalArgumentException.class, () -> injectedFileLoader.get());
        }

        @Test
        void shouldFailToProduceFileLoaderOnInvalidPath() {
            configuration.put(CONFIGURATION_KEY, PATH_NOT_THERE);
            configuration.fireEvent();
            assertThrows(IllegalArgumentException.class, () -> injectedFileLoader.get());
        }

        @Test
        void shouldProduceFileLoaderOnInvalidPath() {
            configuration.put(CONFIGURATION_KEY, PATH_NOT_THERE);
            configuration.fireEvent();
            assertNotNull(injectedFileLoaderTolerant.get());
        }

        @Test
        void shouldProduceFileLoader() {
            configuration.put(CONFIGURATION_KEY, FILE_LOCATION);
            configuration.fireEvent();

            assertNotNull(injectedFileLoader.get());
        }
    }

    @Nested
    class PathTests {

        @Test
        void shouldProducePathOnInvalidPath() {
            configuration.put(CONFIGURATION_KEY, PATH_NOT_THERE);
            configuration.fireEvent();
            assertNotNull(injectedPathTolerant.get());
        }

        @Test
        void shouldProducePath() {
            configuration.put(CONFIGURATION_KEY, FILE_LOCATION);
            configuration.fireEvent();

            assertNotNull(injectedPath.get());
        }

        @Test
        void shouldFailToProducePathEmptyPath() {
            assertThrows(IllegalArgumentException.class, () -> injectedPath.get());
        }
    }

    @Nested
    class ListTests {

        @Test
        void shouldProduceSplittedList() {
            // Property initially not there
            assertTrue(injectedListProvider.get().isEmpty());
            assertFalse(injectedListWithDefaultProvider.get().isEmpty());
            assertEquals(LIST_DEFAULT_VALUE, injectedListWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, LIST_SINGLE_VALUE);
            configuration.fireEvent();

            assertEquals(1, injectedListProvider.get().size());
            assertNotEquals(LIST_DEFAULT_VALUE, injectedListWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, LIST_TWO_VALUES);
            configuration.fireEvent();

            assertEquals(2, injectedListProvider.get().size());
            assertNotEquals(LIST_DEFAULT_VALUE, injectedListWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, "");
            configuration.fireEvent();
            assertTrue(injectedListProvider.get().isEmpty());
            assertFalse(injectedListWithDefaultProvider.get().isEmpty());
            assertEquals(LIST_DEFAULT_VALUE, injectedListWithDefaultProvider.get().iterator().next());
        }
    }

    @Nested
    class SetTests {

        @Test
        void shouldProduceSplittedSet() {
            // Property initially not there
            assertTrue(injectedSetProvider.get().isEmpty());
            assertFalse(injectedSetWithDefaultProvider.get().isEmpty());
            assertEquals(LIST_DEFAULT_VALUE, injectedSetWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, LIST_SINGLE_VALUE);
            configuration.fireEvent();

            assertEquals(1, injectedSetProvider.get().size());
            assertNotEquals(LIST_DEFAULT_VALUE, injectedSetWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, LIST_TWO_VALUES);
            configuration.fireEvent();

            assertEquals(2, injectedSetProvider.get().size());
            assertNotEquals(LIST_DEFAULT_VALUE, injectedSetWithDefaultProvider.get().iterator().next());

            configuration.put(CONFIGURATION_KEY, "");
            configuration.fireEvent();
            assertTrue(injectedSetProvider.get().isEmpty());
            assertFalse(injectedSetWithDefaultProvider.get().isEmpty());
            assertEquals(LIST_DEFAULT_VALUE, injectedSetWithDefaultProvider.get().iterator().next());
        }
    }

    @Nested
    class NullableTests {

        @Test
        void shouldAllowNullableInjection() {
            assertDoesNotThrow(() -> nullableProperty.get());
            assertNull(nullableProperty.get());
        }
    }

    @Nested
    class CacheConfigTests {

        @Test
        void cacheConfigRecordStatsDisabledIfPortalMetricsDisabled() {
            configuration.fireEvent(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "false");
            var cacheConfig = assertDoesNotThrow(() -> myCacheConfig.get());
            assertNotNull(cacheConfig);
            assertFalse(cacheConfig.isRecordStatistics());
        }

        @Test
        void cacheConfigRecordStatsEnabledPerDefault() {
            configuration.fireEvent(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "true");
            var cacheConfig = assertDoesNotThrow(() -> myCacheConfig.get());
            assertNotNull(cacheConfig);
            assertTrue(cacheConfig.isRecordStatistics());
        }

        @Test
        void cacheConfigRecordStatsDisabling() {
            configuration.put(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "true");
            configuration.put("my-cache." + CacheConfig.RECORD_STATISTICS_KEY, "false");
            configuration.fireEvent();
            LOGGER.info("Event Fired: %s", ConfigurationHelper.resolveConfigProperty("my-cache." + CacheConfig.RECORD_STATISTICS_KEY).get());
            var cacheConfig = assertDoesNotThrow(() -> myCacheConfig.get());
            assertNotNull(cacheConfig);
            assertFalse(cacheConfig.isRecordStatistics());
        }

        @Test
        void cacheConfigRecordStatsDisabledViaAnnotation() {
            configuration.fireEvent(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "true");
            var cacheConfig = assertDoesNotThrow(() -> myCacheConfigWithDisabledStats.get());
            assertNotNull(cacheConfig);
            assertFalse(cacheConfig.isRecordStatistics());
        }

        @Test
        void cacheConfigRecordStatsDisabledViaAnnotationButEnabledViaConfig() {
            configuration.put(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "true");
            configuration.put("my-cache2." + CacheConfig.RECORD_STATISTICS_KEY, "true");
            configuration.fireEvent();
            assertEquals("true", ConfigurationHelper.resolveConfigPropertyOrThrow("my-cache2." + CacheConfig.RECORD_STATISTICS_KEY));
            var cacheConfig = assertDoesNotThrow(() -> myCacheConfigWithDisabledStats.get());
            assertNotNull(cacheConfig);
            assertTrue(cacheConfig.isRecordStatistics());
        }
    }

    private static final CuiLogger LOGGER = new CuiLogger(ConfigurationProducerTest.class);
}
