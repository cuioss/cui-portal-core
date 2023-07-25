package de.cuioss.portal.core.test.junit5;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CUSTOMIZATION_ENABLED;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_SERVLET_BASIC_AUTH_ALLOWED;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;

@EnableAutoWeld
@EnablePortalConfiguration(configuration = "key1:value1")
class PortalConfigurationMockExtensionTest {

    @Inject
    @ConfigProperty(name = PORTAL_SERVLET_BASIC_AUTH_ALLOWED)
    private Provider<Boolean> attributeMpProvider;

    @Inject
    @ConfigProperty(name = PORTAL_STAGE)
    private Provider<Optional<String>> attributeMpOptional;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    PortalConfigurationMockExtensionTest() {
    }

    @Test
    void shouldHandleMicroProfile() {
        assertNotNull(attributeMpProvider);
        assertNotNull(attributeMpProvider.get());
        assertTrue(attributeMpProvider.get());

        assertNotNull(attributeMpOptional);
        assertTrue(attributeMpOptional.get().isPresent());

        configuration.fireEvent(PORTAL_CUSTOMIZATION_ENABLED, "false");
        assertTrue(attributeMpProvider.get());
    }
}
