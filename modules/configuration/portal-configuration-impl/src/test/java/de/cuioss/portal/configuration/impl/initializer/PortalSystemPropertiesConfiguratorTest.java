package de.cuioss.portal.configuration.impl.initializer;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_SYSTEM_PROPERTY_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfiguration;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import lombok.Getter;

@EnablePortalConfiguration
@EnableAutoWeld
@EnableTestLogger
class PortalSystemPropertiesConfiguratorTest {

    private static final String PROPERTY_VALUE_FOR = "Property value for";

    private static final String SOME_VALUE = "someValue";

    private static final String PROPERTY_KEY = "myPropertyKey";

    private static final String PROPERTY_KEY_PREFIXED = PORTAL_SYSTEM_PROPERTY_PREFIX + PROPERTY_KEY;

    @Inject
    @Getter
    @PortalInitializer
    private PortalSystemPropertiesConfigurator underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @BeforeEach
    void beforeTest() {
        configuration.clear();
        configuration.initializeConfigurationSystem();
    }

    @AfterEach
    void afterTest(){
        System.clearProperty(PROPERTY_KEY);
    }

    @Test
    void shouldDoNothingWithoutProperties() {
        underTest.initialize();
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.INFO, PortalSystemPropertiesConfigurator.class);
    }

    @Test
    void shouldIgnoreInvalidKey() {
        configuration.put(PORTAL_SYSTEM_PROPERTY_PREFIX, SOME_VALUE);
        configuration.initializeConfigurationSystem();
        assertNull(System.getProperty(PROPERTY_KEY));
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.INFO, PortalSystemPropertiesConfigurator.class);
    }

    @Test
    void shouldSetPropertyOnInitialize() {
        assertNull(System.getProperty(PROPERTY_KEY));
        configuration.put(PROPERTY_KEY_PREFIXED, SOME_VALUE);
        configuration.initializeConfigurationSystem();

        assertEquals(SOME_VALUE, System.getProperty(PROPERTY_KEY));
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.INFO, PROPERTY_VALUE_FOR);
    }

    @Test
    void shouldLogOnlyUnchangedProperty() {
        TestLogLevel.DEBUG.addLogger(PortalSystemPropertiesConfigurator.class);
        System.setProperty(PROPERTY_KEY, SOME_VALUE);
        configuration.put(PROPERTY_KEY_PREFIXED, SOME_VALUE);
        configuration.initializeConfigurationSystem();

        assertEquals(SOME_VALUE, System.getProperty(PROPERTY_KEY));
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.DEBUG, PROPERTY_VALUE_FOR);
    }

    @Test
    void shouldSetPropertyOnEvent() {
        assertNull(System.getProperty(PROPERTY_KEY));
        configuration.put(PROPERTY_KEY_PREFIXED, SOME_VALUE);
        configuration.fireEvent();

        assertEquals(SOME_VALUE, System.getProperty(PROPERTY_KEY));
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.INFO, PROPERTY_VALUE_FOR);
    }

    @Test
    void shouldIgnoreEmptyMapOnEvent() {
        configuration.fireEvent();

        assertNull(System.getProperty(PROPERTY_KEY));
    }

    @Test
    void shouldIgnoreOtherKeysOnEvent() {
        configuration.put(PROPERTY_KEY, SOME_VALUE);
        configuration.fireEvent();

        assertNull(System.getProperty(PROPERTY_KEY));
    }
}
