package de.cuioss.portal.metrics.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;

import org.eclipse.microprofile.metrics.Tag;
import org.junit.jupiter.api.Test;

class MetricIdBuilderTest {

    @Test
    void exceptionOnMissingName() {
        var builder = new MetricIdBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.build());
    }

    @Test
    void exceptionOnEmptyName() {
        var builder = new MetricIdBuilder().name("");
        assertThrows(IllegalArgumentException.class, () -> builder.build());
    }

    @Test
    void dontProcessExceptionMappersIfNoException() {
        var builder = new MetricIdBuilder().name("test").exceptionTagMapper(throwable -> {
            fail("ExceptionMapper must not be processed if there is no exception given.");
            return null;
        });
        assertDoesNotThrow(() -> builder.build());
    }

    @Test
    void processedExceptionMappers() {
        final var countDown = new CountDownLatch(2);
        var builder = new MetricIdBuilder().name("test").exception(new Exception()).exceptionTagMapper(throwable -> {
            countDown.countDown();
            return null;
        }).exceptionTagMapper(throwable -> {
            countDown.countDown();
            return null;
        });
        assertDoesNotThrow(() -> builder.build());
        assertEquals(0L, countDown.getCount());
    }

    @Test
    void addsTags() {
        var testTag1 = new Tag("test1", "value");
        var testTag2 = new Tag("test2", "value");
        var testTag3 = new Tag("test3", "value");

        var builder = new MetricIdBuilder().name("test").tag(testTag1).tag(testTag2).tags(new Tag[] { testTag3 });
        var metricID = assertDoesNotThrow(() -> builder.build());

        assertTrue(metricID.getTags().containsKey("test1"), "test Tag 1 missing");
        assertTrue(metricID.getTags().containsKey("test2"), "test Tag 2 missing");
        assertTrue(metricID.getTags().containsKey("test3"), "test Tag 3 missing");
    }
}
