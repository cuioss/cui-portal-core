package de.cuioss.portal.configuration.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.support.InvalidFilterConfiguration;

@EnableAutoWeld
@AddEnabledInterceptors(PortalConfigurationChangeInterceptorImpl.class)
class PortalConfigurationChangeInvalidFilterInterceptorTest {

    @Inject
    private InvalidFilterConfiguration invalidConfiguration;

    @Inject
    @PortalConfigurationChangeEvent
    private Event<String> event;

    @Test
    void shouldFailOnEvent() {
        assertTrue(invalidConfiguration.isEmpty());
        assertThrows(IllegalStateException.class, () -> {
            event.fire("boom");
        });
    }

}
