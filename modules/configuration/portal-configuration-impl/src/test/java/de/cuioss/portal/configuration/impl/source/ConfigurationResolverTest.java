package de.cuioss.portal.configuration.impl.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.jboss.weld.environment.util.Collections;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfiguration;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import lombok.Getter;

@EnablePortalConfiguration
@EnableAutoWeld
class ConfigurationResolverTest {

    @Inject
    @Getter
    private ConfigurationResolver underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Test
    void testGetString() {
        configuration.put("abc", "def");
        configuration.fireEvent();
        assertEquals("def", underTest.getString("abc"));
    }

    @Test
    void testGetKeys() {
        configuration.put("abc", "def");
        configuration.fireEvent();
        assertTrue(Collections.asList(underTest.getKeys()).contains("abc"));
    }
}
