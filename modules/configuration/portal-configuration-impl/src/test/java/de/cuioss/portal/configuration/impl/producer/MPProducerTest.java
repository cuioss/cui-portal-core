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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.types.ConfigAsList;
import de.cuioss.test.generator.Generators;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Ensure we always get what we expect from the MicroProfile config
 * implementation.
 *
 * @author Sven Haag
 */
@EnableAutoWeld
@EnablePortalConfigurationLocal
class MPProducerTest {

    private static final CuiLogger log = new CuiLogger(MPProducerTest.class);

    private static final String KEY_BASE = "mp.producer.config.test.";
    private static final String KEY_EMPTY_DEFAULT = KEY_BASE + "emptyDefault";
    private static final String KEY_NO_DEFAULT = KEY_BASE + "noDefault";
    private static final String VALUE = Generators.nonEmptyStrings().next()
            // don't test escaping
            .replace("\\", "")
            // don't test list separator
            .replace(",", "")
            // don't test variable resolution
            .replace("$", "");
    private static final String ESCAPE_VALUE_IN = "V/J\\&LkK8+g@x^vs{Q[XfR5";
    private static final String ESCAPE_VALUE_OUT = "V/J&LkK8+g@x^vs{Q[XfR5";
    private static final String KEY_UNTRIMMED_STRING = KEY_BASE + "untrimmedString";

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @BeforeEach
    void beforeTest() {
        configuration.clear();
        configuration.initializeConfigurationSystem();
    }

    @Inject
    @ConfigProperty(name = KEY_EMPTY_DEFAULT, defaultValue = "")
    private Provider<List<String>> stringListEmptyDefaultValueProvider;

    @Inject
    @ConfigProperty(name = KEY_EMPTY_DEFAULT)
    private Provider<List<String>> stringListProvider;

    @Inject
    @ConfigProperty(name = KEY_NO_DEFAULT)
    private Provider<List<String>> stringListNoDefaultValueProvider;

    @Inject
    @ConfigProperty(name = KEY_UNTRIMMED_STRING)
    private Provider<String> untrimmedString;

    @Inject
    @ConfigAsList(name = KEY_UNTRIMMED_STRING)
    private Provider<List<String>> untrimmedList;

    @Inject
    @ConfigProperty(name = "portal.test.integer.provider")
    private Provider<Integer> integerProvider;

    @Inject
    @ConfigProperty(name = "portal.test.integer.provider", defaultValue = "b00m")
    private Provider<Integer> integerProviderWithInvalidDefault;

    @Inject
    @ConfigProperty(name = "portal.test.integer.provider", defaultValue = "0")
    private Provider<Integer> integerProviderWithDefault;

    @Inject
    @ConfigProperty(name = "portal.test.integer.optional.provider", defaultValue = "0")
    private Provider<Optional<Integer>> optionalIntegerProviderWithDefault;

    @Test
    void listEmptyDefault() {
        // empty string means: remove that property
        assertThrows(NoSuchElementException.class, () -> stringListEmptyDefaultValueProvider.get(),
                "List property with empty string as defaultValue should inject NULL");

        configuration.fireEvent(KEY_EMPTY_DEFAULT, ESCAPE_VALUE_IN);

        assertNotNull(stringListEmptyDefaultValueProvider.get(), "provider list should provide new value");
        assertFalse(stringListEmptyDefaultValueProvider.get().isEmpty(), "provider list should contain new value");
        assertEquals(ESCAPE_VALUE_OUT, stringListEmptyDefaultValueProvider.get().iterator().next());

        log.info("setting new value: {}", VALUE);
        configuration.fireEvent(KEY_EMPTY_DEFAULT, VALUE);

        assertDoesNotThrow(() -> stringListEmptyDefaultValueProvider.get(), "provider list should provide new value");
        final var newValue = stringListEmptyDefaultValueProvider.get();
        assertNotNull(newValue, "provider list should not be null");
        assertFalse(newValue.isEmpty(), "provider list should contain new value");
        assertEquals(VALUE, newValue.iterator().next());
    }

    @Test
    void stringShouldNotBeTrimmed() {
        configuration.fireEvent(KEY_UNTRIMMED_STRING, " untrimmed ");
        assertEquals(" untrimmed ", untrimmedString.get());

        configuration.fireEvent(KEY_UNTRIMMED_STRING, " ");
        assertEquals(" ", untrimmedString.get());

        configuration.fireEvent(KEY_UNTRIMMED_STRING, "");
        assertThrows(NoSuchElementException.class, untrimmedString::get);
    }

    @Test
    void listShouldBeTrimmed() {
        configuration.fireEvent(KEY_UNTRIMMED_STRING, "a,,, b , c ");
        assertEquals(3, untrimmedList.get().size());
        assertEquals("a", untrimmedList.get().get(0));
        assertEquals("b", untrimmedList.get().get(1));
        assertEquals("c", untrimmedList.get().get(2));
    }

    @Test
    void listNoDefault() {
        assertThrows(NoSuchElementException.class, () -> stringListNoDefaultValueProvider.get(),
                "List property without defaultValue should result in empty collection but is null");
    }

    @Test
    void shouldInjectIntegerWithoutNPE() {
        assertThrows(NoSuchElementException.class, () -> integerProvider.get());
        assertThrows(IllegalArgumentException.class, () -> integerProviderWithInvalidDefault.get());
        assertDoesNotThrow(() -> integerProviderWithDefault.get());

        assertThrows(NoSuchElementException.class, () -> integerProvider.get());
        assertNotNull(integerProviderWithDefault.get());

        assertTrue(optionalIntegerProviderWithDefault.get().isPresent());
    }
}
