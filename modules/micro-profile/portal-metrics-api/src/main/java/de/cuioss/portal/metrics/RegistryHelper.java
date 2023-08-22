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
