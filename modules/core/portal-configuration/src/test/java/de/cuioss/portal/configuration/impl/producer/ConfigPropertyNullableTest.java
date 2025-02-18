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
import static org.junit.jupiter.api.Assertions.assertNull;

import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.types.ConfigPropertyNullable;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("el-syntax")
@EnableAutoWeld
@EnablePortalConfigurationLocal
class ConfigPropertyNullableTest {

    private static final String KEY = "a.config.key";

    @Inject
    private PortalTestConfigurationLocal configuration;

    @Inject
    @ConfigPropertyNullable(name = KEY)
    private Provider<String> value;

    @BeforeEach
    void beforeTest() {
        configuration.clear();
    }

    @Nested
    class NullabilityTests {
        @Test
        void nullForUnknownKey() {
            assertDoesNotThrow(() -> value.get());
            assertNull(value.get());
        }

        @Test
        void nullForEmptyDefault() {
            configuration.fireEvent(KEY, "${not.there:}");
            assertDoesNotThrow(() -> value.get(), "should use empty default value");
            assertNull(value.get());
        }

        @Test
        void nullForUnresolvableVariable() {
            configuration.fireEvent(KEY, "${not.there}");
            assertDoesNotThrow(() -> value.get(), "should ignore unresolvable key");
            assertNull(value.get());
        }
    }
}
