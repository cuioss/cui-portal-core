package de.cuioss.portal.authentication.facade;

/**
 * Identifies the currently active Identification Source
 *
 * @author Oliver Wolff
 *
 */
public enum AuthenticationSource {

    /** Any application specific implementation. */
    CUSTOM,

    /** The authentication is done by an external Key-Cloak Server. */
    KEYCLOAK,

    /** Special variant for testing purpose. */
    MOCK,

    /** Special variant always returning a non authenticated user. */
    DUMMY,

    /** Open ID Connect / Oauth2 */
    OPEN_ID_CONNECT,

    /** The application local tomcat-users.xml */
    TOMCAT_USER,

    /** Default / {@code null} value. */
    UNDEFINED
}
