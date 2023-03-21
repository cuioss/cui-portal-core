package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.cuioss.portal.configuration.PortalConfigurationKeys;

/**
 * Injects a config property as an immutable {@link Set} of trimmed Strings. In case the the
 * property is
 * null or empty and there is no {@link #defaultValue()} set it will be an empty {@link Set}.
 * The default splitting character is {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsSet {

    /**
     * @return the name of the property
     */
    @Nonbinding
    String name();

    /**
     * @return the separator char, defaults to
     *         {@value PortalConfigurationKeys#CONTEXT_PARAM_SEPARATOR}
     */
    @Nonbinding
    char separator() default PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;

    /**
     * @return the String representation of the default value.
     */
    @Nonbinding
    String defaultValue() default "";
}
