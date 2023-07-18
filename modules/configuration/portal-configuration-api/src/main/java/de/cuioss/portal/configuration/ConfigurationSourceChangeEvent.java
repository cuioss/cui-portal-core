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
 * Defines events that will be fired on a runtime change in the configuration.
 * While {@link PortalConfigurationChangeEvent} addresses the clients of the
 * configuration system in general, this event will be fired either by
 * individual {@link org.eclipse.microprofile.config.spi.ConfigSource}s or by a
 * {@link de.cuioss.portal.configuration.schedule.FileWatcherService}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface ConfigurationSourceChangeEvent {
}
