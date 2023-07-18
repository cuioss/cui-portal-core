package de.cuioss.portal.authentication.facade;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.UserStore;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultObject;

/**
 * Extension to {@link AuthenticationFacade} that supports form-based logins
 *
 */
public interface FormBasedAuthenticationFacade extends AuthenticationFacade {

    /**
     * Authenticates a user with the provided credentials. The implementation is
     * responsible for invalidating the old and creating a new session.
     *
     * @param request     The current {@link HttpServletRequest} that needs to be
     *                    authenticated.
     * @param credentials The credentials for authentication
     * @return A {@link ResultObject} with the corresponding
     *         {@link AuthenticatedUserInfo} with
     *         {@link AuthenticatedUserInfo#isAuthenticated()} being {@code true} in
     *         case of successful logins, otherwise it will provide the
     *         corresponding error message to be displayed.
     */
    ResultObject<AuthenticatedUserInfo> login(HttpServletRequest request, LoginCredentials credentials);

    /**
     * Provides list of available systems. Please note that no particular ordering
     * is added on top. The content is displayed on ui level as provided.
     *
     * @return The map providing the available system data. Must not be null but may
     *         be empty.
     */
    List<UserStore> getAvailableUserStores();

}
