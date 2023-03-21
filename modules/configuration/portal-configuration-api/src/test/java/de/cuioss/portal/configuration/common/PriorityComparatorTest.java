package de.cuioss.portal.configuration.common;

import static de.cuioss.test.generator.Generators.fixedValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.common.support.HighPriorityClass;
import de.cuioss.portal.configuration.common.support.LowPriorityClass;
import de.cuioss.portal.configuration.common.support.MediumPriorityClass;
import de.cuioss.portal.configuration.common.support.NoPriorityClass;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.object.ObjectTestContracts;
import de.cuioss.test.valueobjects.api.object.VetoObjectTestContract;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;

@PropertyReflectionConfig(skip = true)
@VetoObjectTestContract(ObjectTestContracts.SERIALIZABLE)
class PriorityComparatorTest extends ValueObjectTest<PriorityComparator> {

    private final PriorityComparator noPriorityClassComparator = new PriorityComparator(
            new NoPriorityClass());

    private final PriorityComparator mediumClassComparator = new PriorityComparator(
            new MediumPriorityClass());

    private final PriorityComparator lowClassComparator = new PriorityComparator(
            new LowPriorityClass());

    private final PriorityComparator highClassComparator = new PriorityComparator(
            new HighPriorityClass());

    private final TypedGenerator<PriorityComparator> comparatorGenerator =
        fixedValues(mediumClassComparator, lowClassComparator, highClassComparator);

    @Test
    void shouldCreateConfiguredOrdering() {
        assertEquals(Integer.valueOf(PortalPriorities.DEFAULT_LEVEL),
                noPriorityClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_CORE_LEVEL),
                lowClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_MODULE_LEVEL),
                mediumClassComparator.getOrder());

        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_ASSEMBLY_LEVEL),
                highClassComparator.getOrder());
    }

    @Test
    void shouldSortCorrectly() {

        final List<PriorityComparator> sortList = Arrays.asList(noPriorityClassComparator,
                lowClassComparator, mediumClassComparator, highClassComparator);
        Collections.shuffle(sortList);
        Collections.sort(sortList);
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_ASSEMBLY_LEVEL),
                sortList.get(0).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_MODULE_LEVEL),
                sortList.get(1).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.PORTAL_CORE_LEVEL),
                sortList.get(2).getOrder());
        assertEquals(Integer.valueOf(PortalPriorities.DEFAULT_LEVEL), sortList.get(3).getOrder());
    }

    @Override
    protected PriorityComparator anyValueObject() {
        return comparatorGenerator.next();
    }

}
