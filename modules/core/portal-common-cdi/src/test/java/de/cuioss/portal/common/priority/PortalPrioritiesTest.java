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
package de.cuioss.portal.common.priority;

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.common.priority.support.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DisplayName("Tests the PortalPriorities utility class")
class PortalPrioritiesTest {

    @Nested
    @DisplayName("Priority Sorting Tests")
    class PrioritySortingTests {

        @Test
        @DisplayName("Should sort elements by priority in correct order")
        void sortByPriority() {
            final List<SomeInterface> elements = Arrays.asList(new HighPriorityClass(), new LowPriorityClass(),
                    new MediumPriorityClass(), new NoPriorityClass());

            Collections.shuffle(elements);

            final List<SomeInterface> sorted = PortalPriorities.sortByPriority(elements);

            assertAll("Priority sorting verification",
                () -> assertEquals(HighPriorityClass.class, sorted.get(0).getClass(),
                        "High priority class should be first"),
                () -> assertEquals(MediumPriorityClass.class, sorted.get(1).getClass(),
                        "Medium priority class should be second"),
                () -> assertEquals(LowPriorityClass.class, sorted.get(2).getClass(),
                        "Low priority class should be third"),
                () -> assertEquals(NoPriorityClass.class, sorted.get(3).getClass(),
                        "No priority class should be last")
            );
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            final List<SomeInterface> elements = Collections.emptyList();
            final List<SomeInterface> sorted = PortalPriorities.sortByPriority(elements);
            assertTrue(sorted.isEmpty(), "Sorted list should be empty");
        }

        @Test
        @DisplayName("Should reject null elements with NullPointerException")
        void shouldRejectNullElements() {
            final List<SomeInterface> elements = Arrays.asList(null, new HighPriorityClass());

            NullPointerException thrown = assertThrows(
                NullPointerException.class,
                () -> PortalPriorities.sortByPriority(elements),
                "Should throw NullPointerException for null elements"
            );

            assertEquals("wrappedObject", thrown.getMessage(),
                    "Should indicate that wrapped object cannot be null");
        }

        @Test
        @DisplayName("Should reject null list with NullPointerException")
        void shouldRejectNullList() {
            assertThrows(NullPointerException.class,
                () -> PortalPriorities.sortByPriority(null),
                "Should throw NullPointerException for null list");
        }

        @Test
        @DisplayName("Should handle single element list")
        void shouldHandleSingleElementList() {
            final List<SomeInterface> elements = Collections.singletonList(new HighPriorityClass());
            final List<SomeInterface> sorted = PortalPriorities.sortByPriority(elements);

            assertAll("Single element list verification",
                () -> assertEquals(1, sorted.size(), "Sorted list should have one element"),
                () -> assertEquals(HighPriorityClass.class, sorted.get(0).getClass(),
                        "Element should be preserved")
            );
        }

        @Test
        @DisplayName("Should maintain order for equal priorities")
        void shouldMaintainOrderForEqualPriorities() {
            final var first = new NoPriorityClass();
            final var second = new NoPriorityClass();
            final List<SomeInterface> elements = Arrays.asList(first, second);

            final List<SomeInterface> sorted = PortalPriorities.sortByPriority(elements);

            assertAll("Equal priorities verification",
                () -> assertEquals(2, sorted.size(), "Sorted list should maintain size"),
                () -> assertEquals(NoPriorityClass.class, sorted.get(0).getClass(),
                        "First element should be NoPriorityClass"),
                () -> assertEquals(NoPriorityClass.class, sorted.get(1).getClass(),
                        "Second element should be NoPriorityClass")
            );
        }
    }
}
