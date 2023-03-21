package de.cuioss.portal.core.test.mocks.core;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.storage.PortalClientStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class PortalClientStorageMockTest implements ShouldBeNotNull<PortalClientStorageMock> {

    @Getter
    @Inject
    @PortalClientStorage
    private PortalClientStorageMock underTest;

    @Test
    void shouldDefaultSensibly() {
        assertNotNull(underTest.get("key", "value"));
    }

}
