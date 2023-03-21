package de.cuioss.portal.configuration.types;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Injects a config property as a {@link Path}. In case the path is not there / empty the
 * corresponding producer will throw an {@link IllegalArgumentException}. See
 * {@link #failOnNotAccessible()} for details regarding handling of not accessible files.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ConfigAsPath {

    /**
     * @return the name of the property
     */
    @Nonbinding
    String name();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     *         {@link IllegalArgumentException} in case the file-path derived by the property is not
     *         accessible. Defaults to <code>true</code>.
     */
    @Nonbinding
    boolean failOnNotAccessible() default true;
}
