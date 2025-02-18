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
package de.cuioss.portal.common.cdi;

import de.cuioss.tools.collect.CollectionLiterals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tests the AnnotationInstanceProvider utility")
class AnnotationInstanceProviderTest {

    @Nested
    @DisplayName("Basic Annotation Creation Tests")
    class BasicAnnotationTests {

        @Test
        @DisplayName("Should create basic annotation instances")
        void shouldHandleBasicAnnotations() {
            var literal = AnnotationInstanceProvider.of(SuppressWarnings.class);

            assertAll("Basic annotation verification",
                    () -> assertNotNull(literal.toString(), "toString should not be null"),
                    () -> assertEquals(AnnotationInstanceProvider.of(SuppressWarnings.class), literal,
                            "Same annotation class should create equal instances"),
                    () -> assertEquals(0, literal.hashCode(), "Empty annotation should have hashCode 0"),
                    () -> assertEquals(SuppressWarnings.class, literal.annotationType(),
                            "Should return correct annotation type")
            );
        }

        @Test
        @DisplayName("Should create different annotation types")
        void shouldHandleDifferentAnnotationTypes() {
            assertAll("Different annotation types verification",
                    () -> assertInstanceOf(Deprecated.class,
                            AnnotationInstanceProvider.of(Deprecated.class),
                            "Should create Deprecated annotation instance"),
                    () -> assertInstanceOf(SuppressWarnings.class,
                            AnnotationInstanceProvider.of(SuppressWarnings.class),
                            "Should create SuppressWarnings annotation instance")
            );
        }
    }

    @Nested
    @DisplayName("Annotation With Attributes Tests")
    class AnnotationWithAttributesTests {

        @Test
        @DisplayName("Should create annotation with attributes")
        void shouldHandleAnnotationWithAttributes() {
            var attributes = CollectionLiterals.immutableMap("value", "test");
            var literal = AnnotationInstanceProvider.of(SuppressWarnings.class, attributes);

            assertAll("Annotation with attributes verification",
                    () -> assertNotNull(literal.toString(), "toString should not be null"),
                    () -> assertEquals(
                            AnnotationInstanceProvider.of(SuppressWarnings.class, attributes),
                            literal,
                            "Same annotation with same attributes should be equal"),
                    () -> assertNotEquals(0, literal.hashCode(),
                            "Annotation with attributes should have non-zero hashCode"),
                    () -> assertEquals(SuppressWarnings.class, literal.annotationType(),
                            "Should maintain correct annotation type with attributes")
            );
        }

        @Test
        @DisplayName("Should handle empty attributes map")
        void shouldHandleEmptyAttributes() {
            var literal = AnnotationInstanceProvider.of(SuppressWarnings.class,
                    CollectionLiterals.immutableMap());

            assertAll("Empty attributes verification",
                    () -> assertNotNull(literal, "Should create instance with empty attributes"),
                    () -> assertEquals(0, literal.hashCode(),
                            "Empty attributes should result in hashCode 0"),
                    () -> assertEquals(
                            AnnotationInstanceProvider.of(SuppressWarnings.class),
                            literal,
                            "Should equal instance created without attributes")
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should reject null annotation class")
        void shouldRejectNullAnnotationClass() {
            assertThrows(NullPointerException.class,
                    () -> AnnotationInstanceProvider.of(null),
                    "Should throw NullPointerException for null annotation class");
        }

        @Test
        @DisplayName("Should reject null attributes map")
        void shouldRejectNullAttributesMap() {
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                    () -> AnnotationInstanceProvider.of(SuppressWarnings.class, null),
                    "Should throw IllegalArgumentException for null attributes map");

            assertEquals("Map of values must not be null", thrown.getMessage(),
                    "Should indicate that values map cannot be null");
        }
    }
}
