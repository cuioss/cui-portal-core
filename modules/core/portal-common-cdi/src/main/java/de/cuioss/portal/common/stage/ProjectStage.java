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

import de.cuioss.portal.common.PortalCommonCDILogMessages;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.CuiProjectStage;

/**
 * Portal project stage.
 *
 * @author Sven Haag
 */
public enum ProjectStage implements CuiProjectStage {

    /***/
    DEVELOPMENT,

    /***/
    TEST,

    /***/
    PRODUCTION;

    private static final CuiLogger LOGGER = new CuiLogger(ProjectStage.class);

    /**
     * Try to find the corresponding portal project stage from string.
     *
     * @param stage
     * @return the corresponding stage or PRODUCTION if stage is unknown.
     */
    public static ProjectStage fromString(final String stage) {
        if (null != stage) {
            for (ProjectStage ps : ProjectStage.values()) {
                if (ps.name().equalsIgnoreCase(stage)) {
                    return ps;
                }
            }
        }

        LOGGER.error(PortalCommonCDILogMessages.UNKNOWN_PROJECT_STAGE.format(stage));
        return ProjectStage.PRODUCTION;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public boolean isDevelopment() {
        return DEVELOPMENT == this;
    }

    @Override
    public boolean isTest() {
        return TEST == this;
    }

    @Override
    public boolean isProduction() {
        return PRODUCTION == this;
    }

}
