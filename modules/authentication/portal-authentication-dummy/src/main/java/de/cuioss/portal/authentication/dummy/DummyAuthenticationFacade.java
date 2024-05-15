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
package de.cuioss.portal.authentication.dummy;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

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
