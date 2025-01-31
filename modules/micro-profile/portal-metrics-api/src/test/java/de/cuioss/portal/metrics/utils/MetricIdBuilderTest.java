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
package de.cuioss.portal.metrics.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.microprofile.metrics.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

class MetricIdBuilderTest {

    @Test
    void exceptionOnMissingName() {
        var builder = new MetricIdBuilder();
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void exceptionOnEmptyName() {
        var builder = new MetricIdBuilder().name("");
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void dontProcessExceptionMappersIfNoException() {
        var builder = new MetricIdBuilder().name("test").exceptionTagMapper(throwable -> {
            fail("ExceptionMapper must not be processed if there is no exception given.");
            return null;
        });
        assertDoesNotThrow(builder::build);
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
        assertDoesNotThrow(builder::build);
        assertEquals(0L, countDown.getCount());
    }

    @Test
    void addsTags() {
        var testTag1 = new Tag("test1", "value");
        var testTag2 = new Tag("test2", "value");
        var testTag3 = new Tag("test3", "value");

        var builder = new MetricIdBuilder().name("test").tag(testTag1).tag(testTag2).tags(new Tag[]{testTag3});
        var metricID = assertDoesNotThrow(builder::build);

        assertTrue(metricID.getTags().containsKey("test1"), "test Tag 1 missing");
        assertTrue(metricID.getTags().containsKey("test2"), "test Tag 2 missing");
        assertTrue(metricID.getTags().containsKey("test3"), "test Tag 3 missing");
    }
}
