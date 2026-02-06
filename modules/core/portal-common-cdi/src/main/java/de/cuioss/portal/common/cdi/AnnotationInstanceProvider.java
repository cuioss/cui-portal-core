/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.common.cdi;

import de.cuioss.tools.logging.CuiLogger;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for creating runtime instances of annotations using dynamic proxies.
 * 
 * <h2>Overview</h2>
 * This class enables the creation of annotation instances at runtime, particularly
 * useful when working with CDI's {@link jakarta.enterprise.inject.spi.AnnotatedType}
 * and when annotations need to be configured from external sources like properties
 * or XML files.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Creates annotation instances using Java's {@link Proxy} mechanism</li>
 *   <li>Supports default values for annotation members</li>
 *   <li>Allows custom member value overrides</li>
 *   <li>Thread-safe implementation</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>
 * // Create with default values
 * Priority priority = AnnotationInstanceProvider.of(Priority.class);
 * 
 * // Create with custom values
 * Map&lt;String, Object&gt; values = new HashMap&lt;&gt;();
 * values.put("value", 100);
 * Priority customPriority = AnnotationInstanceProvider.of(Priority.class, values);
 * 
 * // Load annotation class dynamically
 * String className = "jakarta.annotation.Priority";
 * Class&lt;? extends Annotation&gt; annotationClass = 
 *     (Class&lt;? extends Annotation&gt;) Class.forName(className);
 * Annotation annotation = AnnotationInstanceProvider.of(annotationClass);
 * </pre>
 * 
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>All annotation methods are properly implemented (equals, hashCode, toString)</li>
 *   <li>Member values are immutable once set</li>
 *   <li>Null values are not allowed for annotation members</li>
 * </ul>
 * 
 * @author Apache DeltaSpike Team
 * @see jakarta.enterprise.util.AnnotationLiteral
 * @see java.lang.reflect.Proxy
 */
public class AnnotationInstanceProvider implements Annotation, InvocationHandler, Serializable {
    @Serial
    private static final long serialVersionUID = -2345068201195886173L;

    private static final CuiLogger LOGGER = new CuiLogger(AnnotationInstanceProvider.class);
    private static final Object[] EMPTY_OBJECT_ARRAY = {};
    @SuppressWarnings("rawtypes") // owolff: Original Code
    private static final Class[] EMPTY_CLASS_ARRAY = {};

    private final Class<? extends Annotation> annotationClass;
    @SuppressWarnings("java:S1948") // owolff: Original Code
    private final Map<String, ?> memberValues;

    /**
     * Required to use the result of the factory instead of a default implementation
     * of {@link jakarta.enterprise.util.AnnotationLiteral}.
     *
     * @param annotationClass class of the target annotation
     */
    private AnnotationInstanceProvider(Class<? extends Annotation> annotationClass, Map<String, ?> memberValues) {
        this.annotationClass = annotationClass;
        this.memberValues = memberValues;
    }

    /**
     * Creates an annotation instance for the given annotation class.
     *
     * @param annotationClass type of the target annotation, must not be null
     * @param values         map of member values, must not be null, keys are member names
     * @param <T>           the annotation type
     * @return annotation instance for the given type
     * @throws IllegalArgumentException if values is null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T of(Class<T> annotationClass, Map<String, ?> values) {
        if (values == null) {
            throw new IllegalArgumentException("Map of values must not be null");
        }
        LOGGER.debug("Creating annotation instance for class '%s'", annotationClass.getName());
        LOGGER.trace("Annotation values: %s", values);
        return (T) initAnnotation(annotationClass, values);
    }

    /**
     * Creates an annotation instance for the given annotation class using default values.
     *
     * @param annotationClass type of the target annotation, must not be null
     * @param <T>           the annotation type
     * @return annotation instance for the given type
     */
    public static <T extends Annotation> T of(Class<T> annotationClass) {
        LOGGER.debug("Creating annotation instance for class '%s' with default values",
                annotationClass.getName());
        return of(annotationClass, Collections.emptyMap());
    }

    /**
     * Creates a proxy instance for the given annotation class.
     *
     * @param annotationClass type of the target annotation, must not be null
     * @param values         map of member values, must not be null
     * @param <T>           the annotation type
     * @return proxy instance for the given annotation type
     */
    private static synchronized <T extends Annotation> Annotation initAnnotation(Class<T> annotationClass,
            Map<String, ?> values) {
        return (Annotation) Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass},
                new AnnotationInstanceProvider(annotationClass, values));
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the method is not supported or member values are invalid
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        LOGGER.trace("Invoking method '%s' on annotation proxy", method.getName());
        switch (method.getName()) {
            case "hashCode" -> {
                return hashCode();
            }
            case "equals" -> {
                if (Proxy.isProxyClass(args[0].getClass())
                        && Proxy.getInvocationHandler(args[0]) instanceof AnnotationInstanceProvider) {
                    return equals(Proxy.getInvocationHandler(args[0]));

                }
                return equals(args[0]);
            }
            case "annotationType" -> {
                return annotationType();
            }
            case "toString" -> {
                return toString();
            }
            default -> LOGGER.trace("Handling member access for method %s", method);
        }
        if (memberValues.containsKey(method.getName())) {
            Object value = memberValues.get(method.getName());
            LOGGER.trace("Returning member value for method '%s': %s", method.getName(), value);
            return value;
        }
        Object defaultValue = method.getDefaultValue();
        LOGGER.trace("Returning default value for method '%s': %s", method.getName(), defaultValue);
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationClass;
    }

    /**
     * Returns a string representation of this annotation instance.
     * Format follows the standard annotation string representation:
     * "@AnnotationType(member1=value1, member2=value2)".
     *
     * @return string representation of the annotation
     * @throws RuntimeException if member value access fails
     */
    @Override
    public String toString() {
        var methods = annotationClass.getDeclaredMethods();

        var sb = new StringBuilder("@" + annotationType().getName() + "(");
        var length = methods.length;

        for (var i = 0; i < length; i++) {
            // Member name
            sb.append(methods[i].getName()).append("=");

            // Member value
            Object memberValue;
            try {
                memberValue = invoke(this, methods[i], EMPTY_OBJECT_ARRAY);
            } catch (RuntimeException e) { // cui-rewrite:disable InvalidExceptionUsageRecipe
                memberValue = "";
            }
            sb.append(memberValue);

            if (i < length - 1) {
                sb.append(",");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnnotationInstanceProvider that)) {
            if (annotationClass.isInstance(o)) {
                for (Map.Entry<String, ?> entry : memberValues.entrySet()) {
                    try {
                        var oValue = annotationClass.getMethod(entry.getKey(), EMPTY_CLASS_ARRAY).invoke(o,
                                EMPTY_OBJECT_ARRAY);
                        if (oValue == null || entry.getValue() == null || !oValue.equals(entry.getValue())) {
                            return false;
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return true;
            }
            return false;
        }

        if (!annotationClass.equals(that.annotationClass)) {
            return false;
        }

        return memberValues.equals(that.memberValues);
    }

    @Override
    public int hashCode() {
        var result = 0;
        for (Method m : annotationClass.getDeclaredMethods()) {
            try {
                var value = invoke(this, m, EMPTY_OBJECT_ARRAY);
                if (value == null) {
                    return 0;
                }
                result += Objects.hash(m.getName(), value);
            } catch (RuntimeException ex) { // cui-rewrite:disable InvalidExceptionUsageRecipe
                throw new IllegalStateException(ex);
            }
        }
        return result;
    }

}