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
 * Copied from deltaspike core
 * <p>
 * A small helper class to create an Annotation instance of the given annotation
 * class via {@link Proxy}. The annotation literal gets filled
 * with the default values.
 * </p>
 * <p>
 * This class can be used to dynamically create Annotations which can be used in
 * AnnotatedTyp. This is e.g. the case if you configure an annotation via
 * properties or XML file. In those cases you cannot use
 * {@link jakarta.enterprise.util.AnnotationLiteral} because the type is not known
 * at compile time.
 * </p>
 * <p>
 * usage:
 * </p>
 *
 * <pre>
 * String annotationClassName = ...;
 * Class&lt;? extends annotation&gt; annotationClass =
 *     (Class&lt;? extends Annotation&gt;) ClassUtils.getClassLoader(null).loadClass(annotationClassName);
 * Annotation a = AnnotationInstanceProvider.of(annotationClass)
 * </pre>
 *
 * @author <a href="https://github.com/apache/deltaspike/blob/ds-1.9.2/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/util/metadata/AnnotationInstanceProvider.java">...</a>
 */
public class AnnotationInstanceProvider implements Annotation, InvocationHandler, Serializable {
    @Serial
    private static final long serialVersionUID = -2345068201195886173L;
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
     * Creates an annotation instance for the given annotation class
     *
     * @param annotationClass type of the target annotation
     * @param values          A non-null map of the member values, keys being the
     *                        name of the members
     * @param <T>             current type
     * @return annotation instance for the given type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T of(Class<T> annotationClass, Map<String, ?> values) {
        if (values == null) {
            throw new IllegalArgumentException("Map of values must not be null");
        }
        return (T) initAnnotation(annotationClass, values);
    }

    /**
     * Creates an annotation instance for the given annotation class
     *
     * @param annotationClass type of the target annotation
     * @param <T>             current type
     * @return annotation instance for the given type
     */
    public static <T extends Annotation> T of(Class<T> annotationClass) {
        return of(annotationClass, Collections.emptyMap());
    }

    private static synchronized <T extends Annotation> Annotation initAnnotation(Class<T> annotationClass,
                                                                                 Map<String, ?> values) {
        return (Annotation) Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass},
            new AnnotationInstanceProvider(annotationClass, values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
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
        }
        if (memberValues.containsKey(method.getName())) {
            return memberValues.get(method.getName());
        }
        return method.getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationClass;
    }

    /**
     * Copied from Apache OWB (javax.enterprise.util.AnnotationLiteral#toString())
     * with minor changes.
     *
     * @return the current state of the annotation as string
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
            } catch (Exception e) {
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
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
        return result;
    }

}