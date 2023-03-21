package de.cuioss.portal.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HealthCheckConfigKeys {

    private static final String PORTAL_HEALTHCHECK_BASE = PortalConfigurationKeys.PORTAL_BASE + "healthcheck.";

    private static final String PORTAL_HEALTHCHECK_DETAILS_BASE = PORTAL_HEALTHCHECK_BASE + "detailed.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.enabled'
     * <p>
     * Used to enable or disable the portals health check servlet.
     * Valid values are: true|false. Default is true
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_ENABLED = PORTAL_HEALTHCHECK_BASE + PortalConfigurationKeys.ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.detailed.requiresLoggedInUser'
     * <p>
     * Used to define whether detailed health-check information is only presented for a logged in
     * user.
     * Valid values are: true|false. Default is true
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_LOG_IN_REQUIRED =
        PORTAL_HEALTHCHECK_DETAILS_BASE + "requiresLoggedInUser";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.detailed.requiredRoles'
     * <p>
     * Used to define which roles are needed to view detailed health-check information.
     * Valid values are: Role-Names separated by a ',' . Default is 'Metrics-Collector'
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_ROLES_REQUIRED =
        PORTAL_HEALTHCHECK_DETAILS_BASE + "requiredRoles";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.httpCodeDown'
     * <p>
     * Used to define the HTTP response status code if one or more services are DOWN.
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_HTTPCODEDOWN = PORTAL_HEALTHCHECK_BASE + "httpCodeDown";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.customOutput'
     * <p>
     * Used to define the responded JSON format. If <code>true</code>, the output conforms to the
     * ICW style, which is based on Dropwizard. If <code>false</code>, the JAVA MicroProfile style
     * is used.
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_CUSTOMOUTPUT = PORTAL_HEALTHCHECK_BASE + "customOutput";
}
