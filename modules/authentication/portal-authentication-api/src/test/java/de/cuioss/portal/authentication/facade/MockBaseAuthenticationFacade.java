package de.cuioss.portal.authentication.facade;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import lombok.Setter;

class MockBaseAuthenticationFacade extends BaseAuthenticationFacade {

    @Setter
    private AuthenticatedUserInfo authenticatedUserInfo;

    @Override
    public AuthenticatedUserInfo retrieveCurrentAuthenticationContext(HttpServletRequest servletRequest) {
        return enrich(authenticatedUserInfo);
    }

    @Override
    public boolean logout(HttpServletRequest servletRequest) {
        return false;
    }

    @Override
    public AuthenticationSource getAuthenticationSource() {
        return null;
    }
}
