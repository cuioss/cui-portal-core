package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.ConfigurationSourceChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.schedule.ConfigChangeObserver;
import de.cuioss.portal.configuration.impl.schedule.FileWatcherServiceImpl;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.schedule.FileChangedEvent;
import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;

@EnablePortalConfigurationLocal
@AddBeanClasses({ FileWatcherServiceImpl.class, InstallationConfigSourcePathInitializer.class,
        ConfigChangeObserver.class })
@EnableAutoWeld
@EnableTestLogger(rootLevel = TestLogLevel.TRACE)
class PortalInstallationConfigurationTest {

    private static final Path CONFIG_DIR = Paths.get("target/test-classes/META-INF/installation-configuration-test");
    private static final Path APPLICATION_YML = CONFIG_DIR.resolve("application.yml");
    private static final Path APPLICATION_PRODUCTION_YML = CONFIG_DIR.resolve("application-production.yml");
    private static final Path APPLICATION_USER_YML = CONFIG_DIR.resolve("application-user.yml");
    private static final String PROPERTY_VALUE_PROD = "property_value_b";
    private static final Path PATH_NOT_THERE = Paths.get("/not/there");

    final TypedGenerator<String> keyValueGenerator = Generators.letterStrings(10, 15);

    private Map<String, String> payload;

    @Inject
    @FileChangedEvent
    private Event<Path> fileChangeEvent;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Inject
    @ConfigProperty(name = "test.yaml.property")
    private Provider<String> keyValue;

    @BeforeEach
    void initConfigSystem() {
        configuration.clear();

        System.setProperty(PORTAL_CONFIG_DIR, CONFIG_DIR.toString());
        // needed to trigger InstallationConfigSourcePathInitializer
        configuration.put(PORTAL_CONFIG_DIR, CONFIG_DIR.toString());

        configuration.initializeConfigurationSystem();
        configuration.fireEvent();
    }

    @AfterAll
    static void afterAll() {
        System.clearProperty(PORTAL_CONFIG_DIR);
    }

    @Test
    void productionFileTakesPrecedence() {
        assertEquals(PROPERTY_VALUE_PROD, keyValue.get());
    }

    @Test
    void shouldReactOnFileChangeEvents() {
        // InvalidPath should be ignored
        payload = null;
        fileChangeEvent.fire(PATH_NOT_THERE);
        assertNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_YML); // initial loading of config source
        assertNull(payload);
        appendArbitraryProperty(APPLICATION_YML); // do actual config change
        fileChangeEvent.fire(APPLICATION_YML);
        assertNotNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_PRODUCTION_YML); // initial loading of config source
        assertNull(payload);
        appendArbitraryProperty(APPLICATION_PRODUCTION_YML); // do actual config change
        fileChangeEvent.fire(APPLICATION_PRODUCTION_YML);
        assertNotNull(payload);

        payload = null;
        fileChangeEvent.fire(APPLICATION_USER_YML);
        assertNull(payload); // because file does not exist
    }

    private void appendArbitraryProperty(final Path file) {
        final var yamlProperty = System.lineSeparator() + keyValueGenerator.next() + ": " + keyValueGenerator.next();
        try {
            Files.write(file, yamlProperty.getBytes(), StandardOpenOption.APPEND);
        } catch (final IOException e) {
            fail("Could not append to file", e);
        }
    }

    void providerChangeEventListener(@Observes @ConfigurationSourceChangeEvent final Map<String, String> eventMap) {
        payload = eventMap;
    }
}
