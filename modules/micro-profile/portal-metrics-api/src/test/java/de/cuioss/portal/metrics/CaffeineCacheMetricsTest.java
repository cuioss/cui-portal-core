package de.cuioss.portal.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.portal.metrics.CaffeineCacheMetrics;

class CaffeineCacheMetricsTest {

    @Test
    void shouldRegisterCachedMetrics() {
        final var prefix = "test";
        final var cacheConfig = new CacheConfig(2, TimeUnit.MINUTES, 10, true);
        final Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(cacheConfig.getSize())
            .expireAfterAccess(cacheConfig.getExpiration(), cacheConfig.getTimeUnit())
            .recordStats()
            .build();
        final MetricRegistry registry = new PortalTestMetricRegistry();
        new CaffeineCacheMetrics(prefix, cache, cacheConfig).bindTo(registry);

        assertEquals(9, registry.getGauges().size());
        assertTrue(registry.getGauges().firstKey().getName().startsWith(prefix));
    }
}
