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
 * Injects a number of config-properties as an immutable {@link Map} of properties. It filters the
 * property-keys using {@link #startsWith()}
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
     * @return boolean indicating whether the keys in the resulting map should be stripped of the
     *         prefix defined at {@link #startsWith()}. Defaults to {@code false}
     */
    @Nonbinding
    boolean stripPrefix() default false;

}
