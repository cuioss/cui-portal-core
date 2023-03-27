package de.cuioss.portal.configuration.initializer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;

class ApplicationInitializerTest {

    @Test
    void shouldHandleCompareCorrectly() {
        var early = new TestInitializer(ApplicationInitializer.ORDER_EARLY);
        var intermediate = new TestInitializer(ApplicationInitializer.ORDER_INTERMEDIATE);
        var late = new TestInitializer(ApplicationInitializer.ORDER_LATE);

        assertEquals(0, intermediate.compareTo(intermediate));
        assertEquals(-1, early.compareTo(intermediate));
        assertEquals(1, late.compareTo(intermediate));

    }

    @Test
    void shouldImplementNoOpDestroy() {
        assertDoesNotThrow(() -> new TestInitializer(ApplicationInitializer.ORDER_EARLY).destroy());
    }

    @Test
    void shouldDefaultToIntermediate() {
        ApplicationInitializer actual = () -> {
        };
        assertEquals(ApplicationInitializer.ORDER_INTERMEDIATE, actual.getOrder());
    }

    @AllArgsConstructor
    static class TestInitializer implements ApplicationInitializer {

        @Getter
        private Integer order;

        @Override
        public void initialize() {
        }

    }
}
