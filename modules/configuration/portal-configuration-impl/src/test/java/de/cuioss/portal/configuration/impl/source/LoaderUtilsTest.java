package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.impl.source.LoaderUtils.loadConfigurationFromSource;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.EMPTY_PATH;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.EXISTING_PROPERTIES;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.EXISTING_YML;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.NOT_EXISTING_PROPERTIES;
import static de.cuioss.portal.configuration.impl.support.TestResourcesRepository.NOT_EXISTING_YML;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.io.FileLoader;

@EnableTestLogger(debug = LoaderUtils.class)
class LoaderUtilsTest {

    @Test
    void shouldLoadExistingFiles() {
        assertFalse(LoaderUtils.loadConfigurationFromSource(EXISTING_PROPERTIES).isEmpty());
        assertFalse(LoaderUtils.loadConfigurationFromSource(EXISTING_YML).isEmpty());
    }

    @Test
    void shouldNotLoadNonExistingPropertyFile() {
        assertTrue(LoaderUtils.loadConfigurationFromSource(NOT_EXISTING_PROPERTIES).isEmpty());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.ERROR, "Portal-519");

    }

    @Test
    void shouldNotLoadNonExistingYml() {
        assertTrue(LoaderUtils.loadConfigurationFromSource(NOT_EXISTING_YML).isEmpty());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.ERROR, "Portal-519");
    }

    @Test
    void shouldCreateFromFileResources() {
        assertFalse(loadConfigurationFromSource(EXISTING_YML).isEmpty());
        assertTrue(loadConfigurationFromSource(NOT_EXISTING_YML).isEmpty());
        assertFalse(loadConfigurationFromSource(EXISTING_PROPERTIES).isEmpty());
        assertTrue(loadConfigurationFromSource(NOT_EXISTING_PROPERTIES).isEmpty());
        assertTrue(loadConfigurationFromSource((FileLoader) null).isEmpty());
        assertTrue(loadConfigurationFromSource((FileConfigurationSource) null).isEmpty());
        assertTrue(loadConfigurationFromSource(EMPTY_PATH).isEmpty());
    }
}
