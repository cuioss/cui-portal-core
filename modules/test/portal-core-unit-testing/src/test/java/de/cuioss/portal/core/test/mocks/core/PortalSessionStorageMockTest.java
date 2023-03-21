package de.cuioss.portal.core.test.mocks.core;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class PortalSessionStorageMockTest implements ShouldBeNotNull<PortalSessionStorageMock> {

    @Inject
    @PortalSessionStorage
    @Getter
    private PortalSessionStorageMock underTest;

}
