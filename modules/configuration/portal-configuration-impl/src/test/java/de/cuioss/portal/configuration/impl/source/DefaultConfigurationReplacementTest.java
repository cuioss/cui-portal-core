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
package de.cuioss.portal.configuration.impl.source;

import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.collect.CollectionLiterals;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_STAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnablePortalConfigurationLocal
@EnableAutoWeld
class DefaultConfigurationReplacementTest {

    public static final String PRODUCTION = "production";
    public static final String PROFILE = "smallrye.config.profile";

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
        assertTrue(CollectionLiterals.mutableList(ConfigProvider.getConfig().getPropertyNames()).contains(PROFILE));
        assertEquals(PRODUCTION, ConfigProvider.getConfig().getValue(PORTAL_STAGE, String.class));
    }
}
