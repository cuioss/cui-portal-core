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
package de.cuioss.portal.configuration.util;

import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.easymock.EasyMock;
import org.jboss.weld.injection.EmptyInjectionPoint;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ConfigurationHelper} verifying configuration handling and edge cases.
 */
@EnableAutoWeld
@EnableTestLogger
@DisplayName("ConfigurationHelper Tests")
class ConfigurationHelperTest {

    private static final String KEY1 = "key1";
    private static final String NOT_THERE = "NotThere";
    private static final String VALUE_ONE = "1";
    private static final Map<String, String> SIMPLE_MAP = immutableMap(KEY1, "value1", "key2", "value2");

    private final Set<String> usedSystemConfigKeys = new HashSet<>();

    final InjectionPoint ip = EasyMock.mock(InjectionPoint.class);
    final Annotated annotated = EasyMock.mock(Annotated.class);

    enum TestEnum {
        ONE, TWO
    }

    @AfterEach
    void afterTest() {
        usedSystemConfigKeys.forEach(System::clearProperty);
    }

    @Nested
    @DisplayName("Map Filtering Tests")
    class MapFilteringTests {

        @Test
        @DisplayName("Should handle empty map")
        void shouldFilterEmptyMap() {
            assertTrue(ConfigurationHelper.getFilteredPropertyMap(Collections.emptyMap(), "", false).isEmpty());
            assertTrue(ConfigurationHelper.getFilteredPropertyMap(Collections.emptyMap(), "", true).isEmpty());
        }

        @Test
        @DisplayName("Should handle non-matching filter")
        void shouldNotFilterContent() {
            assertTrue(ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, NOT_THERE, false).isEmpty());
            assertTrue(ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, NOT_THERE, true).isEmpty());
        }

        @Test
        @DisplayName("Should filter content correctly")
        void shouldFilterContent() {
            assertEquals(1, ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, KEY1, false).size());
            assertEquals(1, ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, KEY1, true).size());
        }

        @Test
        @DisplayName("Should strip prefixes correctly")
        void shouldStripPrefixes() {
            assertEquals(2, ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, "key", false).size());
            final var strippedMap = ConfigurationHelper.getFilteredPropertyMap(SIMPLE_MAP, "key", true);
            assertEquals(2, strippedMap.size());
            assertTrue(strippedMap.containsKey("1"));
            assertTrue(strippedMap.containsKey("2"));
        }
    }

    @Nested
    @DisplayName("Enum Conversion Tests")
    class EnumConversionTests {

        @Test
        @DisplayName("Should handle case-insensitive enum conversion")
        void shouldConvertToEnumIgnoringCase() {
            assertEquals(TestEnum.ONE, ConfigurationHelper.convertToEnum("one", TestEnum.class));
            assertEquals(TestEnum.ONE, ConfigurationHelper.convertToEnum(" oNe ", TestEnum.class));
        }

        @Test
        @DisplayName("Should use default value and log error")
        void shouldConvertToEnumUsingDefault() {
            TestLogLevel.ERROR.addLogger(ConfigurationHelper.class);
            assertEquals(TestEnum.TWO, ConfigurationHelper.convertToEnum("invalid", TestEnum.class, TestEnum.TWO));
            assertLogMessagePresentContaining(TestLogLevel.ERROR, "Portal-512");
        }

        @Test
        @DisplayName("Should handle invalid enum input")
        void shouldNotConvertToEnumOnWrongStringInput() {
            assertThrows(IllegalArgumentException.class, () -> ConfigurationHelper.convertToEnum("invalid", TestEnum.class));
        }

        @Test
        @DisplayName("Should handle null enum input")
        void shouldNotConvertToEnumOnNullInput() {
            assertThrows(IllegalArgumentException.class, () -> ConfigurationHelper.convertToEnum(null, TestEnum.class));
        }

        @Test
        @DisplayName("Should handle empty enum input")
        void shouldNotConvertToEnumOnEmptyInput() {
            assertThrows(IllegalArgumentException.class, () -> ConfigurationHelper.convertToEnum("", TestEnum.class));
        }

        @Test
        @DisplayName("Should handle missing default value")
        void shouldNotConvertToEnumOnMissingDefault() {
            assertThrows(IllegalArgumentException.class, () -> ConfigurationHelper.convertToEnum("test", TestEnum.class, false, null));
        }
    }

    @Nested
    @DisplayName("Configuration Resolution Tests")
    class ConfigurationResolutionTests {

        @Test
        @DisplayName("Should resolve configuration map")
        void shouldResolveConfigMap() {
            assertNotNull(ConfigurationHelper.resolveConfigProperties());
            assertFalse(ConfigurationHelper.resolveConfigProperties().isEmpty());
        }

        @Test
        @DisplayName("Should resolve configuration names")
        void shouldResolveConfigNames() {
            assertNotNull(ConfigurationHelper.resolveConfigPropertyNames());
            assertFalse(ConfigurationHelper.resolveConfigPropertyNames().isEmpty());
        }

        @Test
        @DisplayName("Should resolve filtered properties")
        void shouldResolveFilteredProperties() {
            assertNotNull(ConfigurationHelper.resolveFilteredConfigProperties(KEY1));
            assertTrue(ConfigurationHelper.resolveFilteredConfigProperties(KEY1).isEmpty());
        }

        @Test
        @DisplayName("Should resolve single property")
        void shouldResolveSingleProperty() {
            assertFalse(ConfigurationHelper.resolveConfigProperty(KEY1).isPresent());

            setAsSystemProperty(KEY1, "1");
            assertTrue(ConfigurationHelper.resolveConfigProperty(KEY1).isPresent());
            assertTrue(ConfigurationHelper.resolveConfigProperty(KEY1, Integer.class).isPresent());
            assertEquals(Integer.valueOf(1), ConfigurationHelper.resolveConfigProperty(KEY1, Integer.class).get());

            System.clearProperty(KEY1);
            assertFalse(ConfigurationHelper.resolveConfigProperty(KEY1).isPresent());
            assertFalse(ConfigurationHelper.resolveConfigProperty("").isPresent());
        }

        @Test
        @DisplayName("Should resolve single property or throw")
        void shouldResolveSinglePropertyOrThrow() {
            assertFalse(ConfigurationHelper.resolveConfigProperty(KEY1).isPresent());

            setAsSystemProperty(KEY1, VALUE_ONE);
            assertEquals(VALUE_ONE, ConfigurationHelper.resolveConfigPropertyOrThrow(KEY1));
            assertThrows(IllegalStateException.class, () -> ConfigurationHelper.resolveConfigPropertyOrThrow("not.there"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle injection point")
        void shouldHandleInjectionPoint() {
            assertThrows(NullPointerException.class, () -> ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, null));
            assertThrows(NullPointerException.class, () -> ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, Test.class));
        }

        @Test
        @DisplayName("Should handle missing annotation")
        void resolveAnnotationOrThrow() {
            EasyMock.expect(ip.getAnnotated()).andReturn(annotated).anyTimes();
            EasyMock.expect(annotated.getAnnotation(EasyMock.eq(Test.class))).andReturn(null);
            EasyMock.expect(annotated.getAnnotations()).andReturn(Collections.emptySet());
            EasyMock.replay(ip, annotated);

            assertThrows(IllegalStateException.class, () -> ConfigurationHelper.resolveAnnotationOrThrow(ip, Test.class));
        }

        @Test
        @DisplayName("Should ignore unresolvable keys")
        @SuppressWarnings("el-syntax")
        void ignoresOtherUnresolvableKeys() {
            setAsSystemProperty("a.config.key", "value");
            setAsSystemProperty("unresolvable.key", "${not.there}");

            assertDoesNotThrow(() -> ConfigurationHelper.resolveConfigProperty("a.config.key"));
            assertDoesNotThrow(() -> ConfigurationHelper.resolveConfigProperty("unresolvable.key"));
            assertDoesNotThrow(ConfigurationHelper::resolveConfigProperties);
        }

        @Test
        @DisplayName("Should handle empty list value")
        void configAsListWithEmptyValue() {
            setAsSystemProperty(KEY1, "");
            var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle single list value")
        void configAsListWithSingleValue() {
            setAsSystemProperty(KEY1, "foo");
            var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        @DisplayName("Should handle single untrimmed list value")
        void configAsListWithSingleUntrimmedValue() {
            setAsSystemProperty(KEY1, "  foo  ,");
            var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        @DisplayName("Should handle multiple list values")
        void configAsListWithMultipleValues() {
            setAsSystemProperty(KEY1, "  foo  , bar  , b a z ");
            var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.contains("foo"));
            assertTrue(result.contains("bar"));
            assertTrue(result.contains("b a z"));
        }

        @Test
        @DisplayName("Should replace placeholders")
        void replacePlaceholders() {
            assertEquals("test", ConfigurationHelper.replacePlaceholders("test", true));
        }
    }

    private void setAsSystemProperty(String key, String value) {
        System.setProperty(key, value);
        usedSystemConfigKeys.add(key);
    }
}
