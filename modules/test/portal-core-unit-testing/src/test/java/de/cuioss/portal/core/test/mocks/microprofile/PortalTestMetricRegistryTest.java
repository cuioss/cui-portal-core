package de.cuioss.portal.core.test.mocks.microprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.generator.junit.EnableGeneratorController;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

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
