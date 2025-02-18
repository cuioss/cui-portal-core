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

import de.cuioss.portal.common.priority.PriorityComparator;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static java.util.Objects.requireNonNull;

/**
 * Utility class providing convenient methods for programmatic CDI bean access
 * and management.
 * 
 * <h2>Overview</h2>
 * This class simplifies common CDI operations such as bean lookup, instantiation,
 * and annotation-based filtering.
 * It provides type-safe access to CDI beans while handling common edge cases.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Type-safe bean lookup and instantiation</li>
 *   <li>Annotation-based bean filtering</li>
 *   <li>Priority-based bean ordering</li>
 *   <li>Proper resource cleanup</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>
 * // Simple bean lookup
 * MyBean bean = PortalBeanManager.getBean(MyBean.class);
 * 
 * // Lookup with qualifier
 * MyBean bean = PortalBeanManager.getBean(MyBean.class, MyQualifier.class);
 * 
 * // Get all beans of a type, sorted by priority
 * List<MyInterface> beans = PortalBeanManager.getBeans(MyInterface.class);
 * 
 * // Create bean instance with specific qualifiers
 * Set<Annotation> qualifiers = new HashSet<>();
 * qualifiers.add(new MyQualifierLiteral());
 * MyBean bean = PortalBeanManager.create(beanManager, MyBean.class, qualifiers);
 * </pre>
 * 
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>Throws {@link IllegalArgumentException} for invalid bean lookups</li>
 *   <li>Returns empty collections instead of null for multi-bean lookups</li>
 *   <li>Logs warnings for ambiguous bean resolutions</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @see BeanManager
 * @see CDI
 */
@UtilityClass
public final class PortalBeanManager {

    private static final CuiLogger LOGGER = new CuiLogger(PortalBeanManager.class);

    /**
     * Looks up a normal scoped CDI bean programmatically.
     *
     * @param beanManager the bean manager to use for lookup, must not be null
     * @param beanClass the type of bean to look up, must not be null
     * @param annotationClass optional qualifier annotation class, may be null
     * @param <T> the type of the bean to resolve
     * @param <V> the type of the qualifier annotation
     * @return the found bean instance, never null
     * @throws IllegalArgumentException if the bean cannot be uniquely identified
     * @throws NullPointerException if beanManager or beanClass is null
     */
    @SuppressWarnings("unchecked")
    private static <T, V extends Annotation> T getCDIBean(final BeanManager beanManager, final Class<T> beanClass,
            final Class<V> annotationClass) {
        requireNonNull(beanManager, "beanManager");
        requireNonNull(beanClass, "beanClass");

        LOGGER.debug("Attempting to resolve bean of type '%s' with qualifier '%s'",
                beanClass.getName(), annotationClass != null ? annotationClass.getName() : "none");

        final Set<Bean<?>> beanTypes = resolveBeanTypes(beanManager, beanClass, annotationClass);
        checkBeanTypesFound(beanClass, annotationClass, beanTypes);
        final var sortedBeans = sortByPriority(mutableList(beanTypes));
        // Wild casting here
        final var bean = (Bean<T>) sortedBeans.iterator().next();
        final CreationalContext<T> context = beanManager.createCreationalContext(bean);
        // Wild casting again
        T result = (T) beanManager.getReference(bean, beanClass, context);

        LOGGER.debug("Successfully resolved bean: %s", result);
        return result;
    }

    /**
     * Sorts a list of CDI beans by their priority.
     *
     * @param toBeSorted the list of beans to sort, must not be null
     * @return sorted list of beans in descending priority order
     */
    private static List<Bean<?>> sortByPriority(final List<Bean<?>> toBeSorted) {
        if (toBeSorted.size() < 2)
            return toBeSorted;
        final List<PriorityComparator> wrapperList = new ArrayList<>();
        for (final Bean<?> element : toBeSorted) {
            wrapperList.add(new PriorityComparator(element));
        }
        Collections.sort(wrapperList);
        final List<Bean<?>> result = new ArrayList<>();
        for (final PriorityComparator comparator : wrapperList) {
            result.add((Bean<?>) comparator.getWrapped());
        }
        return result;
    }

    /**
     * Resolves a CDI bean instance.
     *
     * @param beanClass the type of bean to resolve, must not be null
     * @param annotationClass optional qualifier annotation class, may be null
     * @param <T> the type of the bean to resolve
     * @param <V> the type of the qualifier annotation
     * @return Optional containing the resolved bean, or empty if not found
     * @throws IllegalArgumentException if the bean cannot be uniquely identified
     * @throws NullPointerException if beanClass is null
     */
    public static <T, V extends Annotation> Optional<T> resolveBean(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return Optional.ofNullable(getCDIBean(getBeanManager(), beanClass, annotationClass));
    }

    /**
     * Resolves a required CDI bean instance.
     *
     * @param beanClass the type of bean to resolve, must not be null
     * @param annotationClass optional qualifier annotation class, may be null
     * @param <T> the type of the bean to resolve
     * @param <V> the type of the qualifier annotation
     * @return the resolved bean instance, never null
     * @throws IllegalStateException if no bean could be found
     * @throws NullPointerException if beanClass is null
     */
    public static <T, V extends Annotation> T resolveBeanOrThrowIllegalStateException(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return Optional.ofNullable(getCDIBean(getBeanManager(), beanClass, annotationClass)).orElseThrow(
                () -> new IllegalStateException("Portal-532: " + createErrorMessage(beanClass, annotationClass)));
    }

    /**
     * Resolves a required CDI bean without qualifier.
     *
     * @param beanClass the type of bean to resolve, must not be null
     * @param <T> the type of the bean to resolve
     * @return the resolved bean instance, never null
     * @throws IllegalStateException if no bean could be found
     * @throws NullPointerException if beanClass is null
     */
    public static <T> T resolveRequiredBean(final Class<T> beanClass) {
        return resolveBeanOrThrowIllegalStateException(beanClass, null);
    }

    /**
     * Resolves bean types matching the given criteria.
     *
     * @param beanManager the bean manager to use for lookup, must not be null
     * @param beanClass the type of bean to resolve, must not be null
     * @param annotationClass optional qualifier annotation class, may be null
     * @param <T> the type of the bean to resolve
     * @param <V> the type of the qualifier annotation
     * @return set of matching bean types, may be empty but never null
     * @throws NullPointerException if beanManager or beanClass is null
     */
    @SuppressWarnings("squid:S1452") // owolff: Not able to avoid the wildcard call here
    public static <T, V extends Annotation> Set<Bean<?>> resolveBeanTypes(final BeanManager beanManager,
            final Class<T> beanClass, final Class<V> annotationClass) {
        Set<Bean<?>> beanTypes;
        if (null == annotationClass) {
            beanTypes = beanManager.getBeans(beanClass);
        } else {
            beanTypes = beanManager.getBeans(beanClass, AnnotationInstanceProvider.of(annotationClass));
        }
        return beanTypes;
    }

    /**
     * Helper method that checks is exactly one bean-type was found.
     *
     * @param beanClass the class of the bean to be checked
     * @param annotationClass the class of the annotation to be checked
     * @param beanTypes the set of beans found by the CDI container
     * @param <T> the type of the bean
     * @param <V> the type of the qualifier annotation
     * @throws IllegalStateException if no bean or multiple beans were found
     */
    private static <T, V extends Annotation> void checkBeanTypesFound(final Class<T> beanClass,
            final Class<V> annotationClass, final Set<Bean<?>> beanTypes) {
        if (beanTypes.isEmpty())
            throw new IllegalArgumentException(createErrorMessage(beanClass, annotationClass));
    }

    /**
     * Creates an error message for bean resolution failures.
     *
     * @param beanClass the class of the bean that could not be resolved
     * @param annotationClass the class of the qualifier annotation, may be null
     * @param <T> the type of the bean
     * @param <V> the type of the qualifier annotation
     * @return formatted error message
     */
    private static <T, V extends Annotation> String createErrorMessage(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return "No bean of type " + beanClass + " and annotation "
                + (null != annotationClass ? annotationClass.getName() : "(null)") + " could be found";
    }

    /**
     * Use this method if {@link #resolveBean(Class, Class)} returned an
     * {@link Optional#empty()} and you want to create a Portal log message string.
     *
     * @param beanClass       tried to resolve
     * @param annotationClass tried to resolve
     * @param <T>
     * @param <V>
     * @return Portal-532 log message
     * @deprecated Must not be used from outside
     */
    @Deprecated(since = "1.2")
    public static <T, V extends Annotation> String createLogMessage(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return "Portal-532: " + createErrorMessage(beanClass, annotationClass);
    }

    /**
     * Returns the CDI {@link BeanManager}.
     *
     * @return The CDI {@link BeanManager}.
     * @see CDI#getBeanManager()
     */
    private static BeanManager getBeanManager() {
        return CDI.current().getBeanManager();
    }
}
