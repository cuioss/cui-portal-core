package de.cuioss.portal.configuration.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.support.EmptyFilterConfiguration;
import de.cuioss.tools.collect.CollectionLiterals;

@EnableAutoWeld
@AddEnabledInterceptors(PortalConfigurationChangeInterceptorImpl.class)
class PortalConfigurationChangeEmptyFilterInterceptorTest {

    static final Map<String, String> UNKNOWN_ELEMENTS =
        CollectionLiterals.immutableMap("key1", "value1", "key2", "value2", "key3", "value3");

    @SuppressWarnings("unused")
    @Inject
    private EmptyFilterConfiguration emptyFilterConfiguration;

    @Inject
    @PortalConfigurationChangeEvent
    private Event<Map<String, String>> event;

    @Test
    void shouldFailOnEvent() {
        assertThrows(IllegalStateException.class, () -> {
            event.fire(UNKNOWN_ELEMENTS);
        });
    }

}
