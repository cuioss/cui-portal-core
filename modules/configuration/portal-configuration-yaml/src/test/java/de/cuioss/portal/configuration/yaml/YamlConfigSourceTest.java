package de.cuioss.portal.configuration.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class YamlConfigSourceTest {

    @Test
    void loadData() {
        final var underTest = new YamlConfigSource("classpath:/test.yml");
        assertEquals(6, underTest.getProperties().size());
    }

    @Test
    void loadUnavailableFile() {
        assertThrows(IllegalArgumentException.class, () -> new YamlConfigSource("classpath:/b00m.yml"));
    }

    @Test
    void loadUnavailableFileAsOptionalConfigSource() {
        final var underTest = new YamlConfigSource("classpath:/b00m.yml", true);
        assertEquals(0, underTest.getProperties().size());
    }
}
