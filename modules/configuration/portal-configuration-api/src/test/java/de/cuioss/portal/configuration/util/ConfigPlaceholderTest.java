package de.cuioss.portal.configuration.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfigPlaceholderTest {

    @Test
    void npeIfConfigKeyIsNull() {
        assertThrows(NullPointerException.class, () -> new ConfigPlaceholder(null));
        assertThrows(NullPointerException.class, () -> new ConfigPlaceholder(null, null));
    }

    @Test
    void keyOnlyConstructorSetsDefaultValueToNull() {
        var nullDefault = new ConfigPlaceholder("key").getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isEmpty());
    }

    @Test
    void returnsOptionalEmptyDefaultValueIfNull() {
        var nullDefault = new ConfigPlaceholder("key", null).getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isEmpty());
    }

    @Test
    void returnsDefaultValueIfNotNull() {
        var nullDefault = new ConfigPlaceholder("key", "default").getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isPresent());
        assertEquals("default", nullDefault.get());
    }

    @Test
    void verifyInvalidPlaceholders() {
        assertFalse(ConfigPlaceholder.isPlaceholder(null));
        assertFalse(ConfigPlaceholder.isPlaceholder(""));
        assertFalse(ConfigPlaceholder.isPlaceholder("${key"));
        assertFalse(ConfigPlaceholder.isPlaceholder("${key:default"));
        assertFalse(ConfigPlaceholder.isPlaceholder(" ${key}"));
        assertFalse(ConfigPlaceholder.isPlaceholder(" ${key:default}"));
        assertFalse(ConfigPlaceholder.isPlaceholder("${:default}"));
    }

    @Test
    void verifyValidPlaceholders() {
        assertTrue(ConfigPlaceholder.isPlaceholder("${key}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:default}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:foo:bar}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:${key2:default}crap}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:${key2}crap}"));
    }

    @Test
    void splitInvalidPlaceholderThrowsException() {
        assertThrows(NullPointerException.class, () -> ConfigPlaceholder.split(null));
        assertThrows(IllegalArgumentException.class, () -> ConfigPlaceholder.split(""));
        assertThrows(IllegalArgumentException.class, () -> ConfigPlaceholder.split(" ${key}"));
    }

    @Test
    void splitPlaceholderWithKeyOnly() {
        var result = ConfigPlaceholder.split("${key}");

        assertEquals("key", result.getConfigKey());
        assertTrue(result.getDefaultValue().isEmpty());
    }

    @Test
    void splitPlaceholderWithDefault() {
        var result = ConfigPlaceholder.split("${key:default}");

        assertEquals("key", result.getConfigKey());
        assertEquals("default", result.getDefaultValue().orElse(null));
    }

    @Test
    void splitPlaceholderWithNestedDefault() {
        var result = ConfigPlaceholder.split("${key:${key2}}");

        assertEquals("key", result.getConfigKey());
        assertEquals("${key2}", result.getDefaultValue().orElse(null));
    }

    @Test
    void splitPlaceholderWithNestedInvalidDefault() {
        var result = ConfigPlaceholder.split("${key:${key2}crap}");

        assertEquals("key", result.getConfigKey());
        assertEquals("${key2}crap", result.getDefaultValue().orElse(null));
    }

    @Test
    void splitPlaceholderWithDefaultContainingDoubleColon() {
        var result = ConfigPlaceholder.split("${key:foo:bar}");

        assertEquals("key", result.getConfigKey());
        assertEquals("foo:bar", result.getDefaultValue().orElse(null));
    }
}
