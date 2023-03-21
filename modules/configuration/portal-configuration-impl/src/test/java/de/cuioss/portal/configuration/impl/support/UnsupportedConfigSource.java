package de.cuioss.portal.configuration.impl.support;

import java.util.Map;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.source.PropertiesConfigSource;

/**
 * Representing a {@linkplain FileConfigurationSource} to an existing file that is not supported.
 *
 * @author Sven Haag
 */
final public class UnsupportedConfigSource extends PropertiesConfigSource {

    @SuppressWarnings("javadoc")
    public UnsupportedConfigSource() {
        super("file:target/test-classes/config/supported.not");
    }

    @Override
    public Map<String, String> getProperties() {
        throw new IllegalStateException(
                "this should not be called as the file cannot be loaded in the first place");
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_MODULE_LEVEL;
    }
}
