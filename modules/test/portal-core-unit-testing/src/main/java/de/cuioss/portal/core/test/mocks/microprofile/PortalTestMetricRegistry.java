package de.cuioss.portal.core.test.mocks.microprofile;

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

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.metrics.ConcurrentGauge;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricFilter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.SimpleTimer;
import org.eclipse.microprofile.metrics.Snapshot;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Simple Mock variant of {@link MetricRegistry}.
 * Partially implemented.
 *
 * @author Oliver Wolff
 * @author Sven Haag
 */
@ApplicationScoped
public class PortalTestMetricRegistry implements MetricRegistry {

    private static final RuntimeException NOT_IMPLEMENTED_EXCEPTION =
        new UnsupportedOperationException("Not implemented yet");

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Metadata> metadataMap = new HashMap<>();

    @Getter(AccessLevel.PROTECTED)
    private final Map<MetricID, Metric> metricMap = new ConcurrentHashMap<>();

    /**
     * @param name metric name
     *
     * @return the metric with the given name
     */
    public Optional<Metric> getMetric(final String name) {
        return metricMap.entrySet().stream()
            .filter(e -> e.getKey().getName().equals(name))
            .map(Map.Entry::getValue)
            .findAny();
    }

    /**
     * @param name
     *
     * @return {@link Optional} on the requested {@link MetricID}
     */
    public Optional<MetricID> getMetricID(final String name) {
        return metricMap.keySet().stream()
            .filter(metric -> metric.getName().equals(name))
            .findAny();
    }

    /**
     * @param name metric name
     *
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

    @Override
    public <T extends Metric> T register(final String name, final T metric) {
        metricMap.put(new MetricID(name), metric);
        return metric;
    }

    @Override
    public <T extends Metric> T register(final Metadata metadata, final T metric) {
        metricMap.put(new MetricID(metadata.getName()), metric);
        metadataMap.put(metadata.getName(), metadata);
        return metric;
    }

    @Override
    public <T extends Metric> T register(final Metadata metadata, final T metric, final Tag... tags) {
        metricMap.put(new MetricID(metadata.getName(), tags), metric);
        metadataMap.put(metadata.getName(), metadata);
        return metric;
    }

    @Override
    public Counter counter(final String name) {
        return null;
    }

    @Override
    public Counter counter(final String name, final Tag... tags) {
        return null;
    }

    @Override
    public Counter counter(MetricID metricID) {
        return null;
    }

    @Override
    public Counter counter(final Metadata metadata) {
        return null;
    }

    @Override
    public Counter counter(final Metadata metadata, final Tag... tags) {
        return null;
    }

    @Override
    public ConcurrentGauge concurrentGauge(final String name) {
        return null;
    }

    @Override
    public ConcurrentGauge concurrentGauge(final String name, final Tag... tags) {
        return null;
    }

    @Override
    public ConcurrentGauge concurrentGauge(MetricID metricID) {
        return null;
    }

    @Override
    public ConcurrentGauge concurrentGauge(final Metadata metadata) {
        return null;
    }

    @Override
    public ConcurrentGauge concurrentGauge(final Metadata metadata, final Tag... tags) {
        return null;
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
    public Meter meter(final String name) {
        return null;
    }

    @Override
    public Meter meter(final String name, final Tag... tags) {
        return null;
    }

    @Override
    public Meter meter(MetricID metricID) {
        return null;
    }

    @Override
    public Meter meter(final Metadata metadata) {
        return null;
    }

    @Override
    public Meter meter(final Metadata metadata, final Tag... tags) {
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
            public double getFifteenMinuteRate() {
                return 0;
            }

            @Override
            public double getFiveMinuteRate() {
                return 0;
            }

            @Override
            public double getMeanRate() {
                return 0;
            }

            @Override
            public double getOneMinuteRate() {
                return 0;
            }

            @Override
            public Snapshot getSnapshot() {
                throw NOT_IMPLEMENTED_EXCEPTION;
            }
        };

        metricMap.put(id, timer);
        metadataMap.put(name, new MetadataBuilder()
            .withName(name)
            .withType(MetricType.TIMER)
            .withUnit(MetricUnits.NANOSECONDS)
            .build());
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
    public SimpleTimer simpleTimer(final String name) {
        return null;
    }

    @Override
    public SimpleTimer simpleTimer(final String name, final Tag... tags) {
        return null;
    }

    @Override
    public SimpleTimer simpleTimer(MetricID metricID) {
        return null;
    }

    @Override
    public SimpleTimer simpleTimer(final Metadata metadata) {
        return null;
    }

    @Override
    public SimpleTimer simpleTimer(final Metadata metadata, final Tag... tags) {
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
        return null;
    }

    @Override
    public ConcurrentGauge getConcurrentGauge(MetricID metricID) {
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
    public Meter getMeter(MetricID metricID) {
        return null;
    }

    @Override
    public Timer getTimer(MetricID metricID) {
        return null;
    }

    @Override
    public SimpleTimer getSimpleTimer(MetricID metricID) {
        return null;
    }

    @Override
    public Metadata getMetadata(String s) {
        return null;
    }

    @Override
    public boolean remove(final String name) {
        return false;
    }

    @Override
    public boolean remove(final MetricID metricID) {
        return false;
    }

    @Override
    public void removeMatching(final MetricFilter filter) {
        // NOOP
    }

    @Override
    public SortedSet<String> getNames() {
        return Collections.unmodifiableSortedSet(
            new TreeSet<>(metricMap.keySet().stream()
                .map(MetricID::getName)
                .collect(Collectors.toSet())));
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
            if (entry.getValue() instanceof Gauge) {
                result.put(entry.getKey(), (Gauge) entry.getValue());
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SortedMap<MetricID, Gauge> getGauges(final MetricFilter filter) {
        throw new UnsupportedOperationException();
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
    public SortedMap<MetricID, ConcurrentGauge> getConcurrentGauges() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<MetricID, ConcurrentGauge> getConcurrentGauges(final MetricFilter filter) {
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
    public SortedMap<MetricID, Meter> getMeters() {
        return null;
    }

    @Override
    public SortedMap<MetricID, Meter> getMeters(final MetricFilter filter) {
        return null;
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers() {
        final SortedMap<MetricID, Timer> out = new TreeMap<>();
        for (final Map.Entry<MetricID, Metric> entry : metricMap.entrySet()) {
            if (entry.getValue() instanceof Timer) {
                out.put(entry.getKey(), (Timer) entry.getValue());
            }
        }
        return out;
    }

    @Override
    public SortedMap<MetricID, Timer> getTimers(final MetricFilter filter) {
        return null;
    }

    @Override
    public SortedMap<MetricID, SimpleTimer> getSimpleTimers() {
        return null;
    }

    @Override
    public SortedMap<MetricID, SimpleTimer> getSimpleTimers(final MetricFilter filter) {
        return null;
    }

    @Override
    public SortedMap<MetricID, Metric> getMetrics(MetricFilter metricFilter) {
        return null;
    }

    @Override
    public <T extends Metric> SortedMap<MetricID, T> getMetrics(Class<T> aClass, MetricFilter metricFilter) {
        return null;
    }

    @Override
    public Map<MetricID, Metric> getMetrics() {
        return null;
    }

    @Override
    public Map<String, Metadata> getMetadata() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
