package de.cuioss.portal.configuration.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.source.ConfigurationSourceResolver;
import de.cuioss.tools.io.ClassPathLoader;

class YamlConfigurationSourceResolverTest {

    @Test
    void shouldProvideResolver() {

        var resolver = ServiceLoader.load(ConfigurationSourceResolver.class).findFirst();
        assertTrue(resolver.isPresent());

        var loader = resolver.get().resolve(new ClassPathLoader(YamlConfigSourceTest.CLASSPATH_TEST_YML));
        assertTrue(loader.isPresent());

        assertEquals(6, loader.get().getConfigurationMap().size());

    }

}
