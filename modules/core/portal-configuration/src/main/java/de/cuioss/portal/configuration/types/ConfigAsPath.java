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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

/**
 * Injects a config property as a {@link Path}. In case the path is not there /
 * empty the corresponding producer will throw an
 * {@link IllegalArgumentException}. See {@link #failOnNotAccessible()} for
 * details regarding handling of not accessible files.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsPath {

    /**
     * @return the name of the property
     */
    @Nonbinding
    String name();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     *         {@link IllegalArgumentException} in case the file-path derived by the
     *         property is not accessible. Defaults to <code>true</code>.
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;
}
