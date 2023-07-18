package de.cuioss.portal.configuration;

import lombok.experimental.UtilityClass;

/**
 * Provides Constants for the Portal
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
public class CuiConstants {

    /**
     * The currently active prefix for mapping jsf-views to the servlet: "/faces/"
     */
    public static final String FACES_VIEW_PREFIX = "/faces/";

    /**
     * The currently active prefix for mapping jsf-resources to the servlet:
     * "/javax.faces.resource/"
     */
    public static final String FACES_RESOURCES_PREFIX = "/javax.faces.resource/";
}
