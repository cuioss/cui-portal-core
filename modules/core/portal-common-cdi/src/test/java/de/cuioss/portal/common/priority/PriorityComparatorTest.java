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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.common.priority.support.HighPriorityClass;
import de.cuioss.portal.common.priority.support.LowPriorityClass;
import de.cuioss.portal.common.priority.support.MediumPriorityClass;
import de.cuioss.portal.common.priority.support.NoPriorityClass;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.object.ObjectTestContracts;
import de.cuioss.test.valueobjects.api.object.VetoObjectTestContract;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;

@PropertyReflectionConfig(skip = true)
@VetoObjectTestContract(ObjectTestContracts.SERIALIZABLE)
class PriorityComparatorTest extends ValueObjectTest<PriorityComparator> {

    private final PriorityComparator noPriorityClassComparator = new PriorityComparator(new NoPriorityClass());

    private final PriorityComparator mediumClassComparator = new PriorityComparator(new MediumPriorityClass());

    private final PriorityComparator lowClassComparator = new PriorityComparator(new LowPriorityClass());

    private final PriorityComparator highClassComparator = new PriorityComparator(new HighPriorityClass());

    private final TypedGenerator<PriorityComparator> comparatorGenerator = fixedValues(mediumClassComparator,
            lowClassComparator, highClassComparator);

    @Test
    void shouldCreateConfiguredOrdering() {
        assertEquals(Integer.valueOf(PortalPriorities.DEFAULT_LEVEL), noPriorityClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_CORE_LEVEL), lowClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_MODULE_LEVEL), mediumClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_ASSEMBLY_LEVEL), highClassComparator.getOrder());
    }

    @Test
    void shouldSortCorrectly() {

        final List<PriorityComparator> sortList = Arrays.asList(noPriorityClassComparator, lowClassComparator,
                mediumClassComparator, highClassComparator);
        Collections.shuffle(sortList);
        Collections.sort(sortList);
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_ASSEMBLY_LEVEL), sortList.get(0).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_MODULE_LEVEL), sortList.get(1).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_CORE_LEVEL), sortList.get(2).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.DEFAULT_LEVEL), sortList.get(3).getOrder());
    }

    @Override
    protected PriorityComparator anyValueObject() {
        return comparatorGenerator.next();
    }

}
