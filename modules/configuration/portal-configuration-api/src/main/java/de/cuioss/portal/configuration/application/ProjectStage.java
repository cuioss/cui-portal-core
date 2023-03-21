package de.cuioss.portal.configuration.application;

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
    CONFIGURATION,

    /***/
    PRODUCTION;

    private static final String PROJECT_STAGE_CONFIGURATION_ERROR = "Portal-500: Unknown project stage '{}' detected! "
            + "Set the property 'icw.portal.configuration.stage' to one of: development, test, configuration, production";

    /**
     * Try to find corresponding portal project stage from string.
     *
     * @param stage
     * @return corresponding stage or PRODUCTION if stage is unknown.
     */
    public static ProjectStage fromString(final String stage) {
        if (null != stage) {
            for (ProjectStage ps : ProjectStage.values()) {
                if (ps.name().equalsIgnoreCase(stage)) {
                    return ps;
                }
            }
        }

        new CuiLogger(ProjectStage.class).error(PROJECT_STAGE_CONFIGURATION_ERROR, stage);
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

    @Override
    public boolean isConfiguration() {
        return CONFIGURATION == this;
    }
}
