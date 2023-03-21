package de.cuioss.portal.configuration.yaml;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.logging.CuiLogger;

/**
 * Loads all {@value #META_INF_LOCATION} from the provided classloader
 * {@link ConfigSourceProvider#getConfigSources(java.lang.ClassLoader)}.
 *
 * @author Sven Haag
 * @see <a href=
 *      "https://docs.oracle.com/javase/9/docs/api/java/lang/ClassLoader.html#getResources-java.lang.String-">
 *      JAVA 9 - ClassLoader#getResources</a>
 */
public class YamlDefaultConfigSourceProvider implements ConfigSourceProvider {

    private static final CuiLogger log = new CuiLogger(YamlDefaultConfigSourceProvider.class);

    private static final String ERR_MSG = "Portal-539: Could not load YAML default config";

    /** Default location for yaml microprofile-config files. */
    public static final String META_INF_LOCATION = "META-INF/microprofile-config.yaml";

    /**
     * @param classLoader
     *
     * @return a {@link YamlConfigSource} for each {@value #META_INF_LOCATION}
     */
    @Override
    public Iterable<ConfigSource> getConfigSources(final ClassLoader classLoader) {
        final var builder = new CollectionBuilder<ConfigSource>();
        final Enumeration<URL> resources;

        try {
            resources = classLoader.getResources(META_INF_LOCATION);
        } catch (final IOException e) {
            log.error(ERR_MSG, e);
            return builder.toImmutableList();
        }

        while (resources.hasMoreElements()) {
            try {
                builder.add(new YamlDefaultConfigSource(resources.nextElement()));
            } catch (final Exception e) {
                log.error(ERR_MSG, e);
            }
        }

        return builder.toImmutableList();
    }
}
