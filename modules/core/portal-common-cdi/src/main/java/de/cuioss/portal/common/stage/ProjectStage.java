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
package de.cuioss.portal.common.stage;

import de.cuioss.portal.common.PortalCommonLogMessages;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.CuiProjectStage;

/**
 * Portal project stage representing the application's runtime environment.
 * Implements {@link CuiProjectStage} to provide standardized stage detection.
 *
 * <h2>Available Stages</h2>
 * <ul>
 *   <li>DEVELOPMENT - For development environments</li>
 *   <li>TEST - For testing environments</li>
 *   <li>PRODUCTION - For production environments</li>
 * </ul>
 *
 * @author Sven Haag
 */
public enum ProjectStage implements CuiProjectStage {

    /** Development stage for local development environments */
    DEVELOPMENT,

    /** Test stage for testing and QA environments */
    TEST,

    /** Production stage for live environments */
    PRODUCTION;

    private static final CuiLogger LOGGER = new CuiLogger(ProjectStage.class);

    /**
     * Resolves the corresponding portal project stage from a string value.
     * Case-insensitive matching is used.
     *
     * @param stage the string representation of the stage, may be null
     * @return the corresponding stage or PRODUCTION if stage is unknown or null
     */
    public static ProjectStage fromString(final String stage) {
        if (null == stage) {
            LOGGER.error(PortalCommonLogMessages.ERROR.INVALID_STAGE.format(stage));
            return ProjectStage.PRODUCTION;
        }

        for (ProjectStage ps : ProjectStage.values()) {
            if (ps.name().equalsIgnoreCase(stage)) {
                return ps;
            }
        }

        LOGGER.error(PortalCommonLogMessages.ERROR.INVALID_STAGE.format(stage));
        return ProjectStage.PRODUCTION;
    }

    /**
     * Returns the lowercase name of the project stage.
     *
     * @return lowercase string representation of the stage
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDevelopment() {
        return DEVELOPMENT == this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTest() {
        return TEST == this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProduction() {
        return PRODUCTION == this;
    }

}
