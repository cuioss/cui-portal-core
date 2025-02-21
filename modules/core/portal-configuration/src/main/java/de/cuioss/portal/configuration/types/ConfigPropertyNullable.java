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

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CDI qualifier for injecting optional configuration properties that may be null.
 * This qualifier provides a way to handle missing configuration values without
 * throwing exceptions.
 * <p>
 * Features:
 * <ul>
 *   <li>Null-safe configuration injection</li>
 *   <li>Optional default value support</li>
 *   <li>Empty string to null conversion</li>
 *   <li>Support for primitive and wrapper types</li>
 * </ul>
 * <p>
 * <h2>Usage Guidelines</h2>
 * <p>
 * 1. Recommended for:
 * <ul>
 *   <li>Passivation-capable beans (e.g., {@link jakarta.enterprise.context.SessionScoped})</li>
 *   <li>Beans implementing {@link java.io.Serializable}</li>
 *   <li>Optional configuration values that may not be present</li>
 * </ul>
 * <p>
 * 2. Not recommended for:
 * <ul>
 *   <li>{@link jakarta.enterprise.context.ApplicationScoped} beans (use {@link java.util.Optional} instead)</li>
 *   <li>Collection types (has no effect)</li>
 *   <li>Required configuration values</li>
 * </ul>
 * <p>
 * <h2>Supported Types</h2>
 * <ul>
 *   <li>Primitive types (int, long, boolean, etc.)</li>
 *   <li>Wrapper types ({@link Integer}, {@link Long}, etc.)</li>
 *   <li>{@link String} values</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage - returns null if not configured
 * &#64;Inject
 * &#64;ConfigPropertyNullable(name = "app.optional.setting")
 * private String optionalSetting;
 *
 * // With default value
 * &#64;Inject
 * &#64;ConfigPropertyNullable(
 *     name = "app.timeout",
 *     defaultValue = "30"
 * )
 * private Integer timeout;
 * </pre>
 * <p>
 * Null Handling:
 * <ul>
 *   <li>Returns null if configuration key is missing</li>
 *   <li>Returns null if value resolves to empty string</li>
 *   <li>Returns defaultValue if specified (or null if defaultValue is empty)</li>
 * </ul>
 *
 * @author Sven Haag
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface ConfigPropertyNullable {

    /**
     * The configuration property key to inject.
     * <p>
     * The value associated with this key will be injected into the annotated
     * field.
     * If the key is not found and no default value is specified,
     * null will be injected instead of throwing an exception.
     *
     * @return the configuration key
     */
    @Nonbinding
    String name();

    /**
     * Optional default value to use when the configuration key is not found.
     * <p>
     * Special handling:
     * <ul>
     *   <li>Empty string ("") will be converted to null</li>
     *   <li>For numeric types, the string must be parseable to the target type</li>
     *   <li>For boolean, accepts "true"/"false" (case-insensitive)</li>
     * </ul>
     *
     * @return the default value string, empty string if none
     */
    @Nonbinding
    String defaultValue() default "";
}
