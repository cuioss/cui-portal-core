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

import com.github.benmanes.caffeine.cache.Cache;
import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;
import org.eclipse.microprofile.metrics.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.microprofile.metrics.MetricRegistry.name;

/**
 * Provides a factory for programmatically creating and registering cache
 * metrics.
 * <p>
 * {@code Caffeine.newBuilder().recordStats()} should be used!
 *
 * <pre>
 * &#64;Inject
 * &#64;ConfigAsCacheConfig(name = "cache.config.key")
 * private CacheConfig cacheConfig;
 *
 * &#64;Inject
 * &#64;RegistryType(type = MetricRegistry.Type.APPLICATION)
 * private MetricRegistry appRegistry;
 * final Cache<String, String> cache = Caffeine.newBuilder().maximumSize(cacheConfig.getSize())
 *         .expireAfterAccess(cacheConfig.getExpiration(), cacheConfig.getTimeUnit()).recordStats().build();
 * new CaffeineCacheMetrics("my-cache-name", cache, cacheConfig).bindTo(appRegistry);
 * </pre>
 *
 * @author Oliver Wolff
 */
public class CaffeineCacheMetrics {

    private static final CuiLogger LOGGER = new CuiLogger(CaffeineCacheMetrics.class);

    private final String namePrefix;
    private final Cache<?, ?> cache;
    private final CacheConfig cacheConfig;
    private final Tag[] tags;

    /**
     * @param namePrefix  must not be null nor empty. Used to separate different
     *                    caches
     * @param cache       must not be null
     * @param cacheConfig the configuration for that cache must not be null
     */
    public CaffeineCacheMetrics(final String namePrefix, final Cache<?, ?> cache, final CacheConfig cacheConfig) {
        this(namePrefix, cache, cacheConfig, Collections.emptySet());
    }

    /**
     * @param namePrefix  must not be null nor empty. Used to separate different
     *                    caches
     * @param cache       must not be null
     * @param cacheConfig the configuration for that cache must not be null
     * @param tags        tags to be added to each metric
     */
    public CaffeineCacheMetrics(final String namePrefix, final Cache<?, ?> cache, final CacheConfig cacheConfig,
                                final Iterable<Tag> tags) {
        requireNonNull(emptyToNull(namePrefix));
        requireNonNull(cache);
        requireNonNull(cacheConfig);

        this.namePrefix = namePrefix;
        this.cache = cache;
        this.cacheConfig = cacheConfig;
        this.tags = CollectionBuilder.copyFrom(tags).toArray(Tag.class);
    }

    /**
     * @return a newly created {@link Map} with {@link Metric} providing a number of
     * {@link Gauge}s with a consistent naming-scheme
     */
    private HashMap<Metadata, Gauge<? extends Number>> createMetrics() {
        final var metrics = new HashMap<Metadata, Gauge<? extends Number>>();

        metrics.put(getMetadata("hitRate"), (Gauge<Double>) () -> cache.stats().hitRate());
        metrics.put(getMetadata("hitCount"), (Gauge<Long>) () -> cache.stats().hitCount());

        metrics.put(getMetadata("missCount"), (Gauge<Long>) () -> cache.stats().missCount());
        metrics.put(getMetadata("missRate"), (Gauge<Double>) () -> cache.stats().missRate());

        metrics.put(getMetadata("loadFailureCount"), (Gauge<Long>) () -> cache.stats().loadFailureCount());
        metrics.put(getMetadata("loadFailureRate"), (Gauge<Double>) () -> cache.stats().loadFailureRate());
        metrics.put(getMetadata("evictionCount"), (Gauge<Long>) () -> cache.stats().evictionCount());

        metrics.put(getMetadata("config.size"), (Gauge<Long>) cacheConfig::getSize);
        metrics.put(getMetadata("config.expiration"),
            (Gauge<Long>) () -> cacheConfig.getTimeUnit().toMillis(cacheConfig.getExpiration()));

        return metrics;
    }

    private Metadata getMetadata(final String name) {
        return new MetadataBuilder().withName(name(namePrefix, name)).withUnit(MetricUnits.NONE).build();
    }

    /**
     * Add metrics to the given {@link MetricRegistry}, if not present.
     *
     * @param registry to be used
     * @throws NullPointerException if registry is null
     */
    public void bindTo(final MetricRegistry registry) {
        requireNonNull(registry);
        var metrics = createMetrics();
        metrics.forEach((meta, metric) -> registry.gauge(meta, metric::getValue, tags));
    }
}
