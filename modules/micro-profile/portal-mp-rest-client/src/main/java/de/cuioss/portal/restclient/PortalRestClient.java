package de.cuioss.portal.restclient;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;

/**
 * <p>
 * Marker identifying concrete instances of {@link RestClientHolder}. The connection-specific
 * metadata is
 * derived by a number of properties derived by {@link #baseName()}. Expected is a structure as
 * defined within {@link ConfigAsConnectionMetadata}.
 * </ p>
 */
@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface PortalRestClient {

    /**
     * @return the basename of the configuration, see {@link ConfigAsConnectionMetadata} for details
     */
    @Nonbinding
    String baseName();

    /**
     * @return boolean indicating whether the corresponding producer should throw an
     *         {@link IllegalArgumentException} in case the properties contain errors. Defaults to
     *         <code>true</code>. In case of <code>false</code> will return the created
     *         {@link RestClientHolder} without structural checks on the configuration
     */
    @Nonbinding
    boolean failOnInvalidConfiguration() default true;
}
