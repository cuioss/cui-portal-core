package de.cuioss.portal.common.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PortalResourceLoaderTest {

    @Test
    void shouldLoadExisitingResource() {
        assertTrue(PortalResourceLoader.getRessource("/de/cuioss/portal/l18n/messages/high1.properties", getClass())
                .isPresent());
    }

    @Test
    void shouldHAndleNotExisitingResource() {
        assertTrue(PortalResourceLoader.getRessource("/not/ther", getClass()).isEmpty());
    }

}
