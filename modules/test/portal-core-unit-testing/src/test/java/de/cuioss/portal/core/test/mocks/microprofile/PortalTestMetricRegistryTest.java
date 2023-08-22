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
package de.cuioss.portal.core.test.mocks.microprofile;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
@EnableGeneratorController
class PortalTestMetricRegistryTest implements ShouldBeNotNull<PortalTestMetricRegistry> {

    private static final TypedGenerator<Tag> tags = new MetricTagGenerator();
    private static final TypedGenerator<Metadata> metadata = new MetricMetadataGenerator();
    private static final TypedGenerator<MetricID> ids = new MetricIDGenerator();
    private static final TypedGenerator<String> names = letterStrings(1, 5);
    private static final TypedGenerator<Metric> metrics = new MetricGenerator();

    @Inject
    @Getter
    private PortalTestMetricRegistry underTest;

    @Test
    void shouldRegisterMetric() {
        var name = names.next();
        underTest.register(metadata.next(), metrics.next());
        underTest.register(name, metrics.next());
        underTest.register(metadata.next(), metrics.next(), tags.next());
        assertNotNull(underTest.getMetric(name));
        assertNotNull(underTest.getMetricID(name));
        assertTrue(underTest.getTags(name).isPresent());
    }

    @Test
    void shouldHandleMissingEntries() {
        assertNull(underTest.getMetadata(names.next()));
        assertNull(underTest.getType());
        assertTrue(underTest.getMetadata().isEmpty());
        assertFalse(underTest.getMetric(names.next()).isPresent());
        assertFalse(underTest.getMetricID(names.next()).isPresent());
        assertNull(underTest.getMetric(ids.next()));
        assertTrue(underTest.getMetricIDs().isEmpty());
        assertTrue(underTest.getNames().isEmpty());
        assertFalse(underTest.getTags(names.next()).isPresent());
        assertTrue(underTest.getMetrics().isEmpty());
        assertTrue(underTest.getMetrics((metricID, metric) -> true).isEmpty());
    }

    @Test
    void shouldReturnNullOnMissingImplementationsCounter() {
        assertNull(underTest.counter(ids.next()));
        assertNull(underTest.getCounter(ids.next()));
        assertNull(underTest.counter(names.next()));
        assertNull(underTest.counter(names.next(), tags.next()));
        assertNull(underTest.counter(metadata.next()));
        assertNull(underTest.counter(metadata.next(), tags.next()));
    }

    @Test
    void shouldReturnNullOnMissingImplementationsConcurrentGauge() {
        assertNull(underTest.concurrentGauge(ids.next()));
        assertNull(underTest.getConcurrentGauge(ids.next()));
        assertNull(underTest.concurrentGauge(names.next()));
        assertNull(underTest.concurrentGauge(names.next(), tags.next()));
        assertNull(underTest.concurrentGauge(metadata.next()));
        assertNull(underTest.concurrentGauge(metadata.next(), tags.next()));
        assertTrue(underTest.getConcurrentGauges().isEmpty());
        assertTrue(underTest.getConcurrentGauges((metricID, metric) -> true).isEmpty());
    }

    @Test
    void shouldReturnNullOnMissingImplementationsHistogram() {
        assertNull(underTest.histogram(ids.next()));
        assertNull(underTest.histogram(names.next()));
        assertNull(underTest.histogram(names.next(), tags.next()));
        assertNull(underTest.histogram(metadata.next()));
        assertNull(underTest.histogram(metadata.next(), tags.next()));
        assertTrue(underTest.getHistograms().isEmpty());
        assertNull(underTest.getHistogram(ids.next()));
        assertTrue(underTest.getHistograms((metricID, metric) -> true).isEmpty());
    }

    @Test
    void shouldReturnNullOnMissingImplementationsMeter() {
        assertNull(underTest.meter(ids.next()));
        assertNull(underTest.getMeter(ids.next()));
        assertNull(underTest.meter(names.next()));
        assertNull(underTest.meter(names.next(), tags.next()));
        assertNull(underTest.meter(metadata.next()));
        assertNull(underTest.meter(metadata.next(), tags.next()));
    }

    @Test
    void shouldReturnNullOnMissingImplementationsTimer() {
        assertNull(underTest.timer(ids.next()));
        assertNull(underTest.getTimer(ids.next()));
        var timeName = names.next();
        assertNull(underTest.timer(timeName));
        assertNull(underTest.timer(metadata.next()));
        assertNull(underTest.timer(metadata.next(), tags.next()));
        var timer = underTest.timer(timeName, tags.next());
        // Reentrant
        underTest.timer(timeName, tags.next());
        assertNotNull(timer);
        assertNotNull(timer.getElapsedTime());
        assertEquals(0, timer.getCount());
        assertEquals(0, timer.getFifteenMinuteRate());
        assertEquals(0, timer.getFiveMinuteRate());
        assertEquals(0, timer.getMeanRate());
        assertEquals(0, timer.getOneMinuteRate());
        assertNotNull(timer.time());
        assertDoesNotThrow(() -> timer.time().stop());
        assertThrows(RuntimeException.class, () -> timer.time((Callable<?>) null));
        assertThrows(RuntimeException.class, () -> timer.time((Runnable) null));
        assertThrows(RuntimeException.class, () -> timer.update(null));
        assertThrows(RuntimeException.class, () -> timer.getSnapshot());

        assertFalse(underTest.getTimers().isEmpty());
        assertTrue(underTest.getTimers((metricID, metric) -> true).isEmpty());
    }

    @Test
    void shouldReturnNullOnMissingImplementationsSimpleTimer() {
        assertNull(underTest.simpleTimer(ids.next()));
        assertNull(underTest.getSimpleTimer(ids.next()));
        assertNull(underTest.simpleTimer(names.next()));
        assertNull(underTest.simpleTimer(names.next(), tags.next()));
        assertNull(underTest.simpleTimer(metadata.next()));
        assertNull(underTest.simpleTimer(metadata.next(), tags.next()));
        assertTrue(underTest.getSimpleTimers().isEmpty());
        assertTrue(underTest.getSimpleTimers((metricID, metric) -> true).isEmpty());
    }

    @Test
    void shouldReturnCounterAndMeter() {
        assertTrue(underTest.getCounters().isEmpty());
        assertTrue(underTest.getCounters((metricID, metric) -> true).isEmpty());
        assertTrue(underTest.getMeters().isEmpty());
        assertTrue(underTest.getMeters((metricID, metric) -> true).isEmpty());

    }
}
