package de.cuioss.portal.core.test.tests.configuration;

import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.source.PropertiesConfigSource;
import de.cuioss.tools.io.FileTypePrefix;

/**
 * Portal default configuration source for {@code properties} files. Per
 * default, the file must be provided under
 * {@value PropertiesDefaultConfigSource#META_INF_LOCATION}. This MicroProfile
 * {@link ConfigSource} has an ordinal of {@link ConfigSource#DEFAULT_ORDINAL} +
 * {@link PortalPriorities#PORTAL_CORE_LEVEL} per default.
 *
 * <p>
 * This is a {@link FileConfigurationSource} too, to allow testing with
 * AbstractConfigurationKeyVerifierTest.
 * </p>
 *
 * <p>
 * <em>This class is not registered via SPI</em>, and doesn't need to. The unit
 * test will only load the file to compare it against the given class containing
 * the possible config keys for that module.
 * </p>
 *
 * @author Sven Haag
 */
final public class PropertiesDefaultConfigSource extends PropertiesConfigSource {

    public static final String META_INF_LOCATION = "META-INF/microprofile-config.properties";
    public static final String CLASSPATH_LOCATION = FileTypePrefix.CLASSPATH.getPrefix() + "/" + META_INF_LOCATION;

    /**
     * Loading properties from {@link #CLASSPATH_LOCATION}. This basically is a
     * convenience constructor for unit testing a modules default config.
     */
    public PropertiesDefaultConfigSource() {
        super(CLASSPATH_LOCATION);
    }
}
