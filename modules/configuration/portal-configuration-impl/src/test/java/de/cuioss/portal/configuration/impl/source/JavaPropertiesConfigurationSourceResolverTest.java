package de.cuioss.portal.configuration.impl.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ServiceLoader;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.impl.support.TestResourcesRepository;
import de.cuioss.portal.configuration.source.ConfigurationSourceResolver;
import de.cuioss.tools.io.ClassPathLoader;

class JavaPropertiesConfigurationSourceResolverTest {

    @Test
    @Disabled
    void shouldProvideResolver() {

        var resolver = ServiceLoader.load(ConfigurationSourceResolver.class).findFirst();
        assertTrue(resolver.isPresent());

        var loader = resolver.get().resolve(new ClassPathLoader(TestResourcesRepository.EXISTING_PROPERTIES.getPath()));
        assertTrue(loader.isPresent());

        assertEquals(1, loader.get().getConfigurationMap().size());

    }

}
