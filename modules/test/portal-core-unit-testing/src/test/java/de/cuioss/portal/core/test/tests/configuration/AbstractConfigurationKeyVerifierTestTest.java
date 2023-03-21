package de.cuioss.portal.core.test.tests.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.Getter;

class AbstractConfigurationKeyVerifierTestTest extends AbstractConfigurationKeyVerifierTest {

    static final String PATH1 = "classpath:/META-INF/mock-configuration.yml";
    static final String PATH2 = "classpath:/META-INF/mock-configuration2.yml";
    static final String PATH3 = "classpath:/META-INF/mock-configuration3.yml";

    @Getter
    private MockConfigurationSource underTest;

    @Getter
    private List<String> keysIgnoreList;

    @Getter
    private List<String> configurationKeysIgnoreList;

    @BeforeEach
    void before() {
        underTest = new MockConfigurationSource(PATH1);

        keysIgnoreList = new ArrayList<>();
        configurationKeysIgnoreList = new ArrayList<>();
    }

    @Override
    public Class<?> getKeyHolder() {
        return ConfigurationKeys.class;
    }

    @Test
    void shouldDetectMissingKeyInConfiguration() {
        underTest = new MockConfigurationSource(PATH3);
        // TODO super.configurationKeysShouldReverseMapToKeyNames();
    }

    @Test
    void shouldDetectMissingKeyInKeyHolder() {
        underTest = new MockConfigurationSource(PATH2);
        assertThrows(AssertionError.class, () -> {
            super.configurationKeysShouldReverseMapToKeyNames();
        });
    }

    @Test
    void shouldBlackListKey() {
        assertEquals(4, super.resolveKeyNames().size());
        keysIgnoreList.add(ConfigurationKeys.KEY_1);
        assertEquals(3, super.resolveKeyNames().size());
    }

    @Test
    void shouldFilterConfigurationKey() {
        assertEquals(4, super.resolveConfigurationKeyNames().size());
        configurationKeysIgnoreList.add("de");
        assertEquals(1, super.resolveConfigurationKeyNames().size());
    }
}
