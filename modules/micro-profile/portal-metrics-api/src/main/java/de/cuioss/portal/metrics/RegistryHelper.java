package de.cuioss.portal.metrics;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

import de.cuioss.tools.logging.CuiLogger;
import lombok.RequiredArgsConstructor;

/**
 * Helper class for metrics registration.
 *
 * @author Sven Haag
 */
@RequiredArgsConstructor
public class RegistryHelper {

    /**
     * "threads"
     */
    public static final String THREADS_SUFFIX = "threads";

    private final CuiLogger log;
    private final MetricRegistry registry;

    /**
     * Registers the given metric if its {@link MetricID} is not present.
     *
     * @param metadata
     * @param metric
     * @param tags
     */
    public void bindIfNotPresent(final Metadata metadata, final Metric metric, final Tag... tags) {
        final var metricId = new MetricID(metadata.getName(), tags);
        if (!registry.getMetricIDs().contains(metricId)) {
            log.trace("Adding metric {}: {}", metricId, metadata);
            registry.register(metadata, metric, tags);
        } else {
            log.trace("Metric already exists: {}", metricId);
        }
    }

    /**
     * First removes and then adds the given metric. Typically only relevant for
     * {@link Gauge} metrics.
     *
     * @param metadata
     * @param metric
     * @param tags
     */
    void bindOverwrite(final Metadata metadata, final Metric metric, final Tag... tags) {
        final var metricId = new MetricID(metadata.getName(), tags);
        log.trace("Adding metric {}: {}", metricId, metadata);
        registry.remove(metricId);
        registry.register(metadata, metric, tags);
    }
}
