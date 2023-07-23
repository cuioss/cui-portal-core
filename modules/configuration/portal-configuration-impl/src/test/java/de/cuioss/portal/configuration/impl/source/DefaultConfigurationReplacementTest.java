package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.RESOURCE_VERSION;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import lombok.Getter;

@EnablePortalConfigurationLocal
@EnableAutoWeld
class DefaultConfigurationReplacementTest {

    @Inject
    @Getter
    private ConfigurationResolver underTest;

    @Test
    void shouldRepalceVersionForResourceHandler() {
        assertTrue(underTest.containsKey(RESOURCE_VERSION));
        assertNotNull(underTest.getString(RESOURCE_VERSION));
        assertFalse(underTest.getString(RESOURCE_VERSION).contains("${"));
    }

}
