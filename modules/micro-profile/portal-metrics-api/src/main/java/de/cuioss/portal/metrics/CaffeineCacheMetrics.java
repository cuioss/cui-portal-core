/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
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
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.microprofile.metrics.MetricRegistry.name;

/**
 * Factory for creating and registering Caffeine cache metrics in a MicroProfile Metrics registry.
 * This class provides a convenient way to monitor cache performance by exposing various metrics
 * such as hit rates, miss rates, load statistics, and size information.
 * 
 * <h2>Metrics Provided</h2>
 * <ul>
 *   <li>Hit/Miss Statistics:
 *     <ul>
 *       <li>hitRate - Ratio of cache hits to total lookups</li>
 *       <li>hitCount - Number of cache hits</li>
 *       <li>missCount - Number of cache misses</li>
 *       <li>missRate - Ratio of cache misses to total lookups</li>
 *     </ul>
 *   </li>
 *   <li>Load Statistics:
 *     <ul>
 *       <li>loadCount - Number of cache loads</li>
 *       <li>loadSuccessCount - Number of successful cache loads</li>
 *       <li>loadFailureCount - Number of failed cache loads</li>
 *       <li>loadFailureRate - Ratio of failed loads to total loads</li>
 *       <li>averageLoadPenalty - Average time spent loading new values</li>
 *       <li>totalLoadTime - Total time spent loading new values</li>
 *     </ul>
 *   </li>
 *   <li>Size Information:
 *     <ul>
 *       <li>estimatedSize - Current estimated size of the cache</li>
 *       <li>maxSize - Maximum configured size of the cache</li>
 *       <li>evictionCount - Number of cache evictions</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * To use this class, ensure your cache is configured with {@code recordStats()} enabled:
 * 
 * <pre>
 * &#64;Inject
 * &#64;ConfigAsCacheConfig(name = "cache.config.key")
 * private CacheConfig cacheConfig;
 *
 * &#64;Inject
 * &#64;RegistryType(type = MetricRegistry.Type.APPLICATION)
 * private MetricRegistry appRegistry;
 *
 * // Create cache with stats recording enabled
 * Cache&lt;String, String&gt; cache = Caffeine.newBuilder()
 *         .maximumSize(cacheConfig.getSize())
 *         .expireAfterAccess(cacheConfig.getExpiration(), cacheConfig.getTimeUnit())
 *         .recordStats()
 *         .build();
 *
 * // Register metrics
 * new CaffeineCacheMetrics("my-cache", cache, cacheConfig).bindTo(appRegistry);
 * </pre>
 *
 * <p>All metrics will be prefixed with the provided name to distinguish between multiple caches.
 * Optional tags can be added to provide additional context for the metrics.
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
     * Creates a new metrics factory for the given cache.
     *
     * @param namePrefix  must not be null nor empty. Used as prefix for all metrics to
     *                    distinguish between different caches in the same registry
     * @param cache       must not be null and should be configured with
     *                    {@code recordStats()} enabled
     * @param cacheConfig the configuration for the cache, must not be null
     */
    public CaffeineCacheMetrics(final String namePrefix, final Cache<?, ?> cache, final CacheConfig cacheConfig) {
        this(namePrefix, cache, cacheConfig, Collections.emptySet());
    }

    /**
     * Creates a new metrics factory for the given cache with additional tags.
     *
     * @param namePrefix  must not be null nor empty. Used as prefix for all metrics to
     *                    distinguish between different caches in the same registry
     * @param cache       must not be null and should be configured with
     *                    {@code recordStats()} enabled
     * @param cacheConfig the configuration for the cache, must not be null
     * @param tags        additional tags to be added to each metric for better
     *                    categorization and filtering. May be empty but not null.
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
        LOGGER.debug("Created CaffeineCacheMetrics for cache '%s' with %s tags", namePrefix, this.tags.length);
    }

    /**
     * Creates a map of metrics for the cache.
     *
     * @return a newly created {@link Map} containing {@link Metric} instances that
     *         provide various cache statistics as {@link Gauge}s
     */
    private HashMap<Metadata, Gauge<? extends Number>> createMetrics() {
        LOGGER.debug("Creating metrics for cache '%s'", namePrefix);
        final var metrics = new HashMap<Metadata, Gauge<? extends Number>>();

        // Cache hit/miss metrics
        metrics.put(getMetadata("hitRate"), cache.stats()::hitRate);
        metrics.put(getMetadata("hitCount"), cache.stats()::hitCount);
        metrics.put(getMetadata("missCount"), cache.stats()::missCount);
        metrics.put(getMetadata("missRate"), cache.stats()::missRate);

        // Cache load metrics
        metrics.put(getMetadata("loadCount"), cache.stats()::loadCount);
        metrics.put(getMetadata("loadSuccessCount"), cache.stats()::loadSuccessCount);
        metrics.put(getMetadata("loadFailureCount"), cache.stats()::loadFailureCount);
        metrics.put(getMetadata("loadFailureRate"), cache.stats()::loadFailureRate);

        // Cache size metrics
        metrics.put(getMetadata("evictionCount"), cache.stats()::evictionCount);
        metrics.put(getMetadata("estimatedSize"), cache::estimatedSize);
        metrics.put(getMetadata("maxSize"), cacheConfig::getSize);

        // Cache timing metrics
        metrics.put(getMetadata("averageLoadPenalty"), cache.stats()::averageLoadPenalty);
        metrics.put(getMetadata("totalLoadTime"), cache.stats()::totalLoadTime);

        LOGGER.debug("Created %s metrics for cache '%s'", metrics.size(), namePrefix);
        return metrics;
    }

    private Metadata getMetadata(final String name) {
        return new MetadataBuilder().withName(name(namePrefix, name)).withUnit(MetricUnits.NONE).build();
    }

    /**
     * Registers all cache metrics with the given registry. Each metric will be
     * prefixed with the configured name prefix and include any configured tags.
     *
     * @param registry the metric registry to register the metrics with
     * @throws NullPointerException if registry is null
     */
    public void bindTo(final MetricRegistry registry) {
        requireNonNull(registry);
        LOGGER.debug("Binding metrics for cache '%s' to registry", namePrefix);
        final var metrics = createMetrics();
        metrics.forEach((meta, metric) -> registry.gauge(meta, metric::getValue, tags));
        LOGGER.info(PortalMetricsLogMessages.INFO.CACHE_METRICS_REGISTERED, metrics.size(), namePrefix);
    }
}
