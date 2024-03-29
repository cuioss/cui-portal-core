/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * Used to enable or disable the portals health check servlet. Valid values are:
     * true|false. Default is true
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_ENABLED = PORTAL_HEALTHCHECK_BASE + PortalConfigurationKeys.ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.detailed.requiresLoggedInUser'
     * <p>
     * Used to define whether detailed health-check information is only presented
     * for a logged in user. Valid values are: true|false. Default is true
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_LOG_IN_REQUIRED = PORTAL_HEALTHCHECK_DETAILS_BASE
            + "requiresLoggedInUser";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.detailed.requiredRoles'
     * <p>
     * Used to define which roles are needed to view detailed health-check
     * information. Valid values are: Role-Names separated by a ',' . Default is
     * 'Metrics-Collector'
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_ROLES_REQUIRED = PORTAL_HEALTHCHECK_DETAILS_BASE + "requiredRoles";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.healthcheck.httpCodeDown'
     * <p>
     * Used to define the HTTP response status code if one or more services are
     * DOWN.
     * </p>
     */
    public static final String PORTAL_HEALTHCHECK_HTTPCODEDOWN = PORTAL_HEALTHCHECK_BASE + "httpCodeDown";

}
