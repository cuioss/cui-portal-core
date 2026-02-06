/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.impl.source;

import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnablePortalConfigurationLocal
@EnableAutoWeld
class DefaultConfigurationReplacementTest {

    public static final String PRODUCTION = "production";
    public static final String PROFILE = "smallrye.config.profile";

    @Inject
    private PortalTestConfigurationLocal configuration;

    @BeforeEach
    void setUp() {
        configuration.clear();
    }

    @Test
    void shouldProvideConfiguration() {
        assertTrue(ConfigurationHelper.resolveConfigProperties().containsKey(PORTAL_STAGE));
        assertEquals(PRODUCTION, ConfigurationHelper.resolveConfigProperty(PORTAL_STAGE).get());
    }

    @Test
    void shouldProvideReplacedConfiguration() {
        assertTrue(ConfigurationHelper.resolveConfigProperties().containsKey(PROFILE));
        assertEquals(PRODUCTION, ConfigurationHelper.resolveConfigProperty(PORTAL_STAGE).get());
    }

    @Test
    void shouldProvideReplacedConfigurationFromMPConfig() {
        // Collect property names from individual config sources, as SmallRye Config 3.16.0
        // getPropertyNames() iterator may throw NoSuchElementException during initialization
        var allNames = new TreeSet<String>();
        for (var source : ConfigProvider.getConfig().getConfigSources()) {
            source.getPropertyNames().forEach(allNames::add);
        }
        assertTrue(allNames.contains(PROFILE));
        assertEquals(PRODUCTION, ConfigProvider.getConfig().getValue(PORTAL_STAGE, String.class));
    }
}
