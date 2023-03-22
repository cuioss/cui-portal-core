package de.cuioss.portal.core.metrics;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.microprofile.metrics.MetricRegistry.name;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;

import com.github.benmanes.caffeine.cache.Cache;

import de.cuioss.portal.configuration.cache.CacheConfig;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Provides a factory for programmatically creating and registering cache metrics.
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
 * final Cache<String, String> cache = Caffeine.newBuilder()
 *         .maximumSize(cacheConfig.getSize())
 *         .expireAfterAccess(cacheConfig.getExpiration(), cacheConfig.getTimeUnit())
 *         .recordStats()
 *         .build();
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
     * @param namePrefix must not be null nor empty. Used to separate different caches
     * @param cache must not be null
     * @param cacheConfig the configuration for that cache must not be null
     */
    public CaffeineCacheMetrics(final String namePrefix,
            final Cache<?, ?> cache,
            final CacheConfig cacheConfig) {
        this(namePrefix, cache, cacheConfig, Collections.emptySet());
    }

    /**
     * @param namePrefix must not be null nor empty. Used to separate different caches
     * @param cache must not be null
     * @param cacheConfig the configuration for that cache must not be null
     * @param tags tags to be added to each metric
     */
    public CaffeineCacheMetrics(final String namePrefix,
            final Cache<?, ?> cache,
            final CacheConfig cacheConfig,
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
     * @return a newly created {@link Map} with {@link Metric} providing a number of {@link Gauge}s
     *         with a consistent naming-scheme
     */
    private HashMap<Metadata, Metric> createMetrics() {
        final var metrics = new HashMap<Metadata, Metric>();

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
        return new MetadataBuilder()
                .withName(name(namePrefix, name))
                .withType(MetricType.GAUGE)
                .withUnit(MetricUnits.NONE)
                .build();
    }

    /**
     * Add metrics to the given {@link MetricRegistry}, if not present.
     *
     * @param registry to be used
     *
     * @throws NullPointerException if registry is null
     */
    public void bindTo(final MetricRegistry registry) {
        requireNonNull(registry);

        final Map<Metadata, Metric> metrics = createMetrics();
        final var registryHelper = new RegistryHelper(LOGGER, registry);
        metrics.forEach((meta, metric) -> registryHelper.bindIfNotPresent(meta, metric, tags));
    }

    /**
     * @param namePrefix must not be null
     * @param registry to remove from
     *
     * @throws NullPointerException if namePrefix or registry is null
     */
    public static void remove(final String namePrefix, final MetricRegistry registry) {
        requireNonNull(emptyToNull(namePrefix));
        requireNonNull(registry);
        registry.getNames().stream()
                .filter(name -> name.startsWith(namePrefix))
                .forEach(registry::remove);
    }
}
