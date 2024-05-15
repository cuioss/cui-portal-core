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
package de.cuioss.portal.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * <h1>Nullable ConfigProperty</h1>
 * <p>
 * Advises the configuration system to allow injection of {@code null} values,
 * instead of throwing a {@link java.util.NoSuchElementException}. A
 * configuration value will be {@code null}, if the configuration key is missing
 * or if its resolved value is an empty string.
 * </p>
 *
 * <h2>Valid Beans</h2>
 * <p>
 * The usage of this annotation is only encouraged for passivation capable beans
 * such as {@link jakarta.enterprise.context.SessionScoped} beans or any other
 * bean that must implement the {@link java.io.Serializable} interface. For
 * other beans, such as {@link jakarta.enterprise.context.ApplicationScoped}
 * beans, the usage of {@link java.util.Optional} is encouraged - but only if
 * you don't want an exception for a missing config property.
 * </p>
 *
 * <h2>Valid Data Types</h2>
 * <p>
 * Use this annotation for injections of primitive types, their corresponding
 * object like {@link Integer} and {@link String} values. Don't use it for
 * {@link java.util.Collection} types, as it will have no effect.
 * </p>
 *
 * @author Sven Haag
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface ConfigPropertyNullable {

    /**
     * @return config key
     */
    @Nonbinding
    String name();

    /**
     * @return a default value that is used, if no value is provided by any
     *         {@link org.eclipse.microprofile.config.spi.ConfigSource} for the
     *         given config key. An empty string will be converted to {@code null}!
     */
    @Nonbinding
    String defaultValue() default "";
}
