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
package de.cuioss.portal.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.cuioss.portal.configuration.cache.CacheConfig;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class CaffeineCacheMetricsTest {

    @Test
    void shouldRegisterCachedMetrics() {
        final var prefix = "test";
        final var cacheConfig = new CacheConfig(2, TimeUnit.MINUTES, 10, true);
        final Cache<String, String> cache = Caffeine.newBuilder().maximumSize(cacheConfig.getSize())
                .expireAfterAccess(cacheConfig.getExpiration(), cacheConfig.getTimeUnit()).recordStats().build();
        final MetricRegistry registry = new PortalTestMetricRegistry();
        new CaffeineCacheMetrics(prefix, cache, cacheConfig).bindTo(registry);

        assertEquals(9, registry.getGauges().size());
        assertTrue(registry.getGauges().firstKey().getName().startsWith(prefix));
    }
}
