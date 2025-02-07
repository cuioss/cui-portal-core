package de.cuioss.portal.authentication.facade;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUserEnricher;

@ApplicationScoped
@Priority(50) // Higher priority (lower number) to run first
public class MockHighPriorityUserEnricher implements PortalUserEnricher {

    @Override
    public AuthenticatedUserInfo apply(AuthenticatedUserInfo authenticatedUserInfo) {
        if (null == authenticatedUserInfo) {
            return null;
        }
        authenticatedUserInfo.getRoles().add("highPriorityRole");
        return authenticatedUserInfo;
    }
}
