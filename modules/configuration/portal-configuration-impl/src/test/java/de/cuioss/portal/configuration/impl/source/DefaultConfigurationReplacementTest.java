package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

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
    void shouldReplaceVersionForResourceHandler() {
        assertTrue(underTest.containsKey(PORTAL_STAGE));
        assertNotNull(underTest.getString(PORTAL_STAGE));
    }

    @Test
    void shouldEnumerateKeys() {
        assertTrue(Collections.list(underTest.getKeys()).size() > 10);
    }

}
