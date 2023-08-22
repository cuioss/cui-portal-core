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
import java.util.List;
import java.util.Locale;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.cuioss.portal.configuration.PortalConfigurationKeys;

/**
 * Injects a config property as a {@link List} of {@link Locale}. The
 * corresponding producer may return an empty {@link List}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsLocaleList {

    /**
     * @return the name of the property
     */
    @Nonbinding
    String name();

    /**
     * @return boolean indicating whether the producer should return the
     *         system-locale in case the given locale can not be parsed
     *         '{@code true}' or throw an {@link IllegalArgumentException}
     *         {@code false}. Defaults to {@code true}
     */
    @Nonbinding
    boolean defaultToSystem() default true;

    /**
     * @return the separator char to separate the individual Locale elements,
     *         defaults to {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    @Nonbinding
    char separator() default PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;
}
