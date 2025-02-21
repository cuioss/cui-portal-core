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

import de.cuioss.portal.configuration.MetricsConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.TestEnvConfigSource;
import de.cuioss.portal.configuration.types.ConfigAsCacheConfig;
import de.cuioss.portal.configuration.types.ConfigAsFilteredMap;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.string.MoreStrings;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.THEME_DEFAULT;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigPropertyOrThrow;
import static org.junit.jupiter.api.Assertions.*;

@EnablePortalConfigurationLocal
@EnableAutoWeld
@SuppressWarnings("el-syntax")
class PortalConfigSourceTest {

    private static final String OTHER_KEY = ".otherKey";

    private static final String SOME_PREFIX = "some.prefix";

    private static final String SOME_KEY = "someKey";

    private static final String SOME_VALUE = "someValue";

    @Inject
    private PortalTestConfigurationLocal configuration;

    private Map<String, String> payload;

    @Inject
    @ConfigAsFilteredMap(startsWith = SOME_PREFIX)
    private Provider<Map<String, String>> filteredMap;

    @Inject
    @ConfigAsFilteredMap(startsWith = SOME_PREFIX, stripPrefix = true)
    private Provider<Map<String, String>> filteredMapWOPrefix;

    @Inject
    @ConfigAsCacheConfig(name = SOME_PREFIX, defaultExpiration = 4, defaultSize = 3)
    private Provider<CacheConfig> cacheConfigProvider;

    @Inject
    @ConfigAsCacheConfig(name = "", defaultExpiration = 4, defaultSize = 3)
    private Provider<CacheConfig> invalidCacheConfigProvider;

    @BeforeEach
    void beforeTest() {
        configuration.clear();
    }

    @Test
    void shouldProvideAtLeastDefaultConfiguration() {
        final var map = ConfigurationHelper.resolveConfigProperties();
        assertNotNull(map);
        assertTrue(map.size() > 10);
        assertNotNull(MoreStrings.emptyToNull(map.get(PortalConfigurationKeys.PORTAL_STAGE)));
    }

    @Test
    void shouldProvideAdditionalConfiguration() {
        assertNull(resolveConfigProperty(SOME_KEY).orElse(null));
        configuration.fireEvent(SOME_KEY, SOME_VALUE);
        assertEquals(SOME_VALUE, resolveConfigPropertyOrThrow(SOME_KEY));
    }

    @Test
    void shouldOverwriteKeysCorrectly() {
        configuration.fireEvent(THEME_DEFAULT, SOME_VALUE);
        assertEquals(SOME_VALUE, resolveConfigProperty(THEME_DEFAULT).orElse(null));
    }

    @Test
    void shouldHandleConfigAsFilteredMap() {
        assertTrue(filteredMap.get().isEmpty());

        configuration.fireEvent(SOME_PREFIX + SOME_KEY, SOME_VALUE);
        assertEquals(1, filteredMap.get().size());

        configuration.fireEvent(SOME_PREFIX + OTHER_KEY, SOME_VALUE);
        assertEquals(2, filteredMap.get().size());

        // Should support empty key
        configuration.fireEvent(SOME_PREFIX, SOME_VALUE);
        assertEquals(3, filteredMap.get().size());

        // Should ignore without prefix
        configuration.fireEvent(SOME_KEY, SOME_VALUE);
        assertEquals(3, filteredMap.get().size());
    }

    @Test
    void shouldHandleConfigAsFilteredMapWOPrefix() {
        assertTrue(filteredMapWOPrefix.get().isEmpty());

        configuration.fireEvent(SOME_PREFIX + SOME_KEY, SOME_VALUE);

        assertEquals(1, filteredMapWOPrefix.get().size());
        assertTrue(filteredMapWOPrefix.get().containsKey(SOME_KEY));

        configuration.fireEvent(SOME_PREFIX + OTHER_KEY, SOME_VALUE);

        assertEquals(2, filteredMapWOPrefix.get().size());
        assertTrue(filteredMapWOPrefix.get().containsKey(SOME_KEY));
        assertTrue(filteredMapWOPrefix.get().containsKey(OTHER_KEY));

        // Should support empty key
        configuration.fireEvent(SOME_PREFIX, SOME_VALUE);

        assertEquals(3, filteredMapWOPrefix.get().size());
        assertTrue(filteredMapWOPrefix.get().containsKey(SOME_KEY));
        assertTrue(filteredMapWOPrefix.get().containsKey(OTHER_KEY));
        assertTrue(filteredMapWOPrefix.get().containsKey(""));

        // Should ignore without prefix
        configuration.fireEvent(SOME_KEY, SOME_VALUE);
        assertEquals(3, filteredMapWOPrefix.get().size());
    }

    @Test
    void shouldHandleCacheConfig() {
        configuration.fireEvent(MetricsConfigKeys.PORTAL_METRICS_ENABLED, "true");

        // Should use defaults
        assertEquals(4, cacheConfigProvider.get().getExpiration());
        assertEquals(3, cacheConfigProvider.get().getSize());
        assertEquals(TimeUnit.MINUTES, cacheConfigProvider.get().getTimeUnit());

        configuration.put(SOME_PREFIX + "." + CacheConfig.EXPIRATION_KEY, "7");
        configuration.put(SOME_PREFIX + "." + CacheConfig.SIZE_KEY, "8");
        configuration.put(SOME_PREFIX + "." + CacheConfig.EXPIRATION_UNIT_KEY, TimeUnit.SECONDS.name());
        configuration.fireEvent();

        // Should use configuredValues
        assertEquals(7, cacheConfigProvider.get().getExpiration());
        assertEquals(8, cacheConfigProvider.get().getSize());
        assertEquals(TimeUnit.SECONDS, cacheConfigProvider.get().getTimeUnit());

        configuration.put(SOME_PREFIX + "." + CacheConfig.EXPIRATION_KEY, "boom");
        configuration.put(SOME_PREFIX + "." + CacheConfig.SIZE_KEY, "boom");
        configuration.put(SOME_PREFIX + "." + CacheConfig.EXPIRATION_UNIT_KEY, "boom");
        configuration.fireEvent();
        // Should default on initial value
        assertEquals(4, cacheConfigProvider.get().getExpiration());
        assertEquals(3, cacheConfigProvider.get().getSize());
        assertEquals(TimeUnit.MINUTES, cacheConfigProvider.get().getTimeUnit());
    }

    @Test
    void cacheConfigShouldFailOnMissingName() {
        assertThrows(IllegalArgumentException.class, () -> invalidCacheConfigProvider.get());
    }

    @Test
    void shouldUseEnvPropertyFirst() {
        final var PREFIX = getClass().getSimpleName();
        final var KEY = PREFIX.toLowerCase() + ".portal.test.env";
        final var ENV_KEY = PREFIX.toUpperCase() + "_" + "PORTAL_TEST_ENV";

        assertTrue(
                StreamSupport.stream(ConfigProvider.getConfig().getConfigSources().spliterator(), false)
                        .anyMatch(clazz -> clazz instanceof TestEnvConfigSource),
                "TestEnvConfigSource class not available in configuration system");

        TestEnvConfigSource.getAdditionalProperties().put(ENV_KEY, "ENV");
        TestEnvConfigSource.getAdditionalProperties().put("PLACEHOLDER", "${" + ENV_KEY + ":}"); // indirection
        configuration.put("PLACEHOLDER_YAML", "${PLACEHOLDER:default}"); // usage
        configuration.put(ENV_KEY, "DEFAULT_ENV");
        configuration.put(KEY, "DEFAULT");
        configuration.fireEvent();
        assertEquals("ENV", ConfigurationHelper.resolveConfigProperty(KEY).orElse(null));
        /*
         * assertNotEquals("ENV",
         * ConfigurationHelper.resolveConfigProperty("PLACEHOLDER").orElse(null),
         * "actual placeholder expected because PortalConfigurationProducer has lower prio than ENV config source. "
         * +
         * "this should be no issue because usually ENV properties are only used in actual config property."
         * );
         */
        assertEquals("ENV", ConfigurationHelper.resolveConfigProperty("PLACEHOLDER").orElse(null),
                "expanded value expected");
        assertEquals("ENV", ConfigurationHelper.resolveConfigProperty("PLACEHOLDER_YAML").orElse(null));

        // adding system property which has higher priority than env property
        System.setProperty(KEY, "SYS");
        configuration.fireEvent();
        assertEquals("SYS", ConfigurationHelper.resolveConfigProperty(KEY).orElse(null));
    }

    @Test
    void configHelperShouldNotResolvePropertyWithEmptyValue() {
        configuration.fireEvent("portal.placeholder", "${PORTAL_TEST_ENV:}");
        final var all = ConfigurationHelper.resolveConfigProperties();
        assertFalse(all.containsKey("portal.placeholder"));
    }

    @Test
    void configHelperShouldUseResolvedPortalConfigValue() {
        System.setProperty("TEST_CONFIG_PRIO", ""); // this empty string should be used due to
        // higher prio,
        configuration.fireEvent("TEST_CONFIG_PRIO", "value"); // despite there is a value in a lower
        // prio config source

        final var all = ConfigurationHelper.resolveConfigProperties();
        assertFalse(all.containsKey("TEST_CONFIG_PRIO"), "properties with empty value should be unavailable");
    }
}
