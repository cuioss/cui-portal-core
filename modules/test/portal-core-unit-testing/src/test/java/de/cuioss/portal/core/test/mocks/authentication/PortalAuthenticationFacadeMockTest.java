package de.cuioss.portal.core.test.mocks.authentication;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class PortalAuthenticationFacadeMockTest implements ShouldBeNotNull<PortalAuthenticationFacadeMock> {

    @Getter
    @Inject
    @PortalAuthenticationFacade
    private PortalAuthenticationFacadeMock underTest;

    @Test
    void shouldDefaultSensibly() {
        assertNotNull(underTest.getAvailableUserStores());
    }

}
