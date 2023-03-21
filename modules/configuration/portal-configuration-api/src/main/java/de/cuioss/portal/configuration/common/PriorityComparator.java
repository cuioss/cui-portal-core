package de.cuioss.portal.configuration.common;

import static java.util.Objects.requireNonNull;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.Bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Helper class for sorting elements regarding to its {@link Priority} annotation. In case the given
 * class does not have a {@link Priority} annotation present an order of
 * {@code 0} is assumed.
 *
 * @author Oliver Wolff
 */
@ToString
@EqualsAndHashCode(of = "order")
public class PriorityComparator implements Comparable<PriorityComparator> {

    @Getter
    private final Integer order;

    @Getter
    private final Object wrapped;

    /**
     * @param wrappedObject must not be null
     */
    public PriorityComparator(final Object wrappedObject) {
        requireNonNull(wrappedObject, "wrappedObject");

        Priority priority = null;
        if (wrappedObject instanceof Bean<?>) {
            priority = findPriorityAnnotation(((Bean<?>) wrappedObject).getBeanClass());
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
     * @param annotatedClass may be null
     * @return
     */
    private Priority findPriorityAnnotation(final Class<? extends Object> annotatedClass) {
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

    @Override
    public int compareTo(final PriorityComparator other) {
        return other.getOrder().compareTo(order);
    }

}
