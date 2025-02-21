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

import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricID;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoWeld
@EnableGeneratorController
@DisplayName("PortalTestMetricRegistry Tests")
class PortalTestMetricRegistryTest implements ShouldBeNotNull<PortalTestMetricRegistry> {

    private static final String METRIC_NAME = "test.metric";

    @Getter
    private PortalTestMetricRegistry underTest;

    @BeforeEach
    void setUp() {
        underTest = new PortalTestMetricRegistry();
    }

    @Nested
    @DisplayName("Counter Operations")
    class CounterOperationsTest {

        @Test
        @DisplayName("Should create and retrieve counter")
        void shouldCreateAndRetrieveCounter() {
            Counter counter = underTest.counter(METRIC_NAME);
            assertNotNull(counter, "Counter should be created");
            assertEquals(counter, underTest.getCounter(new MetricID(METRIC_NAME)),
                    "Should retrieve the same counter instance");
        }

        @Test
        @DisplayName("Should create counter with metadata")
        void shouldCreateCounterWithMetadata() {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .build();
            Counter counter = underTest.counter(metadata);
            assertNotNull(counter, "Counter with metadata should be created");
            assertEquals(counter, underTest.getCounter(new MetricID(METRIC_NAME)),
                    "Should retrieve counter by ID");
        }
    }

    @Nested
    @DisplayName("Metadata Operations")
    class MetadataOperationsTest {

        @Test
        @DisplayName("Should handle metadata registration")
        void shouldHandleMetadataRegistration() {
            Metadata metadata = Metadata.builder()
                    .withName(METRIC_NAME)
                    .build();
            Counter counter = underTest.counter(metadata);

            assertNotNull(counter, "Counter should be created");
            assertNotNull(underTest.getMetadata(METRIC_NAME),
                    "Metadata should be stored");
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTest {

        @Test
        @DisplayName("Should return null for non-existent metrics")
        void shouldReturnNullForNonExistentMetrics() {
            assertNull(underTest.getCounter(new MetricID("non.existent")),
                    "Should return null for non-existent counter");
            assertNull(underTest.getMetadata("non.existent"),
                    "Should return null for non-existent metadata");
        }
    }

    @Test
    @DisplayName("Should handle metric removal")
    void shouldHandleMetricRemoval() {
        Counter counter = underTest.counter(METRIC_NAME);
        assertNotNull(counter, "Counter should be created");

        underTest.remove(METRIC_NAME);
        assertNull(underTest.getCounter(new MetricID(METRIC_NAME)),
                "Counter should be removed");
    }
}
