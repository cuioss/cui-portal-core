package de.cuioss.portal.configuration.impl.source;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationKeys;
import io.smallrye.config.inject.ConfigProducer;

@EnableAutoWeld
@AddBeanClasses(ConfigProducer.class)
class PropertiesDefaultConfigSourceProviderTest {

    @Inject
    @ConfigProperty(name = PortalConfigurationKeys.LOCALE_DEFAULT)
    String config;

    @Test
    void shouldLoadConfiguregProperties() {
        assertEquals("de", config);
    }

}
