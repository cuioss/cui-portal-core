package de.cuioss.portal.metrics;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
@DisplayName("CaffeineCacheMetrics Tests")
class CaffeineCacheMetricsTest {

    private static final String CACHE_PREFIX = "test-cache";
    private static final int CACHE_SIZE = 10;
    private static final int CACHE_EXPIRY = 2;

    @Nested
    @DisplayName("Basic Metrics Registration")
    class BasicMetricsTests {
        
        @Test
        @DisplayName("Should register all cache metrics with correct prefix")
        void shouldRegisterCachedMetrics() {
            // Given
            final var cacheConfig = new CacheConfig(CACHE_EXPIRY, TimeUnit.MINUTES, CACHE_SIZE, true);
            final Cache<String, String> cache = createTestCache(cacheConfig);
            final MetricRegistry registry = new PortalTestMetricRegistry();

            // When
            new CaffeineCacheMetrics(CACHE_PREFIX, cache, cacheConfig).bindTo(registry);

            // Then
            assertEquals(13, registry.getGauges().size(), "Expected number of metrics");
            var firstMetric = registry.getGauges().firstKey();
            assertTrue(firstMetric.getName().startsWith(CACHE_PREFIX),
                    "Metrics should start with cache prefix");
        }

        @Test
        @DisplayName("Should register metrics with custom tags")
        void shouldRegisterMetricsWithTags() {
            // Given
            final var cacheConfig = new CacheConfig(CACHE_EXPIRY, TimeUnit.MINUTES, CACHE_SIZE, true);
            final Cache<String, String> cache = createTestCache(cacheConfig);
            final MetricRegistry registry = new PortalTestMetricRegistry();
            final var customTag = new Tag("env", "test");

            // When
            new CaffeineCacheMetrics(CACHE_PREFIX, cache, cacheConfig, java.util.Collections.singleton(customTag))
                    .bindTo(registry);

            // Then: Only check for logging, bis our mock is rudimentary
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "env=test");
        }
    }

    @Nested
    @DisplayName("Parameter Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw on null prefix")
        void shouldThrowOnNullPrefix() {
            final var cacheConfig = new CacheConfig(CACHE_EXPIRY, TimeUnit.MINUTES, CACHE_SIZE, true);
            final Cache<String, String> cache = createTestCache(cacheConfig);

            assertThrows(NullPointerException.class,
                () -> new CaffeineCacheMetrics(null, cache, cacheConfig),
                "Should throw on null prefix");
        }

        @Test
        @DisplayName("Should throw on empty prefix")
        void shouldThrowOnEmptyPrefix() {
            final var cacheConfig = new CacheConfig(CACHE_EXPIRY, TimeUnit.MINUTES, CACHE_SIZE, true);
            final Cache<String, String> cache = createTestCache(cacheConfig);

            assertThrows(NullPointerException.class,
                () -> new CaffeineCacheMetrics("", cache, cacheConfig),
                "Should throw on empty prefix");
        }
    }

    private Cache<String, String> createTestCache(CacheConfig config) {
        return Caffeine.newBuilder()
                .maximumSize(config.getSize())
                .expireAfterAccess(config.getExpiration(), config.getTimeUnit())
                .recordStats()
                .build();
    }
}
