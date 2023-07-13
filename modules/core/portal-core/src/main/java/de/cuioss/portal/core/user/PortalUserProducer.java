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

import org.apache.deltaspike.core.api.common.DeltaSpike;

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
    @DeltaSpike
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
