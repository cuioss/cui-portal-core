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

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.Bean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Comparator implementation for ordering objects based on their {@link jakarta.annotation.Priority}
 * annotation value.
 * 
 * <h2>Overview</h2>
 * This class wraps objects and compares them based on their priority annotation.
 * Objects without a priority annotation are assigned a default priority of 0.
 * Higher priority values indicate higher precedence in sorting.
 * 
 * <h2>Usage</h2>
 * <pre>
 * // Create comparators for objects
 * PriorityComparator comp1 = new PriorityComparator(obj1);
 * PriorityComparator comp2 = new PriorityComparator(obj2);
 * 
 * // Compare objects
 * int result = comp1.compareTo(comp2);
 * </pre>
 * 
 * <h2>Priority Resolution</h2>
 * Priority is resolved in the following order:
 * <ol>
 *   <li>Direct {@link Priority} annotation on the object</li>
 *   <li>For CDI beans, {@link Priority} annotation on the bean class</li>
 *   <li>Default value of 0 if no priority is found</li>
 * </ol>
 * 
 * <p>Note: This class has a natural ordering that is inconsistent with equals.
 * Two objects may compare as equal even if they wrap different objects with the
 * same priority.
 *
 * @author Oliver Wolff
 * @see jakarta.annotation.Priority
 * @see PortalPriorities
 */
@ToString
@EqualsAndHashCode(of = "order")
public class PriorityComparator implements Comparable<PriorityComparator> {

    @Getter
    private final Integer order;

    @Getter
    private final Object wrapped;

    /**
     * Creates a new comparator wrapping the given object.
     *
     * @param wrappedObject the object to wrap and compare, must not be null
     * @throws NullPointerException if wrappedObject is null
     */
    public PriorityComparator(final Object wrappedObject) {
        requireNonNull(wrappedObject, "wrappedObject");

        Priority priority = null;
        if (wrappedObject instanceof Bean<?> bean) {
            priority = findPriorityAnnotation(bean.getBeanClass());
        }
        if (null == priority) {
            priority = findPriorityAnnotation(wrappedObject.getClass());
        }
        if (null == priority) {
            order = 0;
        } else {
            order = priority.value();
        }
        wrapped = wrappedObject;
    }

    /**
     * Recursively find the first appearance of the {@link Priority} annotation
     *
     * @param annotatedClass maybe null
     * @return
     */
    private Priority findPriorityAnnotation(final Class<?> annotatedClass) {
        if (null == annotatedClass) {
            return null;
        }
        final var prio = annotatedClass.getAnnotation(Priority.class);
        if (null != prio) {
            return prio;
        }
        if (null != annotatedClass.getSuperclass()) {
            return findPriorityAnnotation(annotatedClass.getSuperclass());
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final PriorityComparator other) {
        return other.getOrder().compareTo(order);
    }

}
