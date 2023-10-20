package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.portal.configuration.PortalConfigurationKeys.MODULE_DEFAULT_CONFIG_PROPERTY_PATH;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import de.cuioss.portal.configuration.source.PropertiesConfigSource;
import de.cuioss.tools.io.UrlLoader;
import de.cuioss.tools.logging.CuiLogger;

/**
 * {@link ConfigSourceProvider} resolving all
 * {@link PropertiesDefaultConfigSource#META_INF_LOCATION}
 */
public class PropertiesDefaultConfigSourceProvider implements ConfigSourceProvider {

    private static final CuiLogger LOGGER = new CuiLogger(PropertiesDefaultConfigSourceProvider.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        var list = new ArrayList<ConfigSource>();
        try {
            var sources = forClassLoader.getResources(MODULE_DEFAULT_CONFIG_PROPERTY_PATH);
            while (sources.hasMoreElements()) {
                var url = sources.nextElement();
                LOGGER.debug("Adding module default configuration '%s'", url);
                list.add(new PropertiesConfigSource(new UrlLoader(url), false));
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        LOGGER.debug("Resolving '%s' config-sources");
        return list;
    }

}
