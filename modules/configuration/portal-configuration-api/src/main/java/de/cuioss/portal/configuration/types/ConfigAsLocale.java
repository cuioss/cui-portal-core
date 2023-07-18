package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Injects a config property as a {@link Locale}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsLocale {

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
}
