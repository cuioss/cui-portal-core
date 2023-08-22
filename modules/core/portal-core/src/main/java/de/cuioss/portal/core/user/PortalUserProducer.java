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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.portal.authentication.UserChangeEvent;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Stateful producer for the {@link AuthenticatedUserInfo} identified by
 * {@link PortalUser}
 *
 * @author Oliver Wolff
 */
@Named
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
    private AuthenticationFacade authenticationFacade;

    @Inject
    private Provider<HttpServletRequest> servletRequestProvider;

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
