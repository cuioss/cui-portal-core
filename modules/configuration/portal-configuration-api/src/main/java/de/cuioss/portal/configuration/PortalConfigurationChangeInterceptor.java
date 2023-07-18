package de.cuioss.portal.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.util.Nonbinding;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;

/**
 * <p>
 * Filters {@link Observes} methods on {@link PortalConfigurationChangeEvent}s
 * and proceeds according to the given attributes. In order to work it needs
 * either {@link #key()} or {@link #keyPrefix()} as configuration, otherwise it
 * will fail.
 * </p>
 * <p>
 * The annotated method will be called if the corresponding map contains at
 * least one key derived by {@link #key()} of if a key starts with at least one
 * of {@link #keyPrefix()}
 * </p>
 * <p>
 * This interceptor is meant as an convenience method, and therefore optional
 * </p>
 * <p>
 * <em>Caution: </em> Because it uses an {@link Interceptor} it will not work on
 * {@link Dependent} scoped beans.
 * </p>
 *
 * @author Oliver Wolff
 */
@InterceptorBinding
@Inherited
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PortalConfigurationChangeInterceptor {

    /**
     * @return one or more keys that are used for filtering the given properties.
     *         The keys must match using {@link String#equals(Object)}
     */
    @Nonbinding
    String[] key() default {};

    /**
     * @return one or more keys that are used for filtering the given properties but
     *         are interpreted using {@link String#startsWith(String)} in order to
     *         match.
     */
    @Nonbinding
    String[] keyPrefix() default {};
}
