package de.cuioss.portal.authentication.facade;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;

/**
 * Basic interface for cui-portals authentication mechanism. This variant is
 * used by sso logins
 *
 * @author Matthias Walliczek
 *
 */
public interface AuthenticationFacade {

    /**
     * Access the current {@link AuthenticatedUserInfo}.
     *
     * @param servletRequest The servlet request.
     * @return Returns <code>null</code> in case no authentication has taken place.
     *         In case of a existing authentication a populated
     *         {@link AuthenticatedUserInfo} instance is returned.
     */
    AuthenticatedUserInfo retrieveCurrentAuthenticationContext(HttpServletRequest servletRequest);

    /**
     * Logs out the currently authenticated user.
     *
     * @param servletRequest The current servlet request that triggered the logout.
     * @return Returns a boolean indicating whether the logout request was
     *         successful.
     */
    boolean logout(HttpServletRequest servletRequest);

    /**
     * @return The {@link AuthenticationSource} identifying the Source this
     *         {@link AuthenticationFacade} uses.
     */
    AuthenticationSource getAuthenticationSource();

}
