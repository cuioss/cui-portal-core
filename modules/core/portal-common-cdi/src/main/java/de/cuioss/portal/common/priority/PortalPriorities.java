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

import jakarta.annotation.Priority;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for managing priority-based ordering of portal components.
 * 
 * <h2>Overview</h2>
 * Provides constants and methods for ordering components based on the
 * {@link jakarta.annotation.Priority} annotation. Components without the annotation
 * default to {@link #DEFAULT_LEVEL}.
 * 
 * <h2>Priority Levels</h2>
 * <ul>
 *   <li>{@link #PORTAL_CORE_LEVEL}: Core portal components (10)</li>
 *   <li>{@link #PORTAL_MODULE_LEVEL}: Standard portal modules (50)</li>
 *   <li>{@link #PORTAL_ASSEMBLY_LEVEL}: Assembly-specific components (100)</li>
 *   <li>{@link #PORTAL_INSTALLATION_LEVEL}: Installation-specific components (150)</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>
 * // Sort components by priority
 * List&lt;MyComponent&gt; components = new ArrayList&lt;&gt;();
 * List&lt;MyComponent&gt; sorted = PortalPriorities.sortByPriority(components);
 * 
 * // Define component priority
 * &#064;Priority(PortalPriorities.PORTAL_MODULE_LEVEL)
 * public class MyComponent {
 *     // Implementation
 * }
 * </pre>
 * 
 * <p>Higher priority values indicate higher precedence in the ordering.
 * Components are sorted in descending order of priority.
 *
 * @author Oliver Wolff
 * @see jakarta.annotation.Priority
 */
@UtilityClass
public final class PortalPriorities {

    /**
     * Specifies the priority / ordering for the object having no {@link Priority}
     * annotation, integer value will be 0;
     */
    public static final int DEFAULT_LEVEL = 0;

    /**
     * Specifies the priority / ordering for the core, integer value will be 10;
     */
    public static final int PORTAL_CORE_LEVEL = 10;

    /**
     * Specifies the priority / ordering for a concrete module that will override
     * {@link #PORTAL_CORE_LEVEL}, integer value will be 50;
     */
    public static final int PORTAL_MODULE_LEVEL = 50;

    /**
     * Specifies the priority / ordering for an assembly that will override
     * {@link #PORTAL_MODULE_LEVEL}, integer value will be 100;
     */
    public static final int PORTAL_ASSEMBLY_LEVEL = 100;

    /**
     * Specifies the priority / ordering for an installation-specific priority that
     * will override {@link #PORTAL_ASSEMBLY_LEVEL}, integer value will be 150;
     */
    public static final int PORTAL_INSTALLATION_LEVEL = 150;

    /**
     * Sorts a list of objects based on their {@link jakarta.annotation.Priority} annotation.
     * Objects without the annotation are assigned {@link #DEFAULT_LEVEL} priority.
     *
     * @param toBeSorted the list to be sorted, must not be null
     * @param <T> the type of objects in the list, must be annotatable with {@link Priority}
     * @return a new sorted list in descending priority order, or the original list if size &lt; 2
     * @throws NullPointerException if toBeSorted is null
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sortByPriority(final List<T> toBeSorted) {
        if (null == toBeSorted || toBeSorted.size() < 2) {
            return toBeSorted;
        }
        final List<PriorityComparator> wrapperList = new ArrayList<>();
        for (final T element : toBeSorted) {
            wrapperList.add(new PriorityComparator(element));
        }
        Collections.sort(wrapperList);
        final List<T> result = new ArrayList<>();
        for (final PriorityComparator comparator : wrapperList) {
            result.add((T) comparator.getWrapped());
        }
        return result;
    }
}
