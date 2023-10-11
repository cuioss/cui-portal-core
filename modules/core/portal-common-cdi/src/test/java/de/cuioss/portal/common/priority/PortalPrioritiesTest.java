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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.common.priority.support.HighPriorityClass;
import de.cuioss.portal.common.priority.support.LowPriorityClass;
import de.cuioss.portal.common.priority.support.MediumPriorityClass;
import de.cuioss.portal.common.priority.support.NoPriorityClass;
import de.cuioss.portal.common.priority.support.SomeInterface;

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
