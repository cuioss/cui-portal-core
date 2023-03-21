package de.cuioss.portal.configuration.yaml;

import static de.cuioss.portal.configuration.yaml.TestResourcesRepository.EMPTY_PATH;
import static de.cuioss.portal.configuration.yaml.TestResourcesRepository.EXISTING_PROPERTIES;
import static de.cuioss.portal.configuration.yaml.TestResourcesRepository.EXISTING_YML;
import static de.cuioss.portal.configuration.yaml.TestResourcesRepository.NOT_EXISTING_PROPERTIES;
import static de.cuioss.portal.configuration.yaml.TestResourcesRepository.NOT_EXISTING_YML;
import static de.cuioss.portal.configuration.yaml.YamlConfigurationProvider.createFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.scanner.ScannerException;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.UrlLoader;

/**
 * Tests for {@link YamlConfigurationProvider}.
 *
 * COPIED FROM:
 * https://github.com/spring-projects/spring-framework/blob/v5.1.8
 * .RELEASE/spring-beans/src/test/java/org/springframework/beans/factory/config/YamlPropertiesFactoryBeanTests.java
 * https://github.com/spring-projects/spring-framework/blob/v5.1.8
 * .RELEASE/spring-beans/src/test/java/org/springframework/beans/factory/config/YamlProcessorTests.java
 */
@EnableTestLogger
class YamlConfigurationProviderTests {

    @Test
    void loadResource() {
        final var properties = getPropertiesFrom("foo: bar\nspam:\n  foo: baz");
        assertEquals("bar", properties.getProperty("foo"));
        assertEquals("baz", properties.getProperty("spam.foo"));
    }

    @Test
    void loadResourcesWithOverride() {
        final var properties = getPropertiesFrom("foo: bar\nspam:\n  foo: baz\nfoo:\n  bar: spam");
        assertNull(properties.getProperty("foo"));
        assertEquals("baz", properties.getProperty("spam.foo"));
        assertEquals("spam", properties.getProperty("foo.bar"));
    }

    /**
     * Should not throw DuplicateKeyException because
     * org.yaml.snakeyaml.LoaderOptions.allowDuplicateKeys=true
     */
    @Test
    void loadResourcesWithInternalOverride() {
        assertNotNull(getPropertiesFrom("foo: bar\nspam:\n  foo: baz\nfoo: bucket"));
    }

    /**
     * Should not throw DuplicateKeyException because
     * org.yaml.snakeyaml.LoaderOptions.allowDuplicateKeys=true
     */
    @Test
    void loadResourcesWithNestedInternalOverride() {
        assertNotNull(getPropertiesFrom("foo:\n  bar: spam\n  foo: baz\nbreak: it\nfoo: bucket"));
    }

    @Test
    void loadResourceWithMultipleDocuments() {
        final var properties = getPropertiesFrom("foo: bar\nspam: baz\n---\nfoo: bag");
        assertEquals("bag", properties.getProperty("foo"));
        assertEquals("baz", properties.getProperty("spam"));
    }

    @Test
    void loadResourceWithDefaultMatch() {
        final var properties = getPropertiesFrom("one: two\n---\nfoo: bar\nspam: baz\n---\nfoo: bag\nspam: bad");
        assertEquals("bag", properties.getProperty("foo"));
        assertEquals("bad", properties.getProperty("spam"));
        assertEquals("two", properties.getProperty("one"));
    }

    @Test
    void loadNull() {
        final var properties = getPropertiesFrom("foo: bar\nspam:");
        assertEquals("bar", properties.getProperty("foo"));
        assertEquals("", properties.getProperty("spam"));
    }

    @Test
    void loadEmptyArrayValue() {
        final var properties = getPropertiesFrom("a: alpha\ntest: []");
        assertEquals("alpha", properties.getProperty("a"));
        assertNull(properties.getProperty("test")); // instead of ""
    }

    @Test
    void loadArrayOfString() {
        final var properties = getPropertiesFrom("foo:\n- bar\n- baz");
        assertEquals("bar", properties.getProperty("foo[0]"));
        assertEquals("baz", properties.getProperty("foo[1]"));
        assertNull(properties.get("foo"));
    }

    @Test
    void loadArrayOfInteger() {
        final var properties = getPropertiesFrom("foo:\n- 1\n- 2");
        assertEquals("1", properties.getProperty("foo[0]"));
        assertEquals("2", properties.getProperty("foo[1]"));
        assertNull(properties.get("foo"));
    }

    @Test
    void loadArrayOfObject() {
        final var properties =
            getPropertiesFrom("foo:\n- bar:\n    spam: crap\n- baz\n- one: two\n  three: four");
        assertEquals("crap", properties.getProperty("foo[0].bar.spam"));
        assertEquals("baz", properties.getProperty("foo[1]"));
        assertEquals("two", properties.getProperty("foo[2].one"));
        assertEquals("four", properties.getProperty("foo[2].three"));
        assertNull(properties.get("foo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void yaml() {
        final var yaml = new Yaml();
        final Map<String, ?> map = yaml.loadAs("foo: bar\nspam:\n  foo: baz", Map.class);
        assertEquals("bar", map.get("foo"));
        assertEquals("baz", ((Map<String, Object>) map.get("spam")).get("foo"));
    }

    @Test
    void arrayConvertedToIndexedBeanReference() {
        final var properties = getPropertiesFrom("foo: bar\nbar: [1,2,3]");
        assertEquals(4, properties.size());
        assertEquals("bar", properties.get("foo"));
        assertEquals("bar", properties.getProperty("foo"));
        assertEquals(1, properties.get("bar[0]"));
        assertEquals("1", properties.getProperty("bar[0]"));
        assertEquals(2, properties.get("bar[1]"));
        assertEquals("2", properties.getProperty("bar[1]"));
        assertEquals(3, properties.get("bar[2]"));
        assertEquals("3", properties.getProperty("bar[2]"));
    }

    @Test
    void stringResource() {
        final var properties = getPropertiesFrom("foo # a document that is a literal");
        assertEquals("foo", properties.get("document"));
    }

    @Test
    void badDocumentStart() {
        var provider = new YamlConfigurationProvider(
                new StringResource("foo # a document\nbar: baz"));
        assertThrows(ParserException.class, () -> provider.createProperties());
    }

    @Test
    void badResource() {
        var provider = new YamlConfigurationProvider(
                new StringResource("foo: bar\ncd\nspam:\n  foo: baz"));
        assertThrows(ScannerException.class, () -> provider.createProperties());
    }

    @Test
    void badResource2() {
        var provider = new YamlConfigurationProvider(
                new StringResource("foo: bar\ncd\nspam:\n  foo: baz"));
        assertThrows(ScannerException.class, () -> provider
                .createProperties());
    }

    @Test
    void mapConvertedToIndexedBeanReference() {
        final var properties = getPropertiesFrom("foo: bar\nbar:\n spam: bucket");
        assertEquals("bucket", properties.get("bar.spam"));
        assertEquals(2, properties.size());
    }

    @Test
    void integerKeyBehaves() {
        final var properties = getPropertiesFrom("foo: bar\n1: bar");
        assertEquals("bar", properties.get("[1]"));
        assertEquals(2, properties.size());
    }

    @Test
    void integerDeepKeyBehaves() {
        final var properties = getPropertiesFrom("foo:\n  1: bar");
        assertEquals("bar", properties.get("foo[1]"));
        assertEquals(1, properties.size());
    }

    @Test
    void returnAsConfigMap() {
        var result = new YamlConfigurationProvider(
                new StringResource("foo: bar"))
                        .getConfigurationMap();
        Map<String, String> expected = new HashMap<>();
        expected.put("foo", "bar");
        assertEquals(expected, result);
    }

    @Test
    void loadFromPath() {
        var result = new YamlConfigurationProvider("classpath:/test.yml")
                .getConfigurationMap();
        assertEquals(6, result.size());
    }

    private Properties getPropertiesFrom(final String data) {
        try {
            return new YamlConfigurationProvider(new StringResource(data)).createProperties();
        } catch (final Exception e) {
            fail(e);
        }
        fail("Could not load YAML from data: " + data);
        return new Properties();
    }

    @Test
    void shouldCreateFromFileResources() {
        assertTrue(createFromFile(EXISTING_YML).isPresent());
        assertFalse(createFromFile(NOT_EXISTING_YML).isPresent());
        assertFalse(createFromFile(EXISTING_PROPERTIES).isPresent());
        assertFalse(createFromFile(NOT_EXISTING_PROPERTIES).isPresent());
        assertFalse(createFromFile((FileLoader) null).isPresent());
        assertFalse(createFromFile((FileConfigurationSource) null).isPresent());
        assertFalse(createFromFile(EMPTY_PATH).isPresent());
    }

    @Test
    void loadFromUrl() {
        TestLoggerFactory.addLogger(TestLogLevel.DEBUG, YamlConfigurationProvider.class.getName());
        var url = YamlConfigurationProviderTests.class.getResource("/test.yml");
        var result = new YamlConfigurationProvider(new UrlLoader(url)).getConfigurationMap();
        assertNotNull(result);
        assertEquals(6, result.size());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.DEBUG, "Loading YAML from: UrlLoader(url=file:");
    }
}
