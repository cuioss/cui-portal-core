package de.cuioss.portal.configuration;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Defines events that will be fired on a runtime change in the configuration. The payload is the
 * delta of the configuration, not the complete configuration. If you want to filter the calls you
 * can annotate the listener method with {@link PortalConfigurationChangeInterceptor}
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface PortalConfigurationChangeEvent {

}
