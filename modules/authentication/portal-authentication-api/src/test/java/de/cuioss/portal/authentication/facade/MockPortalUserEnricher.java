package de.cuioss.portal.authentication.facade;

import javax.annotation.Priority;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUserEnricher;

@Priority(200)
class MockPortalUserEnricher implements PortalUserEnricher {

    @Override
    public AuthenticatedUserInfo apply(AuthenticatedUserInfo authenticatedUserInfo) {
        authenticatedUserInfo.getRoles().add("testRole");
        return authenticatedUserInfo;
    }
}
