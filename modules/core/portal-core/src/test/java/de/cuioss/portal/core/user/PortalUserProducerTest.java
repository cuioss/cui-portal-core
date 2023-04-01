package de.cuioss.portal.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.portal.core.test.support.PortalAuthenticationFacadeMock;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ PortalAuthenticationFacadeMock.class })
@ActivateScopes(RequestScoped.class)
class PortalUserProducerTest implements ShouldHandleObjectContracts<PortalUserProducer> {

    @Produces
    @DeltaSpike
    @RequestScoped
    HttpServletRequest produceEasyMockServletRequest() {
        return EasyMock.createNiceMock(HttpServletRequest.class);
    }

    @Inject
    @Getter
    private PortalUserProducer underTest;

    @Inject
    @PortalUser
    private Provider<AuthenticatedUserInfo> userInfoProvider;

    /**
     * Checks whether the correct user is produced.
     */
    @Test
    void shouldProduceUser() {
        var userInfo = userInfoProvider.get();
        assertNotNull(userInfo);
        assertTrue(userInfo.isAuthenticated());
        assertEquals(PortalAuthenticationFacadeMock.USER, userInfo.getDisplayName());
    }
}
