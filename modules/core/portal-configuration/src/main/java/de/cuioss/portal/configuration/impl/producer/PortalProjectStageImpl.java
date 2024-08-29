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

import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.portal.configuration.PortalConfigurationKeys;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.CuiProjectStage;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * Portal variant of {@link CuiProjectStage}. It computes the project-stage from
 * the parameter {@link PortalConfigurationKeys#PORTAL_STAGE}
 * <p>
 * Currently, it supports the following stages:
 * <ul>
 * <li>{@link ProjectStage#DEVELOPMENT}</li>
 * <li>{@link ProjectStage#TEST}</li>
 * <li>{@link ProjectStage#PRODUCTION}</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @author Sven Haag
 */
@ApplicationScoped
@EqualsAndHashCode(of = "projectStage", doNotUseGetters = true)
@ToString(of = "projectStage", doNotUseGetters = true)
public class PortalProjectStageImpl implements Serializable {

    @Serial
    private static final long serialVersionUID = 178765109802042493L;

    private static final CuiLogger LOGGER = new CuiLogger(PortalProjectStageImpl.class);

    private static final String PROJECT_STAGE_XY_DETECTED = """
        Portal-101: Project stage '{}' detected. \
        Set the property '\
        """ + PortalConfigurationKeys.PORTAL_STAGE + "' to 'production' for productive usage.";

    private static final String PROJECT_STAGE_PRODUCTION_DETECTED = "Portal-001: Running in Production-Mode";

    /**
     * Bean name for looking up instances.
     */
    public static final String BEAN_NAME = "cuiProjectStage";

    @Produces
    @Dependent
    @Getter(value = AccessLevel.PACKAGE)
    private CuiProjectStage projectStage = ProjectStage.PRODUCTION;

    @Inject
    @ConfigProperty(name = PortalConfigurationKeys.PORTAL_STAGE)
    Provider<String> portalStageConfigurationProvider;

    /**
     * Initializes the bean. See class documentation for an expected result.
     */
    @PostConstruct
    void initialize() {
        final var configuredProjectStage = ProjectStage.fromString(portalStageConfigurationProvider.get());
        LOGGER.debug("Read from configuration-system '%s'", configuredProjectStage);
        projectStage = configuredProjectStage;

        switch (configuredProjectStage) {
            case DEVELOPMENT:
                LOGGER.warn(PROJECT_STAGE_XY_DETECTED, "development");
                break;
            case TEST:
                LOGGER.warn(PROJECT_STAGE_XY_DETECTED, "test");
                break;
            case PRODUCTION:
            default:
                LOGGER.info(PROJECT_STAGE_PRODUCTION_DETECTED);
                break;
        }
    }
}
