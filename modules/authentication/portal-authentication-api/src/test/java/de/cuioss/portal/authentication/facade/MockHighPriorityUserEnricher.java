package de.cuioss.portal.authentication.facade;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUserEnricher;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;

@ApplicationScoped
@Priority(200)
public class MockHighPriorityUserEnricher implements PortalUserEnricher {

    @Override
    public AuthenticatedUserInfo apply(AuthenticatedUserInfo authenticatedUserInfo) {
        if (null == authenticatedUserInfo) {
            return null;
        }
        return BaseAuthenticatedUserInfo.builder()
                .authenticated(authenticatedUserInfo.isAuthenticated())
                .displayName(authenticatedUserInfo.getDisplayName())
                .identifier(authenticatedUserInfo.getIdentifier())
                .roles(immutableList("highPriorityRole"))
                .build();
    }
}
