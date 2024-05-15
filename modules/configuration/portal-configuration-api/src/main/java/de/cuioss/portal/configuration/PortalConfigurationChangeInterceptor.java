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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InterceptorBinding;

/**
 * <p>
 * Filters {@link Observes} methods on {@link PortalConfigurationChangeEvent}s
 * and proceeds according to the given attributes. In order to work it needs
 * either {@link #key()} or {@link #keyPrefix()} as configuration, otherwise it
 * will fail.
 * </p>
 * <p>
 * The annotated method will be called if the corresponding map contains at
 * least one key derived by {@link #key()} of if a key starts with at least one
 * of {@link #keyPrefix()}
 * </p>
 * <p>
 * This interceptor is meant as an convenience method, and therefore optional
 * </p>
 * <p>
 * <em>Caution: </em> Because it uses an {@link Interceptor} it will not work on
 * {@link Dependent} scoped beans.
 * </p>
 *
 * @author Oliver Wolff
 */
@InterceptorBinding
@Inherited
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PortalConfigurationChangeInterceptor {

    /**
     * @return one or more keys that are used for filtering the given properties.
     *         The keys must match using {@link String#equals(Object)}
     */
    @Nonbinding
    String[] key() default {};

    /**
     * @return one or more keys that are used for filtering the given properties but
     *         are interpreted using {@link String#startsWith(String)} in order to
     *         match.
     */
    @Nonbinding
    String[] keyPrefix() default {};
}
