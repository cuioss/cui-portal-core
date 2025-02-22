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

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.UserChangeEvent;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * CDI producer for managing authenticated user information within the portal context.
 * This producer maintains the state of the currently authenticated user and responds
 * to authentication events.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Produces {@link AuthenticatedUserInfo} for injection</li>
 *   <li>Manages user state changes during authentication/logout</li>
 *   <li>Integrates with the portal's authentication facade</li>
 *   <li>Responds to {@link UserChangeEvent} events</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * &#64;Inject
 * Provider<AuthenticatedUserInfo> userInfo;
 * 
 * void someMethod() {
 *     if (userInfo.get().isAuthenticated()) {
 *         // Handle authenticated user
 *     }
 * }
 * </pre>
 *
 * <p>This producer is application scoped but produces user-specific information
 * through the use of proxies and providers.</p>
 *
 * @author Oliver Wolff
 * @see AuthenticatedUserInfo
 * @see UserChangeEvent
 * @see AuthenticationFacade
 * @since 1.0
 */
@RequestScoped
@EqualsAndHashCode(exclude = {"authenticationFacade", "servletRequestProvider"})
@ToString(exclude = {"authenticationFacade", "servletRequestProvider"})
public class PortalUserProducer implements Serializable {

    private static final String BEAN_NAME = "portalUser";

    @Serial
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
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfo;
    }

    /**
     * Listener on changes of the user. Will be used during login action.
     */
    void actOnUserChangeEvent(@Observes @UserChangeEvent final AuthenticatedUserInfo newUserInfo) {
        userInfo = newUserInfo;
    }
}
