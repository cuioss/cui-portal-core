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
public class AtnaConfigurationKeys {

    /** Base String for auditing related configuration: "integration.audit.". */
    public static final String AUDIT_BASE = PortalConfigurationKeys.INTEGRATION_BASE + "audit.";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.enabled'
     * <p>
     * Used for enabling / disabling the auditing. Defaults to {@code false}
     * </p>
     */
    public static final String PORTAL_AUDIT_ENABLED = AUDIT_BASE + PortalConfigurationKeys.ENABLED;

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.sourceId'
     * <p>
     * Identifies the sourceId for the audit-events. If it is not defined the
     * implementation will choose the context-path
     * </p>
     */
    public static final String PORTAL_AUDIT_SOURCE_ID = AUDIT_BASE + "sourceId";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.enterpriseSiteId'
     * <p>
     * Identifies the enterpriseSiteId for the audit-events. If it is not defined
     * the implementation will choose the context-path suffixed with
     * "-enterprise-id"
     * </p>
     */
    public static final String PORTAL_AUDIT_ENTERPRISE_SITE_ID = AUDIT_BASE + "enterpriseSiteId";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.repositoryHost'
     * <p>
     * Identifies the host of the audit-repository. Defaults to "localhost"
     * </p>
     */
    public static final String PORTAL_AUDIT_REPOSITORY_HOST = AUDIT_BASE + "repositoryHost";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.repositoryPort'
     * <p>
     * Identifies the port of the audit-repository. Defaults to "514"
     * </p>
     */
    public static final String PORTAL_AUDIT_REPOSITORY_PORT = AUDIT_BASE + "repositoryPort";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.sourceType'
     * <p>
     * Identifies the type of the audit-source. Defaults to "4" =
     * AuditSourceType.ApplicationServerProcess
     * </p>
     */
    public static final String PORTAL_AUDIT_SOURCE_TYPE = AUDIT_BASE + "sourceType";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.sendingApplication'
     * <p>
     * Identifies the sender of the audit-events. If it is not defined the
     * implementation will choose the context-path
     * </p>
     */
    public static final String PORTAL_AUDIT_SENDING_APPLICATION = AUDIT_BASE + "sendingApplication";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.repositoryTransport'
     * <p>
     * Identifies the transport-protocol of the audit-events. Defaults to UDP
     * </p>
     */
    public static final String PORTAL_AUDIT_REPOSITORY_TRANSPORT = AUDIT_BASE + "repositoryTransport";

    /**
     * Context parameter within configuration-subsystem with the name
     * 'integration.audit.includeParticipantsFromResponse'
     * <p>
     * Defines whether to include participant information to the audit-events.
     * Defaults to {@code false}
     * </p>
     */
    public static final String PORTAL_AUDIT_INCLUDE_PARTICIPANTS_FROM_RESPONSE = AUDIT_BASE
            + "includeParticipantsFromResponse";
}
