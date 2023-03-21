package de.cuioss.portal.configuration.impl.initializer;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import lombok.Getter;

@EnableAutoWeld
@EnableTestLogger
class PortalConfigurationLoggerTest {

    @Inject
    @Getter
    @PortalInitializer
    private PortalConfigurationLogger underTest;

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @BeforeEach
    public void beforeTest() {
        applicationInitializers.forEach(ApplicationInitializer::initialize);
    }

    @Test
    public void shouldIgnoreOnInfoLevel() {
        TestLogLevel.INFO.addLogger(PortalConfigurationLogger.class);
        underTest.initialize();
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.WARN, PortalConfigurationLogger.class);
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.INFO, "Portal Configuration");
    }
}
