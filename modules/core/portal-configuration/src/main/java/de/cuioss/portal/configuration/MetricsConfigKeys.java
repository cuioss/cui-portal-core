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

import static de.cuioss.portal.configuration.PortalConfigurationKeys.ENABLED;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_BASE;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetricsConfigKeys {

    private static final String PORTAL_METRICS_BASE = PORTAL_BASE + "metrics.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.metrics.enabled'
     * <p>
     * Used to enable or disable the portals metrics servlet. Valid values are:
     * true|false. Default is true
     * </p>
     */
    public static final String PORTAL_METRICS_ENABLED = PORTAL_METRICS_BASE + ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.metrics.requiresLoggedInUser'
     * <p>
     * Used to define whether the metrics are only presented for a logged in user.
     * Valid values are: true|false. Default is true
     * </p>
     */
    public static final String PORTAL_METRICS_LOG_IN_REQUIRED = PORTAL_METRICS_BASE + "requiresLoggedInUser";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'portal.metrics.requiredRoles'
     * <p>
     * Used to define which roles are needed to metrics information. Valid values
     * are: Role-Names separated by a ',' . Default is 'Metrics-Collector'
     * </p>
     */
    public static final String PORTAL_METRICS_ROLES_REQUIRED = PORTAL_METRICS_BASE + "requiredRoles";

    /**
     * Context parameter within configuration-subsystem with the name:
     * 'portal.metrics.micrometerCompatibility'
     * <p>
     * Used to define if the export format contains MicroProfile or Micrometer
     * compatible metric names. Valid values are: true|false
     * </p>
     */
    public static final String PORTAL_METRICS_MICROMETER_COMPATIBILITY = PORTAL_METRICS_BASE
            + "micrometerCompatibility";

    /**
     * Context parameter within configuration-subsystem with the name:
     * 'portal.metrics.tomcat.enabled'
     * <p>
     * Used to define, if Tomcat specific metrics are provided. Valid values are:
     * true|false
     * </p>
     */
    public static final String PORTAL_METRICS_TOMCAT_ENABLED = PORTAL_METRICS_BASE + "tomcat." + ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name:
     * 'portal.metrics.os.sun.enabled'
     * <p>
     * Used to define, if Sun specific (com.sun.management.OperatingSystemMXBean)
     * metrics should be provided, if possible. Valid values are: true|false
     * </p>
     */
    public static final String PORTAL_METRICS_OS_SUN_ENABLED = PORTAL_METRICS_BASE + "os.sun." + ENABLED;

    public static final String PORTAL_METRICS_APP_NAME = PORTAL_METRICS_BASE + "appName";
    public static final String MP_METRICS_APP_NAME = "mp.metrics.appName";
}
