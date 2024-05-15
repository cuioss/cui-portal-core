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
package de.cuioss.portal.configuration.impl.producer;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.CONTEXT_PARAM_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Locale;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.types.ConfigAsLocale;
import de.cuioss.portal.configuration.types.ConfigAsLocaleList;
import de.cuioss.tools.string.Joiner;
import lombok.Getter;

@EnablePortalConfigurationLocal
@EnableAutoWeld
class ConfigurationProducerLocaleTest {

    private static final String CONFIGURATION_KEY = "configurationKey";

    @AfterEach
    void after() {
        configuration.clear();
        configuration.fireEvent();
    }

    @Inject
    @Getter
    private PortalConfigProducer underTest;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @Inject
    @ConfigAsLocale(name = CONFIGURATION_KEY)
    private Provider<Locale> injectedLocale;

    @Inject
    @ConfigAsLocale(name = CONFIGURATION_KEY, defaultToSystem = false)
    private Provider<Locale> injectedLocaleNoDefault;

    @Inject
    @ConfigAsLocaleList(name = CONFIGURATION_KEY)
    private Provider<List<Locale>> injectedLocaleList;

    @Test
    void shouldProduceLocaleList() {
        configuration.put(CONFIGURATION_KEY,
                Joiner.on(CONTEXT_PARAM_SEPARATOR).join(Locale.GERMANY.toString(), Locale.ENGLISH.toString()));

        configuration.fireEvent();
        assertEquals(2, injectedLocaleList.get().size());
    }

    @Test
    void shouldIgnoreDuplicatesLocaleList() {
        configuration.put(CONFIGURATION_KEY, Joiner.on(CONTEXT_PARAM_SEPARATOR).join(Locale.GERMANY.toString(),
                Locale.ENGLISH.toString(), Locale.GERMANY.toString()));

        configuration.fireEvent();
        assertEquals(2, injectedLocaleList.get().size());
    }

    @Test
    void shouldProduceLocale() {
        configuration.put(CONFIGURATION_KEY, Locale.GERMANY.toString());
        configuration.fireEvent();

        assertEquals(Locale.GERMANY, injectedLocale.get());
    }

    @Test
    void shouldHandleInvalidLocaleToDefault() {
        configuration.put(CONFIGURATION_KEY, "noway");
        configuration.fireEvent();

        assertEquals(Locale.getDefault(), injectedLocale.get());
    }

    @Test
    void shouldHandleMissingLocaleToDefault() {
        assertEquals(Locale.getDefault(), injectedLocale.get());
    }

    @Test
    void shouldFailOnMissingLocale() {
        assertThrows(IllegalArgumentException.class, () -> injectedLocaleNoDefault.get());
    }

    @Test
    void shouldFailToHandleInvalidLocale() {
        configuration.put(CONFIGURATION_KEY, "noway");
        configuration.fireEvent();

        assertThrows(IllegalArgumentException.class, () -> injectedLocaleNoDefault.get());
    }
}
