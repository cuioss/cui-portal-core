package de.cuioss.portal.authentication.dummy;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.authentication.facade.AuthenticationResults;
import de.cuioss.portal.authentication.facade.AuthenticationSource;
import de.cuioss.portal.authentication.facade.FormBasedAuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.UserStore;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Dummy implementation of the {@link AuthenticationFacade} interface.
 * <p>
 * In contrast to the MockAuthenticationFacade this one always returns an user
 * that is not logged in. Therefore there is no further configuration capability
 * available
 * </p>
 *
 * @author Oliver Wolff
 */
@PortalAuthenticationFacade
@ApplicationScoped
public class DummyAuthenticationFacade implements FormBasedAuthenticationFacade {

    private static final String DUMMY = "dummy";
    private static final CuiLogger log = new CuiLogger(DummyAuthenticationFacade.class);

    static final AuthenticatedUserInfo NOT_LOGGED_IN = new BaseAuthenticatedUserInfo(false, DUMMY, DUMMY, DUMMY, DUMMY);

    @Getter
    @Setter
    private AuthenticationSource authenticationSource = AuthenticationSource.DUMMY;

    /**
     * The dummy implementation provides a successful login in case the identifier
     * and password are equal.
     */
    @Override
    public ResultObject<AuthenticatedUserInfo> login(final HttpServletRequest servletRequest,
            final LoginCredentials loginCredentials) {
        log.debug("Login called");
        return AuthenticationResults.validResult(NOT_LOGGED_IN);
    }

    @Override
    public boolean logout(final HttpServletRequest servletRequest) {
        log.debug("Logout called");
        return false;
    }

    @Override
    public AuthenticatedUserInfo retrieveCurrentAuthenticationContext(final HttpServletRequest servletRequest) {
        log.debug("retrieveCurrentAuthenticationContext called");
        return NOT_LOGGED_IN;
    }

    @Override
    public List<UserStore> getAvailableUserStores() {
        log.debug("getAvailableUserStores called");
        return Collections.emptyList();
    }

}
