package de.cuioss.portal.configuration.impl;

import static de.cuioss.portal.configuration.impl.PortalConfigurationChangeEmptyFilterInterceptorTest.UNKNOWN_ELEMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.impl.support.HappyCaseFilterConfiguration;
import de.cuioss.portal.configuration.impl.support.HappyCaseSingleConfiguration;

@EnableAutoWeld
@AddEnabledInterceptors(PortalConfigurationChangeInterceptorImpl.class)
class PortalConfigurationChangeFilterInterceptorTest {

    @Inject
    private HappyCaseSingleConfiguration happyCaseSingleConfiguration;

    @Inject
    private HappyCaseFilterConfiguration happyCaseFilterConfiguration;

    @Inject
    @PortalConfigurationChangeEvent
    private Event<Map<String, String>> event;

    @BeforeEach
    void before() {
        happyCaseSingleConfiguration.reset();
        happyCaseFilterConfiguration.reset();
    }

    @Test
    void hapyCaseSingleShouldWork() {
        assertTrue(happyCaseSingleConfiguration.isEmpty());
        event.fire(new HashMap<>());
        assertTrue(happyCaseSingleConfiguration.isEmpty());
        event.fire(UNKNOWN_ELEMENTS);
        assertTrue(happyCaseSingleConfiguration.isEmpty());
        event.fire(HappyCaseSingleConfiguration.HAPPY_CASE_MAP);
        assertEquals(HappyCaseSingleConfiguration.HAPPY_CASE_MAP, happyCaseSingleConfiguration.getDeltaMap());
    }

    @Test
    void hapyCaseFilterShouldWork() {
        assertTrue(happyCaseFilterConfiguration.isEmpty());
        event.fire(new HashMap<>());
        assertTrue(happyCaseFilterConfiguration.isEmpty());
        event.fire(UNKNOWN_ELEMENTS);
        assertTrue(happyCaseFilterConfiguration.isEmpty());
        event.fire(HappyCaseFilterConfiguration.HAPPY_CASE_MAP);
        assertEquals(HappyCaseFilterConfiguration.HAPPY_CASE_MAP, happyCaseFilterConfiguration.getDeltaMap());
    }

    @Test
    void shouldIgnoreUnknownElements() {
        event.fire(UNKNOWN_ELEMENTS);
        assertTrue(happyCaseSingleConfiguration.isEmpty());
        assertTrue(happyCaseFilterConfiguration.isEmpty());
    }

    @Test
    void shouldDistinctBetweenClients1() {
        event.fire(HappyCaseSingleConfiguration.HAPPY_CASE_MAP);
        assertEquals(HappyCaseSingleConfiguration.HAPPY_CASE_MAP, happyCaseSingleConfiguration.getDeltaMap());
        assertTrue(happyCaseFilterConfiguration.isEmpty());
    }

    @Test
    void shouldDistinctBetweenClients2() {
        event.fire(HappyCaseSingleConfiguration.HAPPY_CASE_MAP);
        assertEquals(HappyCaseSingleConfiguration.HAPPY_CASE_MAP, happyCaseSingleConfiguration.getDeltaMap());
        assertTrue(happyCaseFilterConfiguration.isEmpty());
    }
}
