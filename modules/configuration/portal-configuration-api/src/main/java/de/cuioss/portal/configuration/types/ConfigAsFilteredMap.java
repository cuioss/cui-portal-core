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
package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Injects a number of config-properties as an immutable {@link Map} of
 * properties. It filters the property-keys using {@link #startsWith()}
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsFilteredMap {

    /**
     * @return The name to be filtered using {@link String#startsWith(String)}
     */
    @Nonbinding
    String startsWith();

    /**
     * @return boolean indicating whether the keys in the resulting map should be
     *         stripped of the prefix defined at {@link #startsWith()}. Defaults to
     *         {@code false}
     */
    @Nonbinding
    boolean stripPrefix() default false;

}
