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
package de.cuioss.portal.configuration.impl.producer;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.portal.configuration.application.PortalProjectStage;
import de.cuioss.portal.configuration.application.PortalProjectStageProducer;
import de.cuioss.portal.configuration.application.ProjectStage;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.CuiProjectStage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Portal variant of {@link CuiProjectStage}. It computes the project-stage from
 * the parameter {@link PortalConfigurationKeys#PORTAL_STAGE}
 * <p>
 * Currently it supports the following stages:
 * <ul>
 * <li>{@link ProjectStage#DEVELOPMENT}</li>
 * <li>{@link ProjectStage#TEST}</li>
 * <li>{@link ProjectStage#CONFIGURATION}</li>
 * <li>{@link ProjectStage#PRODUCTION}</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @author Sven Haag
 */
@ApplicationScoped
@EqualsAndHashCode(of = "projectStage", doNotUseGetters = true)
@ToString(of = "projectStage", doNotUseGetters = true)
public class PortalProjectStageImpl implements PortalProjectStage, Serializable {

    private static final long serialVersionUID = 178765109802042493L;

    private static final CuiLogger LOGGER = new CuiLogger(PortalProjectStageImpl.class);

    private static final String PROJECT_STAGE_XY_DETECTED = """
            Portal-101: Project stage '{}' detected. \
            Set the property '\
            """ + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.";

    private static final String PROJECT_STAGE_CONFIGURATION_DETECTED = """
            Portal-103: Project stage 'configuration' detected. Complete the configuration wizard or \
            set the property 'icw.portal.configuration.stage' to 'production' for productive usage.\
            """;

    private static final String PROJECT_STAGE_PRODUCTION_DETECTED = "Portal-001: Running in Production-Mode";

    /**
     * Bean name for looking up instances.
     */
    public static final String BEAN_NAME = "cuiProjectStage";

    @Produces
    @PortalProjectStageProducer
    @Named(BEAN_NAME)
    @ApplicationScoped
    @Getter
    private CuiProjectStage projectStage = ProjectStage.PRODUCTION;

    @Inject
    @ConfigProperty(name = PortalConfigurationKeys.PORTAL_STAGE)
    Provider<String> portalStageConfigurationProvider;

    /**
     * Initializes the bean. See class documentation for expected result.
     */
    @PostConstruct
    void initialize() {
        initBean();
    }

    private void initBean() {
        final var portalProjectStage = ProjectStage.fromString(portalStageConfigurationProvider.get());
        projectStage = portalProjectStage;

        switch (portalProjectStage) {
        case DEVELOPMENT:
            LOGGER.warn(PROJECT_STAGE_XY_DETECTED, "development");
            break;
        case TEST:
            LOGGER.warn(PROJECT_STAGE_XY_DETECTED, "test");
            break;
        case CONFIGURATION:
            LOGGER.warn(PROJECT_STAGE_CONFIGURATION_DETECTED);
            break;
        case PRODUCTION:
        default:
            LOGGER.info(PROJECT_STAGE_PRODUCTION_DETECTED);
            break;
        }
    }

    /**
     * Listener for {@link PortalConfigurationChangeEvent}s. Reconfigures the
     * project-stage-configuration
     *
     * @param deltaMap
     */
    void portalConfigurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        if (deltaMap.containsKey(PortalConfigurationKeys.PORTAL_STAGE)) {
            LOGGER.debug("Change in portal stage configuration found, reconfigure");
            initBean();
        }
    }
}
