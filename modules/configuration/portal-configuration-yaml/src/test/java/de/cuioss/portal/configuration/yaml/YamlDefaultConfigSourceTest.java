package de.cuioss.portal.configuration.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class YamlDefaultConfigSourceTest {

    @Test
    void loadModuleDefaultConfig() {
        var underTest = new YamlDefaultConfigSource();
        assertEquals("test", underTest.getProperties().get("yaml"));
        assertEquals("test", underTest.getValue("yaml"));
        assertEquals("classpath:/META-INF/microprofile-config.yaml", underTest.getPath());
        assertEquals("YamlDefaultConfigSource[source=classpath:/META-INF/microprofile-config.yaml]",
                underTest.getName());
        assertEquals(101, underTest.getOrdinal());
    }

    @Test
    void loadDefaultConfigFromUrl() {
        var result = new YamlDefaultConfigSource(getClass().getClassLoader().getResource("test.yml")).getProperties();
        assertEquals(6, result.size());
    }
}
