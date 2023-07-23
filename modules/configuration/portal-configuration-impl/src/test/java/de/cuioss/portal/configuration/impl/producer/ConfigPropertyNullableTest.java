package de.cuioss.portal.configuration.impl.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.ConfigPropertyNullable;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;

@SuppressWarnings("el-syntax")
@EnableAutoWeld
@EnablePortalConfigurationLocal
class ConfigPropertyNullableTest {

    private static final String KEY = "a.config.key";

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Inject
    @ConfigPropertyNullable(name = KEY)
    private Provider<String> value;

    @BeforeEach
    public void beforeTest() {
        configuration.clear();
        configuration.initializeConfigurationSystem();
    }

    @Test
    void nullForUnknownKey() {
        assertDoesNotThrow(() -> value.get());
        assertNull(value.get());
    }

    @Test
    void nullForEmptyDefault() {
        configuration.fireEvent(KEY, "${not.there:}");
        assertDoesNotThrow(() -> value.get(), "should use empty default value");
        assertNull(value.get());
    }

    @Test
    void nullForUnresolvableVariable() {
        configuration.fireEvent(KEY, "${not.there}");
        assertDoesNotThrow(() -> value.get(), "should ignore unresolvable key");
        assertNull(value.get());
    }
}
