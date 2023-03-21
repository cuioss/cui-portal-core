package de.cuioss.portal.core.test.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = "key1:value1:zzz")
class PortalTestConfigurationExtensionTest {

    @Inject
    @ConfigProperty(name = "key1")
    private Provider<String> attribute;

    @Test
    void shouldHandleConfig() {
        assertEquals("value1:zzz", attribute.get());
    }
}
