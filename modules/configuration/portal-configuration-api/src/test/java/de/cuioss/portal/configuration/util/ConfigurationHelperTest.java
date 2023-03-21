package de.cuioss.portal.configuration.util;

import static de.cuioss.portal.configuration.util.ConfigurationHelper.convertToEnum;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.getFilteredPropertyMap;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperties;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigProperty;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigPropertyNames;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveConfigPropertyOrThrow;
import static de.cuioss.portal.configuration.util.ConfigurationHelper.resolveFilteredConfigProperties;
import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.easymock.EasyMock;
import org.jboss.weld.injection.EmptyInjectionPoint;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;

@EnableAutoWeld
@EnableTestLogger
class ConfigurationHelperTest {

    private static final String KEY1 = "key1";
    private static final String NOT_THERE = "NotThere";
    private static final String VALUE_ONE = "1";
    private static final Map<String, String> SIMPLE_MAP = immutableMap(KEY1, "value1", "key2", "value2");

    private final Set<String> usedSystemConfigKeys = new HashSet<>();

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
        assertTrue(strippedMap.keySet().contains("1"));
        assertTrue(strippedMap.keySet().contains("2"));
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
        assertThrows(IllegalArgumentException.class, () -> {
            convertToEnum("invalid", TestEnum.class);
        });
    }

    @Test
    void shouldNotConvertToEnumOnNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            convertToEnum(null, TestEnum.class);
        });
    }

    @Test
    void shouldNotConvertToEnumOnEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            convertToEnum("", TestEnum.class);
        });
    }

    @Test
    void shouldNotConvertToEnumOnMissingDefault() {
        assertThrows(IllegalArgumentException.class, () -> {
            convertToEnum("test", TestEnum.class, false, null);
        });
    }

    // Config Resolving
    @Test
    void shouldResolveConfigMap() {
        assertNotNull(resolveConfigProperties());
        assertFalse(resolveConfigProperties().isEmpty());
        setProperty(KEY1, KEY1);
        assertTrue(resolveConfigProperties().containsKey(KEY1));
    }

    @Test
    void shouldResolveConfigNames() {
        assertNotNull(resolveConfigPropertyNames());
        assertFalse(resolveConfigPropertyNames().isEmpty());
        setProperty(KEY1, KEY1);
        assertTrue(resolveConfigPropertyNames().contains(KEY1));
    }

    @Test
    void shouldResolveFilteredProperties() {
        assertNotNull(resolveFilteredConfigProperties(KEY1));
        assertTrue(resolveFilteredConfigProperties(KEY1).isEmpty());
        setProperty(KEY1, KEY1);
        assertFalse(resolveFilteredConfigProperties(KEY1).isEmpty());
    }

    @Test
    void shouldResolveSingleProperty() {
        assertFalse(resolveConfigProperty(KEY1).isPresent());

        setProperty(KEY1, "1");
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

        setProperty(KEY1, VALUE_ONE);

        assertEquals(VALUE_ONE, resolveConfigPropertyOrThrow(KEY1));

        assertThrows(IllegalStateException.class, () -> {
            resolveConfigPropertyOrThrow("not.there");
        });
    }

    @Test
    void shouldHandleInjectionPoint() {
        assertThrows(NullPointerException.class, () -> {
            ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, null);
        });

        assertThrows(NullPointerException.class, () -> {
            ConfigurationHelper.resolveAnnotation(EmptyInjectionPoint.INSTANCE, Test.class).isPresent();
        });
    }

    InjectionPoint ip = EasyMock.mock(InjectionPoint.class);
    Annotated annotated = EasyMock.mock(Annotated.class);

    @Test
    void resolveAnnotationOrThrow() {
        EasyMock.expect(ip.getAnnotated()).andReturn(annotated).anyTimes();
        EasyMock.expect(annotated.getAnnotation(EasyMock.eq(Test.class))).andReturn(null);
        EasyMock.expect(annotated.getAnnotations()).andReturn(Collections.emptySet());
        EasyMock.replay(ip, annotated);

        assertThrows(IllegalStateException.class, () -> ConfigurationHelper.resolveAnnotationOrThrow(ip, Test.class));
    }

    @Test
    void ignoresOtherUnresolvableKeys() {
        setProperty("a.config.key", "value");
        setProperty("unresolvable.key", "${not.there}");

        assertDoesNotThrow(() -> resolveConfigProperty("a.config.key"));
        assertDoesNotThrow(() -> resolveConfigProperty("unresolvable.key"));
        assertDoesNotThrow(ConfigurationHelper::resolveConfigProperties);
    }

    @Test
    void configAsListWithEmptyValue() {
        setProperty(KEY1, "");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void configAsListWithSingleValue() {
        setProperty(KEY1, "foo");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("foo"));
    }

    @Test
    void configAsListWithSingleUntrimmedValue() {
        setProperty(KEY1, "  foo  ,");
        var result = ConfigurationHelper.resolveConfigPropertyAsList(KEY1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("foo"));
    }

    @Test
    void configAsListWithMultipleValues() {
        setProperty(KEY1, "  foo  , bar  , b a z ");
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

    private void setProperty(String key, String value) {
        System.setProperty(key, value);
        usedSystemConfigKeys.add(key);
    }
}
