package de.cuioss.portal.authentication.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import de.cuioss.tools.collect.CollectionLiterals;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ MockBaseAuthenticationFacade.class, MockPortalUserEnricher.class })
class BaseAuthenticationFacadeTest implements ShouldBeNotNull<MockBaseAuthenticationFacade> {

    @Inject
    @Getter
    private MockBaseAuthenticationFacade underTest;

    @Test
    void shouldEnrich() {
        underTest.setAuthenticatedUserInfo(new BaseAuthenticatedUserInfo(true, "display", "identifier", null, null));
        var result = underTest.retrieveCurrentAuthenticationContext(null);
        assertEquals(CollectionLiterals.mutableList("testRole"), result.getRoles());
    }

}
