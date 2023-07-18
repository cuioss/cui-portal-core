package de.cuioss.portal.configuration.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.common.support.HighPriorityClass;
import de.cuioss.portal.configuration.common.support.LowPriorityClass;
import de.cuioss.portal.configuration.common.support.MediumPriorityClass;
import de.cuioss.portal.configuration.common.support.NoPriorityClass;
import de.cuioss.portal.configuration.common.support.SomeInterface;

class PortalPrioritiesTest {

    @Test
    void testSortByPriority() {
        final List<SomeInterface> elements = Arrays.asList(new HighPriorityClass(), new LowPriorityClass(),
                new MediumPriorityClass(), new NoPriorityClass());

        Collections.shuffle(elements);

        final List<SomeInterface> sorted = PortalPriorities.sortByPriority(elements);

        assertEquals(HighPriorityClass.class, sorted.get(0).getClass());
        assertEquals(MediumPriorityClass.class, sorted.get(1).getClass());
        assertEquals(LowPriorityClass.class, sorted.get(2).getClass());
        assertEquals(NoPriorityClass.class, sorted.get(3).getClass());
    }

}
