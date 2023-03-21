package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.cuioss.portal.configuration.cache.CacheConfig;

/**
 * Injects a group config properties that will result in an instance of {@link CacheConfig} to be
 * injected. The properties are expected using the form:
 * <ul>
 * <li>'some.prefix' as {@link #name()}</li>
 * <li>'some.prefix.expiration' will be set to {@link CacheConfig#getExpiration()}</li>
 * <li>'some.prefix.expiration_unit' will be set to {@link CacheConfig#getTimeUnit()}: It will be
 * mapped to one of {@link TimeUnit#DAYS}, {@link TimeUnit#HOURS}, {@link TimeUnit#MINUTES},
 * {@link TimeUnit#SECONDS}, {@link TimeUnit#MICROSECONDS}, {@link TimeUnit#MILLISECONDS}</li>
 * <li>'some.prefix.size' will be set to {@link CacheConfig#getSize()}</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsCacheConfig {

    /**
     * @return the name (prefix) of the cache configuration
     */
    @Nonbinding
    String name();

    /**
     * @return the default expiration, to be used if no specific configuration is
     *         set, see {@link CacheConfig#getExpiration()}. Defaults to '0'
     */
    @Nonbinding
    long defaultExpiration() default 0;

    /**
     * @return the default Size, to be used if no specific configuration is
     *         set, see {@link CacheConfig#getSize()}. Defaults to '0'
     */
    @Nonbinding
    long defaultSize() default 0;

    /**
     * @return the default TimeUnit, to be used if no specific configuration is
     *         set, see {@link CacheConfig#getTimeUnit()}. Defaults to {@link TimeUnit#MINUTES}
     */
    @Nonbinding
    TimeUnit defaultTimeUnit() default TimeUnit.MINUTES;

    /**
     * @return true|false, if cache metrics should be recorded.
     */
    @Nonbinding
    boolean recordStatistics() default true;
}
