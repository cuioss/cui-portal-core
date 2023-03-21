package de.cuioss.portal.configuration.installationpaths;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Defines events that will be fired on a runtime change in the configuration. The payload is
 * {@link PortalInstallationPathChangedPayload}
 *
 * @author Matthias Walliczek
 * @deprecated since v7.3. Listen for {@link de.cuioss.portal.configuration.PortalConfigurationChangeEvent} instead,
 *     using the config keys {@link de.cuioss.portal.configuration.PortalConfigurationKeys#PORTAL_CUSTOMIZATION_DIR} or
 *     {@link de.cuioss.portal.configuration.PortalConfigurationKeys#PORTAL_CUSTOMIZATION_ENABLED}
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Deprecated
public @interface PortalInstallationPathChangedEvent {

}
