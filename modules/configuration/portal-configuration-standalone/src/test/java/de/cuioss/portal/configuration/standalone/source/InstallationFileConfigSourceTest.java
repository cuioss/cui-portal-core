/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.standalone.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR;
import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_DIR_DEFAULT;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresentContaining;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.standalone.source.InstallationFileConfigSource;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.string.Joiner;

/**
 * @author Sven Haag
 */
@EnableTestLogger
class InstallationFileConfigSourceTest {

    @AfterEach
    void afterTest() {
        System.clearProperty(PORTAL_CONFIG_DIR);
    }

    @Test
    void resolvesPath() {
        final var underTest = new InstallationFileConfigSource();
        assertDoesNotThrow(() -> underTest.getPath());
        assertNotNull(underTest.getPath());
        assertTrue(underTest.getPath().endsWith(
                Joiner.on(File.separatorChar).join("portal-configuration-standalone", "config", "application.yml")));
        assertSingleLogMessagePresentContaining(TestLogLevel.WARN, "Portal-121");
        assertSingleLogMessagePresentContaining(TestLogLevel.WARN, "Portal-158");
    }

    @Test
    void resolvesEnvPath() {
        final var underTest = new InstallationFileConfigSource();
        System.setProperty(PORTAL_CONFIG_DIR, "target/test-classes/config/yml");
        underTest.reload();
        assertNotNull(underTest.getPath(), "path should be set");
        assertNotNull(underTest.getProperties(), "properties should never be null");
        assertFalse(underTest.getProperties().isEmpty(), "properties should be available");
    }

    @Test
    void propertiesAreNeverNull() {
        final var underTest = new InstallationFileConfigSource();
        assertNotNull(underTest.getProperties());
    }

    @Test
    void emptyConfigDirProperty() {
        System.setProperty(PORTAL_CONFIG_DIR, "");
        final var underTest = new InstallationFileConfigSource();
        underTest.reload();
        assertEquals(Paths.get(PORTAL_CONFIG_DIR_DEFAULT, "application.yml").toAbsolutePath().toString(),
                underTest.getPath());
    }
}
