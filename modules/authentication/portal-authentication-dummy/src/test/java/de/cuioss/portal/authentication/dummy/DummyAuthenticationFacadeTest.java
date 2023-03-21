package de.cuioss.portal.authentication.dummy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class DummyAuthenticationFacadeTest implements ShouldBeNotNull<DummyAuthenticationFacade> {

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private DummyAuthenticationFacade underTest;

    @Test
    void shouldAllwaysReturnDummyUser() {

        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.retrieveCurrentAuthenticationContext(null));
        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.login(null, null).getResult());

        assertFalse(underTest.logout(EasyMock.createNiceMock(HttpServletRequest.class)));

        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.retrieveCurrentAuthenticationContext(null));
        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.login(null, null).getResult());
    }

    @Test
    void shouldProvideSensibleDefaults() {
        assertFalse(DummyAuthenticationFacade.NOT_LOGGED_IN.isAuthenticated());
        assertNotNull(underTest.getAvailableUserStores());
        assertTrue(underTest.getAvailableUserStores().isEmpty());
    }
}
