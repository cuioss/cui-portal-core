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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

@DisplayName("MetricIdBuilder Tests")
class MetricIdBuilderTest {

    @Nested
    @DisplayName("Parameter Validation")
    class ValidationTests {
        @Test
        @DisplayName("Should throw on missing name")
        void exceptionOnMissingName() {
            var builder = new MetricIdBuilder();
            assertThrows(IllegalArgumentException.class, builder::build,
                    "Builder should throw on missing name");
        }

        @Test
        @DisplayName("Should throw on empty name")
        void exceptionOnEmptyName() {
            var builder = new MetricIdBuilder().name("");
            assertThrows(IllegalArgumentException.class, builder::build,
                    "Builder should throw on empty name");
        }
    }

    @Nested
    @DisplayName("Exception Mapper Tests")
    class ExceptionMapperTests {
        @Test
        @DisplayName("Should not process mappers without exception")
        void dontProcessExceptionMappersIfNoException() {
            var builder = new MetricIdBuilder().name("test").exceptionTagMapper(throwable -> {
                fail("ExceptionMapper must not be processed if there is no exception given.");
                return null;
            });
            assertDoesNotThrow(builder::build,
                    "Builder should not process mappers without exception");
        }

        @Test
        @DisplayName("Should process all exception mappers")
        void processedExceptionMappers() {
            // Given
            final var countDown = new CountDownLatch(2);

            // When
            var builder = new MetricIdBuilder()
                    .name("test")
                    .exception(new RuntimeException("Test exception"))
                    .exceptionTagMapper(throwable -> {
                        countDown.countDown();
                        return new Tag("mapper1", throwable.getMessage());
                    })
                    .exceptionTagMapper(throwable -> {
                        countDown.countDown();
                        return new Tag("mapper2", throwable.getClass().getSimpleName());
                    });

            // Then
            var metricId = assertDoesNotThrow(builder::build,
                    "Builder should process mappers without throwing");
            assertEquals(0L, countDown.getCount(), "All mappers should be processed");
            assertEquals("Test exception", metricId.getTags().get("mapper1"),
                    "First mapper should add exception message");
            assertEquals("RuntimeException", metricId.getTags().get("mapper2"),
                    "Second mapper should add exception class");
        }
    }

    @Nested
    @DisplayName("Tag Management Tests")
    class TagTests {
        @Test
        @DisplayName("Should add all tags correctly")
        void shouldAddAllTags() {
            // Given
            var testTag1 = new Tag("test1", "value1");
            var testTag2 = new Tag("test2", "value2");
            var testTag3 = new Tag("test3", "value3");

            // When
            var builder = new MetricIdBuilder()
                    .name("test")
                    .tag(testTag1)
                    .tag(testTag2)
                    .tags(new Tag[]{testTag3});
            var metricID = assertDoesNotThrow(builder::build,
                    "Builder should handle multiple tags without throwing");

            // Then
            var tags = metricID.getTags();
            assertEquals("value1", tags.get("test1"), "First tag should have correct value");
            assertEquals("value2", tags.get("test2"), "Second tag should have correct value");
            assertEquals("value3", tags.get("test3"), "Third tag should have correct value");
        }

        @Test
        @DisplayName("Should handle null tags gracefully")
        void shouldHandleNullTags() {
            var builder = new MetricIdBuilder()
                    .name("test")
                    .tag(null)
                    .tags((Tag[]) null);

            assertDoesNotThrow(builder::build,
                    "Builder should handle null tags without throwing");
        }
    }
}
