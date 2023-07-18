package de.cuioss.portal.configuration.application;

import de.cuioss.uimodel.application.CuiProjectStage;

/**
 * Temporary interface! Will be obsolete with the migration to JSF 2.3 because
 * then all Beans are CDI beans.
 *
 * @author Sven Haag
 */
public interface PortalProjectStage {

    /**
     * @return CuiProjectStage
     */
    CuiProjectStage getProjectStage();
}
