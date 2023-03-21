package de.cuioss.portal.core.test.mocks.authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;

@EnableAutoWeld
@AddBeanClasses(PortalTestUserProducer.class)
class PortalTestUserProducerTest {

    @Inject
    private PortalTestUserProducer userProducer;

    @Inject
    @PortalUser
    private Provider<AuthenticatedUserInfo> userProvider;

    @Test
    void shouldHandleService() {
        assertNotNull(userProducer);
    }

    @Test
    void shouldProduceUser() {
        assertNotNull(userProvider.get());
        assertTrue(userProvider.get().isAuthenticated());
        userProducer.authenticated(false);
        assertFalse(userProvider.get().isAuthenticated());
    }
}
