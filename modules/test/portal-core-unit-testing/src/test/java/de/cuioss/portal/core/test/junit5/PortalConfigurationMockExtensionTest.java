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
package de.cuioss.portal.core.test.junit5;

import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        configuration.update(PORTAL_CUSTOMIZATION_ENABLED, "false");
        assertTrue(attributeMpProvider.get());
    }
}
