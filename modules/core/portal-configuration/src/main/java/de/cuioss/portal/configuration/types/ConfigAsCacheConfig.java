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
package de.cuioss.portal.configuration.types;

import de.cuioss.portal.configuration.cache.CacheConfig;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for injecting cache configuration properties as a {@link CacheConfig} object.
 * This qualifier provides type-safe injection of cache settings with support for
 * expiration, size limits, and time units.
 * <p>
 * Features:
 * <ul>
 *   <li>Configurable cache expiration</li>
 *   <li>Size-based eviction policy</li>
 *   <li>Flexible time unit support</li>
 *   <li>Optional statistics recording</li>
 *   <li>Default value fallbacks</li>
 * </ul>
 * <p>
 * Configuration Properties:
 * Given a prefix 'cache.users':
 * <ul>
 *   <li>{@code cache.users.expiration} - Cache entry lifetime</li>
 *   <li>{@code cache.users.expiration_unit} - Time unit (DAYS, HOURS, etc.)</li>
 *   <li>{@code cache.users.size} - Maximum number of entries</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Basic usage with defaults
 * &#64;Inject
 * &#64;ConfigAsCacheConfig(name = "cache.users")
 * private CacheConfig userCacheConfig;
 * 
 * // Custom defaults and statistics
 * &#64;Inject
 * &#64;ConfigAsCacheConfig(
 *     name = "cache.documents",
 *     defaultExpiration = 30,
 *     defaultTimeUnit = TimeUnit.MINUTES,
 *     defaultSize = 1000,
 *     recordStatistics = true
 * )
 * private CacheConfig documentCacheConfig;
 * </pre>
 * <p>
 * Example configuration:
 * <pre>
 * # User cache configuration
 * cache.users.expiration=60
 * cache.users.expiration_unit=MINUTES
 * cache.users.size=500
 * 
 * # Document cache configuration
 * cache.documents.expiration=24
 * cache.documents.expiration_unit=HOURS
 * cache.documents.size=2000
 * </pre>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ConfigAsCacheConfig {

    /**
     * The prefix for cache configuration properties.
     * <p>
     * This prefix will be used to locate the following properties:
     * <ul>
     *   <li>{prefix}.expiration - Cache entry lifetime</li>
     *   <li>{prefix}.expiration_unit - Time unit</li>
     *   <li>{prefix}.size - Maximum cache size</li>
     * </ul>
     *
     * @return the configuration prefix
     */
    @Nonbinding
    String name();

    /**
     * Default expiration time for cache entries.
     * <p>
     * This value is used when no explicit expiration is configured via properties.
     * The time unit for this value is specified by {@link #defaultTimeUnit()}.
     * <p>
     * A value of 0 indicates no expiration (entries remain until evicted by size).
     *
     * @return the default expiration time
     */
    @Nonbinding
    long defaultExpiration() default 0;

    /**
     * Default maximum number of entries in the cache.
     * <p>
     * This value is used when no explicit size is configured via properties.
     * When the cache reaches this size, entries will be evicted based on the
     * cache's eviction policy.
     * <p>
     * A value of 0 indicates no size limit.
     *
     * @return the default maximum cache size
     */
    @Nonbinding
    long defaultSize() default 0;

    /**
     * Default time unit for cache entry expiration.
     * <p>
     * This unit applies to the {@link #defaultExpiration()} value and is used
     * when no explicit time unit is configured via properties.
     * <p>
     * Supported units:
     * <ul>
     *   <li>{@link TimeUnit#DAYS}</li>
     *   <li>{@link TimeUnit#HOURS}</li>
     *   <li>{@link TimeUnit#MINUTES}</li>
     *   <li>{@link TimeUnit#SECONDS}</li>
     *   <li>{@link TimeUnit#MILLISECONDS}</li>
     *   <li>{@link TimeUnit#MICROSECONDS}</li>
     * </ul>
     *
     * @return the default time unit, defaults to {@link TimeUnit#MINUTES}
     */
    @Nonbinding
    TimeUnit defaultTimeUnit() default TimeUnit.MINUTES;

    /**
     * Controls whether cache statistics should be recorded.
     * <p>
     * When enabled, the cache will track metrics such as:
     * <ul>
     *   <li>Hit/miss ratios</li>
     *   <li>Eviction counts</li>
     *   <li>Average get/put times</li>
     * </ul>
     * <p>
     * Note that enabling statistics may have a small performance impact.
     *
     * @return {@code true} to enable statistics, {@code false} to disable
     */
    @Nonbinding
    boolean recordStatistics() default true;
}
