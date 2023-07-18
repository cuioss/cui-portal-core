package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.cuioss.tools.io.FileLoader;

/**
 * Injects a config property as a {@link FileLoader}. In case the path is not
 * there / empty the corresponding producer will throw an
 * {@link IllegalArgumentException}. See {@link #failOnNotAccessible()} for
 * details regarding handling of not accessible files.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsFileLoader {

    /**
     * @return the name of the property
     */
    @Nonbinding
    String name();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     *         {@link IllegalArgumentException} in case the file-path derived by the
     *         property is not accessible. Defaults to {@code true}.
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;
}
