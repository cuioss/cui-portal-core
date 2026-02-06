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
package de.cuioss.portal.core.test.mocks.microprofile;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.Getter;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricFilter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Snapshot;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Mock implementation of MicroProfile {@link MetricRegistry} for testing purposes.
 * Provides a simplified metric registry that supports basic metric operations and
 * tracking in a test environment.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Counter and Timer metric support</li>
 *   <li>Metadata management</li>
 *   <li>Concurrent metric storage</li>
 *   <li>Application-scoped registry</li>
 *   <li>Metric ID and tag support</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Counter metrics:
 * <pre>
 * &#64;Inject
 * private MetricRegistry registry;
 *
 * void testCounter() {
 *     // Create and use counter
 *     Counter counter = registry.counter("requests");
 *     counter.inc();
 *
 *     // Verify count
 *     assertEquals(1, counter.getCount());
 * }
 * </pre>
 *
 * Timer metrics:
 * <pre>
 * void testTimer() {
 *     Timer timer = registry.timer("response_time");
 *
 *     // Time an operation
 *     timer.time(() -> {
 *         // Simulated work
 *         Thread.sleep(100);
 *         return null;
 *     });
 *
 *     // Verify timer
 *     assertTrue(timer.getCount() > 0);
 * }
 * </pre>
 *
 * Metadata and tags:
 * <pre>
 * void testMetadata() {
 *     // Create metric with metadata
 *     Metadata metadata = Metadata.builder()
 *         .withName("requests")
 *         .withDescription("Total requests")
 *         .withUnit(MetricUnits.NONE)
 *         .build();
 *
 *     // Create metric with tags
 *     Tag tag = new Tag("endpoint", "/api");
 *     Counter counter = registry.counter(metadata, tag);
 *
 *     // Verify registration
 *     assertTrue(registry.getMetrics().containsKey(new MetricID("requests", tag)));
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Uses {@link ConcurrentHashMap} for thread-safe metric storage</li>
 *   <li>Supports basic metric types (Counter, Timer)</li>
 *   <li>Some advanced features are not implemented</li>
 *   <li>Maintains metadata separately from metrics</li>
 *   <li>Application-scoped for consistent state</li>
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 *   <li>Some metric types may not be fully implemented</li>
 *   <li>Histogram and Meter operations throw UnsupportedOperationException</li>
 *   <li>Some advanced MetricRegistry features are not available</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @author Sven Haag
 * @since 1.0
 * @see MetricRegistry
 * @see Counter
 * @see Timer
 * @see Metadata
 */
@ApplicationScoped
public class PortalTestMetricRegistry implements MetricRegistry {

    private static final RuntimeException NOT_IMPLEMENTED_EXCEPTION = new UnsupportedOperationException(
            "Not implemented yet");

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Metadata> metadataMap = new HashMap<>();

    @Getter(AccessLevel.PROTECTED)
    private final Map<MetricID, Metric> metricMap = new ConcurrentHashMap<>();

    /**
     * @param name metric name
     * @return the metric with the given name
     */
    public Optional<Metric> getMetric(final String name) {
        return metricMap.entrySet().stream().filter(e -> e.getKey().getName().equals(name)).map(Map.Entry::getValue)
                .findAny();
    }

    /**
     * @param name
     * @return {@link Optional} on the requested {@link MetricID}
     */
    public Optional<MetricID> getMetricID(final String name) {
        return metricMap.keySet().stream().filter(metric -> metric.getName().equals(name)).findAny();
    }

    /**
     * @param name metric name
     * @return the tags for the given metric name
     */
    public Optional<Map<String, String>> getTags(final String name) {
        for (final MetricID id : metricMap.keySet()) {
            if (name.equals(id.getName())) {
                return Optional.of(id.getTags());
            }
        }
        return Optional.empty();
    }

    public <T extends Metric> T register(final Metadata metadata, final T metric) {
        metricMap.put(new MetricID(metadata.getName()), metric);
        metadataMap.put(metadata.getName(), metadata);
        return metric;
    }


    @Override
    public Counter counter(final String name) {
        return counter(new MetricID(name));
    }

    @Override
    public Counter counter(final String name, final Tag... tags) {
        return counter(new MetricID(name, tags));
    }

    @Override
    public Counter counter(MetricID metricID) {
        return (Counter) metricMap.computeIfAbsent(metricID, id -> {
            Counter counter = new PortalTestCounter();
            Metadata metadata = Metadata.builder().withName(id.getName()).build();
            metadataMap.put(id.getName(), metadata);
            return counter;
        });
    }

    @Override
    public Counter counter(final Metadata metadata) {
        return counter(new MetricID(metadata.getName()));
    }

    @Override
    public Counter counter(final Metadata metadata, final Tag... tags) {
        return counter(new MetricID(metadata.getName(), tags));
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(String s, T t, Function<T, R> function, Tag... tags) {
        return null;
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(MetricID metricID, T t, Function<T, R> function) {
        return null;
    }

    @Override
    public <T, R extends Number> Gauge<R> gauge(Metadata metadata, T t, Function<T, R> function, Tag... tags) {
        return null;
    }

    @Override
    public <T extends Number> Gauge<T> gauge(String s, Supplier<T> supplier, Tag... tags) {
        return null;
    }

    @Override
    public <T extends Number> Gauge<T> gauge(MetricID metricID, Supplier<T> supplier) {
        return null;
    }

    @Override
    public <T extends Number> Gauge<T> gauge(Metadata metadata, Supplier<T> supplier, Tag... tags) {
        return null;
    }

    @Override
    public Histogram histogram(final String name) {
        return null;
    }

    @Override
    public Histogram histogram(final String name, final Tag... tags) {
        return null;
    }

    @Override
    public Histogram histogram(MetricID metricID) {
        return null;
    }

    @Override
    public Histogram histogram(final Metadata metadata) {
        return null;
    }

    @Override
    public Histogram histogram(final Metadata metadata, final Tag... tags) {
        return null;
    }

    @Override
    public Timer timer(final String name) {
        return null;
    }

    @Override
    public Timer timer(final String name, final Tag... tags) {
        final var id = new MetricID(name, tags);
        if (metricMap.containsKey(id)) {
            return (Timer) metricMap.get(id);
        }

        final Timer timer = new Timer() {

            private final Context context = new Context() {

                private final long start = System.nanoTime();

                @Override
                public long stop() {
                    return System.nanoTime() - start;
                }

                @Override
                public void close() {
                    stop();
                }
            };

            @Override
            public void update(final Duration duration) {
                throw NOT_IMPLEMENTED_EXCEPTION;
            }

            @Override
            public <T> T time(final Callable<T> event) {
                throw NOT_IMPLEMENTED_EXCEPTION;
            }

            @Override
            public void time(final Runnable event) {
                throw NOT_IMPLEMENTED_EXCEPTION;
            }

            @Override
            public Context time() {
                // always returns the same context in contrast to specification.
                return context;
            }

            @Override
            public Duration getElapsedTime() {
                return Duration.of(context.stop(), ChronoUnit.NANOS);
            }

            @Override
            public long getCount() {
                return 0;
            }

            @Override
            public Snapshot getSnapshot() {
                throw NOT_IMPLEMENTED_EXCEPTION;
            }
        };

        metricMap.put(id, timer);
        metadataMap.put(name, new MetadataBuilder().withName(name)
                .withUnit(MetricUnits.NANOSECONDS).build());
        return timer;
    }

    @Override
    public Timer timer(MetricID metricID) {
        return null;
    }

    @Override
    public Timer timer(final Metadata metadata) {
        return null;
    }

    @Override
    public Timer timer(final Metadata metadata, final Tag... tags) {
        return null;
    }

    @Override
    public Metric getMetric(MetricID metricID) {
        return null;
    }

    @Override
    public <T extends Metric> T getMetric(MetricID metricID, Class<T> aClass) {
        return null;
    }

    @Override
    public Counter getCounter(MetricID metricID) {
        Metric metric = metricMap.get(metricID);
        if (metric instanceof Counter counter) {
            return counter;
        }
        return null;
    }


    @Override
    public Gauge<?> getGauge(MetricID metricID) {
        return null;
    }

    @Override
    public Histogram getHistogram(MetricID metricID) {
        return null;
    }

    @Override
    public Timer getTimer(MetricID metricID) {
        return null;
    }


    @Override
    public Metadata getMetadata(String name) {
        return metadataMap.get(name);
    }

    @Override
    public boolean remove(final String name) {
        metricMap.entrySet().removeIf(entry -> entry.getKey().getName().equals(name));
        metadataMap.remove(name);
        return true;
    }

    @Override
    public boolean remove(final MetricID metricID) {
        return metricMap.remove(metricID) != null;
    }

    @Override
    public void removeMatching(final MetricFilter filter) {
        // NOOP
    }

    @Override
    public SortedSet<String> getNames() {
        return Collections.unmodifiableSortedSet(
                new TreeSet<>(metricMap.keySet().stream().map(MetricID::getName).collect(Collectors.toSet())));
    }

    @Override
    public SortedSet<MetricID> getMetricIDs() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(metricMap.keySet()));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SortedMap<MetricID, Gauge> getGauges() {
        final SortedMap<MetricID, Gauge> result = new TreeMap<>();
        for (final Map.Entry<MetricID, Metric> entry : metricMap.entrySet()) {
            if (entry.getValue() instanceof Gauge gauge) {
                result.put(entry.getKey(), gauge);
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SortedMap<MetricID, Gauge> getGauges(final MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, Counter> getCounters() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, Counter> getCounters(final MetricFilter filter) {
        return Collections.emptySortedMap();
    }


    @Override
    public SortedMap<MetricID, Histogram> getHistograms() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, Histogram> getHistograms(final MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers() {
        final SortedMap<MetricID, Timer> out = new TreeMap<>();
        for (final Map.Entry<MetricID, Metric> entry : metricMap.entrySet()) {
            if (entry.getValue() instanceof Timer timer) {
                out.put(entry.getKey(), timer);
            }
        }
        return out;
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers(final MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, Metric> getMetrics(MetricFilter metricFilter) {
        return Collections.emptySortedMap();
    }

    @Override
    public <T extends Metric> SortedMap<MetricID, T> getMetrics(Class<T> aClass, MetricFilter metricFilter) {
        return Collections.emptySortedMap();
    }

    @Override
    public Map<MetricID, Metric> getMetrics() {
        return Collections.emptySortedMap();
    }

    @Override
    public Map<String, Metadata> getMetadata() {
        return Collections.emptySortedMap();
    }

    /**
     * @return te scope
     */
    @Override
    public String getScope() {
        return "";
    }

}
