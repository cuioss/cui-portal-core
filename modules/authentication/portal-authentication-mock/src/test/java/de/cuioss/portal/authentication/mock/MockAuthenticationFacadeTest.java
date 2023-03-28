package de.cuioss.portal.authentication.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
@EnablePortalConfiguration
class MockAuthenticationFacadeTest implements ShouldBeNotNull<MockAuthenticationFacade> {

    private MockHttpServletRequest servletRequest;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @BeforeEach
    void beforeEach() {
        configuration.initializeConfigurationSystem();
        servletRequest = new CuiMockHttpServletRequest();
        servletRequest.setPathInfo("some.url");
    }

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private MockAuthenticationFacade underTest;

    @Test
    void shouldAuthenticateAsDefault() {
        final var userInfo = underTest
                .retrieveCurrentAuthenticationContext(servletRequest);
        assertNotNull(userInfo);
        assertTrue(userInfo.isAuthenticated());
    }

    @Test
    void shouldLogoutAuthenticatedAsDefault() {
        underTest.logout(servletRequest);
        final var userInfo = underTest
                .retrieveCurrentAuthenticationContext(servletRequest);
        assertFalse(userInfo.isAuthenticated());
    }

    @Test
    void shouldNotAuthenticateAsDefaultAsConfigured() {
        configuration.fireEvent(MockAuthenticationFacade.CONFIGURATION_KEY_AUTHENTICATED, "false");
        final var userInfo = underTest
                .retrieveCurrentAuthenticationContext(servletRequest);
        assertNotNull(userInfo);
        assertFalse(userInfo.isAuthenticated());
    }

    @Test
    void shouldProvideConfiguredRoles() {
        configuration.fireEvent(MockAuthenticationFacade.CONFIGURATION_KEY_ROLES, "role, role2");
        final var userInfo = underTest
                .retrieveCurrentAuthenticationContext(servletRequest);
        assertEquals(2, userInfo.getRoles().size());
    }

    @Test
    void shouldHandleNonConfiguredRoles() {
        final var userInfo = underTest
                .retrieveCurrentAuthenticationContext(servletRequest);
        assertEquals(0, userInfo.getRoles().size());
    }
}
