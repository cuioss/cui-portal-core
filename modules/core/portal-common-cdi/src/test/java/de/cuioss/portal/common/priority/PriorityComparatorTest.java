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

import static de.cuioss.test.generator.Generators.fixedValues;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.common.priority.support.*;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.object.ObjectTestContracts;
import de.cuioss.test.valueobjects.api.object.VetoObjectTestContract;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@PropertyReflectionConfig(skip = true)
@VetoObjectTestContract(ObjectTestContracts.SERIALIZABLE)
@DisplayName("Tests the PriorityComparator implementation")
class PriorityComparatorTest extends ValueObjectTest<PriorityComparator> {

    private final PriorityComparator noPriorityClassComparator = new PriorityComparator(new NoPriorityClass());
    private final PriorityComparator mediumClassComparator = new PriorityComparator(new MediumPriorityClass());
    private final PriorityComparator lowClassComparator = new PriorityComparator(new LowPriorityClass());
    private final PriorityComparator highClassComparator = new PriorityComparator(new HighPriorityClass());

    private final TypedGenerator<PriorityComparator> comparatorGenerator = fixedValues(mediumClassComparator,
            lowClassComparator, highClassComparator);

    @Nested
    @DisplayName("Priority Order Tests")
    class PriorityOrderTests {

        @Test
        @DisplayName("Should create configured priority ordering")
        void shouldCreateConfiguredOrdering() {
            assertAll("Priority level verification",
                () -> assertEquals(PortalPriorities.DEFAULT_LEVEL, noPriorityClassComparator.getOrder(),
                        "No priority class should have default level"),
                () -> assertEquals(PortalPriorities.PORTAL_CORE_LEVEL, lowClassComparator.getOrder(),
                        "Low priority class should have core level"),
                () -> assertEquals(PortalPriorities.PORTAL_MODULE_LEVEL, mediumClassComparator.getOrder(),
                        "Medium priority class should have module level"),
                () -> assertEquals(PortalPriorities.PORTAL_ASSEMBLY_LEVEL, highClassComparator.getOrder(),
                        "High priority class should have assembly level")
            );
        }

        @Test
        @DisplayName("Should sort elements in correct priority order")
        void shouldSortCorrectly() {
            final List<PriorityComparator> sortList = Arrays.asList(noPriorityClassComparator, lowClassComparator,
                    mediumClassComparator, highClassComparator);
            Collections.shuffle(sortList);
            Collections.sort(sortList);

            assertAll("Priority order verification",
                () -> assertEquals(PortalPriorities.PORTAL_ASSEMBLY_LEVEL, sortList.get(0).getOrder(),
                        "Highest priority should be first"),
                () -> assertEquals(PortalPriorities.PORTAL_MODULE_LEVEL, sortList.get(1).getOrder(),
                        "Medium priority should be second"),
                () -> assertEquals(PortalPriorities.PORTAL_CORE_LEVEL, sortList.get(2).getOrder(),
                        "Low priority should be third"),
                () -> assertEquals(PortalPriorities.DEFAULT_LEVEL, sortList.get(3).getOrder(),
                        "Default priority should be last")
            );
        }

        @Test
        @DisplayName("Should handle equal priority objects")
        void shouldHandleEqualPriorities() {
            final var firstNoPriority = new PriorityComparator(new NoPriorityClass());
            final var secondNoPriority = new PriorityComparator(new NoPriorityClass());

            assertAll("Equal priority verification",
                () -> assertEquals(0, firstNoPriority.compareTo(secondNoPriority),
                        "Equal priority objects should return 0"),
                () -> assertEquals(firstNoPriority.getOrder(), secondNoPriority.getOrder(),
                        "Equal priority objects should have same order")
            );
        }

        @Test
        @DisplayName("Should handle null input correctly")
        void shouldHandleNullInput() {
            assertThrows(NullPointerException.class,
                () -> new PriorityComparator(null),
                "Should throw NullPointerException for null wrapped object");
        }

        @Test
        @DisplayName("Should maintain stable ordering for equal priorities")
        void shouldMaintainStableOrderingForEqualPriorities() {
            final var first = new PriorityComparator(new NoPriorityClass());
            final var second = new PriorityComparator(new NoPriorityClass());
            final var third = new PriorityComparator(new NoPriorityClass());

            final List<PriorityComparator> originalList = Arrays.asList(first, second, third);
            final List<PriorityComparator> shuffledList = Arrays.asList(first, second, third);
            Collections.shuffle(shuffledList);
            Collections.sort(shuffledList);

            assertAll("Stable ordering verification",
                () -> assertEquals(originalList.size(), shuffledList.size(),
                        "Sorted list should maintain same size"),
                () -> assertTrue(shuffledList.containsAll(originalList),
                        "Sorted list should contain all original elements"),
                () -> assertEquals(first.getOrder(), shuffledList.get(0).getOrder(),
                        "First element should have same order"),
                () -> assertEquals(second.getOrder(), shuffledList.get(1).getOrder(),
                        "Second element should have same order"),
                () -> assertEquals(third.getOrder(), shuffledList.get(2).getOrder(),
                        "Third element should have same order"),
                () -> assertEquals(0, first.compareTo(second),
                        "First and second elements should be equal"),
                () -> assertEquals(0, second.compareTo(third),
                        "Second and third elements should be equal"),
                () -> assertEquals(0, first.compareTo(third),
                        "First and third elements should be equal")
            );
        }

    }

    @Override
    protected PriorityComparator anyValueObject() {
        return comparatorGenerator.next();
    }
}
