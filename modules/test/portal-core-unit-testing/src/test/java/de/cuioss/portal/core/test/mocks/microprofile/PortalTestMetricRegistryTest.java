package de.cuioss.portal.core.test.mocks.microprofile;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;

import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class PortalTestMetricRegistryTest implements ShouldBeNotNull<PortalTestMetricRegistry> {

    @Inject
    @Getter
    private PortalTestMetricRegistry underTest;
}
