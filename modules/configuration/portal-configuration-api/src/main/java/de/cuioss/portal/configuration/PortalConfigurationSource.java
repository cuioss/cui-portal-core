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
 * Marker identifying concrete instances of {@link ConfigurationSource} and/or
 * {@link org.eclipse.microprofile.config.spi.ConfigSource}.
 *
 * @author Oliver Wolff
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface PortalConfigurationSource {

}
