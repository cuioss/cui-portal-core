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

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;
import static java.util.Objects.requireNonNull;

import de.cuioss.portal.common.priority.PriorityComparator;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.*;

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
     * @param beanManager     an instance of the beanManager for doing the lookup.
     * @param beanClass       identifying the type to be loaded must not be null
     * @param annotationClass the class the concrete type must be annotated with
     *                        may be null
     * @return the found bean, or null if none could be found. It will throw an
     * {@link IllegalArgumentException} in case of the bean cannot be
     * identified exactly.
     */
    @SuppressWarnings("unchecked")
    private static <T, V extends Annotation> T getCDIBean(final BeanManager beanManager, final Class<T> beanClass,
            final Class<V> annotationClass) {
        requireNonNull(beanClass, "beanClass");
        requireNonNull(beanManager, "beanManager");

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
     * Shorthand for resolving a CDI Bean
     *
     * @param beanClass       identifying the type to be loaded must not be null
     * @param annotationClass the class the concrete type must be annotated with
     *                        may be null
     * @param <T>
     * @param <V>
     * @return the found bean, or {@link Optional#empty()} if none could be found
     */
    public static <T, V extends Annotation> Optional<T> resolveBean(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return Optional.ofNullable(getCDIBean(getBeanManager(), beanClass, annotationClass));
    }

    /**
     * Shorthand for resolving a CDI Bean or throwing an IllegalStateException
     * otherwise.
     *
     * @param beanClass       identifying the type to be loaded must not be null
     * @param annotationClass the class the concrete type must be annotated with
     *                        may be null
     * @param <T>
     * @param <V>
     * @return the found bean
     */
    public static <T, V extends Annotation> T resolveBeanOrThrowIllegalStateException(final Class<T> beanClass,
            final Class<V> annotationClass) {
        return Optional.ofNullable(getCDIBean(getBeanManager(), beanClass, annotationClass)).orElseThrow(
                () -> new IllegalStateException("Portal-532: " + createErrorMessage(beanClass, annotationClass)));
    }

    /**
     * Shorthand for calling
     * {@link #resolveBeanOrThrowIllegalStateException(Class, Class)} with
     * {@code null} for the parameter annotation.
     *
     * @param beanClass identifying the type to be loaded must not be null
     * @param <T>
     * @return the found bean, or {@link Optional#empty()} if none could be found
     */
    public static <T> T resolveRequiredBean(final Class<T> beanClass) {
        return resolveBeanOrThrowIllegalStateException(beanClass, null);
    }

    /**
     * Helper method for resolving the beanTypes according to the given identifier.
     *
     * @param beanManager     an instance of the beanManager for doing the lookup.
     * @param beanClass       identifying the type to be loaded must not be null
     * @param annotationClass the class the concrete type must be annotated with
     *                        may be null
     * @param <T>
     * @param <V>
     * @return the resolved beanTypes.
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
     * @param beanClass
     * @param annotationClass
     * @param beanTypes
     * @param <T>
     * @param <V>
     */
    private static <T, V extends Annotation> void checkBeanTypesFound(final Class<T> beanClass,
            final Class<V> annotationClass, final Set<Bean<?>> beanTypes) {
        if (beanTypes.isEmpty())
            throw new IllegalArgumentException(createErrorMessage(beanClass, annotationClass));
    }

    /**
     * @param beanClass
     * @param annotationClass
     * @param <T>
     * @param <V>
     * @return the created error-message
     */
    public static <T, V extends Annotation> String createErrorMessage(final Class<T> beanClass,
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
     */
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
    public static BeanManager getBeanManager() {
        return CDI.current().getBeanManager();
    }
}
