package de.cuioss.portal.core.test.mocks.authentication;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo.BaseAuthenticatedUserInfoBuilder;
import lombok.experimental.Delegate;

/**
 * Replacement for instances of {@link PortalUser} producer, enabling finer
 * control without using an underlying service. <em>Caution: </em> This bean is
 * application scoped in order not to introduce problems with improper scoping.
 *
 * @author Oliver Wolff
 */
@Named
@ApplicationScoped
public class PortalTestUserProducer {

    private static final String USER = "user";

    /** "portalUser" */
    public static final String BEAN_NAME = "portalUser";

    @Delegate
    private final BaseAuthenticatedUserInfoBuilder userInfoBuilder =
        BaseAuthenticatedUserInfo.builder().displayName(USER)
                .identifier(USER)
                .qualifiedIdentifier(USER);

    /**
     * Sets an authenticated default user.
     */
    @PostConstruct
    public void init() {
        authenticated(true);
    }

    @SuppressWarnings("cdi-ambiguous-name")
    @Produces
    @Named(PortalTestUserProducer.BEAN_NAME)
    @PortalUser
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfoBuilder.build();
    }

}
