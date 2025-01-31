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

import static de.cuioss.portal.configuration.util.ConfigurationHelper.*;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.easymock.EasyMock;
import org.jboss.weld.injection.EmptyInjectionPoint;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EnableAutoWeld
@EnableTestLogger
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

    @Test
    void shouldFilterEmptyMap() {
        assertTrue(getFilteredPropertyMap(Collections.emptyMap(), "", false).isEmpty());
        assertTrue(getFilteredPropertyMap(Collections.emptyMap(), "", true).isEmpty());
    }

    @Test
    void shouldNotFilterContent() {
        assertTrue(getFilteredPropertyMap(SIMPLE_MAP, NOT_THERE, false).isEmpty());
        assertTrue(getFilteredPropertyMap(SIMPLE_MAP, NOT_THERE, true).isEmpty());
    }

    @Test
    void shouldFilterContent() {
        assertEquals(1, getFilteredPropertyMap(SIMPLE_MAP, KEY1, false).size());
        assertEquals(1, getFilteredPropertyMap(SIMPLE_MAP, KEY1, true).size());
    }

    @Test
    void shouldStripPrefixes() {
        assertEquals(2, getFilteredPropertyMap(SIMPLE_MAP, "key", false).size());
        final var strippedMap = getFilteredPropertyMap(SIMPLE_MAP, "key", true);
        assertEquals(2, strippedMap.size());
        assertTrue(strippedMap.containsKey("1"));
        assertTrue(strippedMap.containsKey("2"));
    }

    @Test
    void shouldConvertToEnumIgnoringCase() {
        assertEquals(TestEnum.ONE, convertToEnum("one", TestEnum.class));
        assertEquals(TestEnum.ONE, convertToEnum(" oNe ", TestEnum.class));
    }

    @Test
    void shouldConvertToEnumUsingDefault() {
        TestLogLevel.ERROR.addLogger(ConfigurationHelper.class);
        assertEquals(TestEnum.TWO, convertToEnum("invalid", TestEnum.class, TestEnum.TWO));
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.ERROR, "Portal-512");
    }

    @Test
    void shouldNotConvertToEnumOnWrongStringInput() {
        assertThrows(IllegalArgumentException.class, () -> convertToEnum("invalid", TestEnum.class));
    }

    @Test
    void shouldNotConvertToEnumOnNullInput() {
        assertThrows(IllegalArgumentException.class, () -> convertToEnum(null, TestEnum.class));
    }

    @Test
    void shouldNotConvertToEnumOnEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> convertToEnum("", TestEnum.class));
    }

    @Test
    void shouldNotConvertToEnumOnMissingDefault() {
        assertThrows(IllegalArgumentException.class, () -> convertToEnum("test", TestEnum.class, false, null));
    }

    // Config Resolving
    @Test
    void shouldResolveConfigMap() {
        assertNotNull(resolveConfigProperties());
        assertFalse(resolveConfigProperties().isEmpty());
    }

    @Test
    void shouldResolveConfigNames() {
        assertNotNull(resolveConfigPropertyNames());
        assertFalse(resolveConfigPropertyNames().isEmpty());
    }

    @Test
    void shouldResolveFilteredProperties() {
        assertNotNull(resolveFilteredConfigProperties(KEY1));
        assertTrue(resolveFilteredConfigProperties(KEY1).isEmpty());
    }

    @Test
    void shouldResolveSingleProperty() {
        assertFalse(resolveConfigProperty(KEY1).isPresent());

        setAsSystemProperty(KEY1, "1");
        assertTrue(resolveConfigProperty(KEY1).isPresent());
        assertTrue(resolveConfigProperty(KEY1, Integer.class).isPresent());
        assertEquals(Integer.valueOf(1), resolveConfigProperty(KEY1, Integer.class).get());

        System.clearProperty(KEY1);
        assertFalse(resolveConfigProperty(KEY1).isPresent());
        assertFalse(resolveConfigProperty("").isPresent());
    }

    @Test
    void shouldResolveSinglePropertyOrThrow() {
        assertFalse(resolveConfigProperty(KEY1).isPresent());

        setAsSystemProperty(KEY1, VALUE_ONE);

        assertEquals(VALUE_ONE, resolveConfigPropertyOrThrow(KEY1));

        assertThrows(IllegalStateException.class, () -> resolveConfigPropertyOrThrow("not.there"));
    }

    @Test
    void shouldHandleInjectionPoint() {
        assertThrows(NullPointerException.class, () -> ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, null));

        assertThrows(NullPointerException.class, () -> ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, Test.class));
    }

    @Test
    void resolveAnnotationOrThrow() {
        EasyMock.expect(ip.getAnnotated()).andReturn(annotated).anyTimes();
        EasyMock.expect(annotated.getAnnotation(EasyMock.eq(Test.class))).andReturn(null);
        EasyMock.expect(annotated.getAnnotations()).andReturn(Collections.emptySet());
        EasyMock.replay(ip, annotated);

        assertThrows(IllegalStateException.class, () -> ConfigurationHelper.resolveAnnotationOrThrow(ip, Test.class));
    }

    @Test
    @SuppressWarnings("el-syntax")
    void ignoresOtherUnresolvableKeys() {
        setAsSystemProperty("a.config.key", "value");
        setAsSystemProperty("unresolvable.key", "${not.there}");

        assertDoesNotThrow(() -> resolveConfigProperty("a.config.key"));
        assertDoesNotThrow(() -> resolveConfigProperty("unresolvable.key"));
        assertDoesNotThrow(ConfigurationHelper::resolveConfigProperties);
    }

    @Test
    void configAsListWithEmptyValue() {
        setAsSystemProperty(KEY1, "");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void configAsListWithSingleValue() {
        setAsSystemProperty(KEY1, "foo");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("foo"));
    }

    @Test
    void configAsListWithSingleUntrimmedValue() {
        setAsSystemProperty(KEY1, "  foo  ,");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("foo"));
    }

    @Test
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
    void replacePlaceholders() {
        assertEquals("test", ConfigurationHelper.replacePlaceholders("test", true));
    }

    private void setAsSystemProperty(String key, String value) {
        System.setProperty(key, value);
        usedSystemConfigKeys.add(key);
    }
}
