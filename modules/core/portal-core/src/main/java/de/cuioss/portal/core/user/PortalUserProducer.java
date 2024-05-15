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
package de.cuioss.portal.core.user;

import java.io.Serializable;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.portal.authentication.UserChangeEvent;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import jakarta.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Stateful producer for the {@link AuthenticatedUserInfo} identified by
 * {@link PortalUser}
 *
 * @author Oliver Wolff
 */
@RequestScoped
@EqualsAndHashCode(exclude = { "authenticationFacade", "servletRequestProvider" })
@ToString(exclude = { "authenticationFacade", "servletRequestProvider" })
public class PortalUserProducer implements Serializable {

    private static final String BEAN_NAME = "portalUser";

    private static final long serialVersionUID = -4732319864322086918L;

    private AuthenticatedUserInfo userInfo;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @PortalAuthenticationFacade
    AuthenticationFacade authenticationFacade;

    @Inject
    Provider<HttpServletRequest> servletRequestProvider;

    /**
     * Initializes the produces by fetching user from facade
     */
    @PostConstruct
    public void initBean() {
        userInfo = authenticationFacade.retrieveCurrentAuthenticationContext(servletRequestProvider.get());
    }

    @Produces
    @Named(BEAN_NAME)
    @PortalUser
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfo;
    }

    /**
     * Listener on changes of the user. Will be used during login action.
     *
     * @param newUserInfo
     */
    void actOnUserChangeEvent(@Observes @UserChangeEvent final AuthenticatedUserInfo newUserInfo) {
        userInfo = newUserInfo;
    }
}
