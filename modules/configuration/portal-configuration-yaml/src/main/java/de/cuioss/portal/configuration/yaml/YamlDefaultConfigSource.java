package de.cuioss.portal.configuration.yaml;

import java.net.URL;

import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.tools.io.FileTypePrefix;

/**
 * A modules default YAML
 * {@link org.eclipse.microprofile.config.spi.ConfigSource} and
 * {@link de.cuioss.portal.configuration.FileConfigurationSource} pointing to
 * <code>classpath:META-INF/microprofile-config.yaml</code>.
 *
 * <p>
 * <em>Note: You need to register your config source via SPI
 * {@link org.eclipse.microprofile.config.spi.ConfigSource}.</em>
 * </p>
 *
 * @author Sven Haag
 */
public final class YamlDefaultConfigSource extends YamlConfigSource {

    @SuppressWarnings("javadoc")
    public YamlDefaultConfigSource() {
        super(FileTypePrefix.CLASSPATH.getPrefix() + "/" + YamlDefaultConfigSourceProvider.META_INF_LOCATION);
    }

    /**
     * @param url must not be null
     */
    public YamlDefaultConfigSource(final URL url) {
        super(FileTypePrefix.URL.getPrefix() + url.toString());
    }

    /**
     * @return 110 and therefore a higher ordinal than
     *         {@code microprofile-config.properties} (100).
     */
    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_CORE_LEVEL;
    }
}
