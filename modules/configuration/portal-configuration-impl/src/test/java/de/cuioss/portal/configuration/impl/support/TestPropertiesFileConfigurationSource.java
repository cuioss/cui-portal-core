package de.cuioss.portal.configuration.impl.support;

import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.source.PropertiesConfigSource;

/**
 * @author Sven Haag
 */
final public class TestPropertiesFileConfigurationSource extends PropertiesConfigSource {

    @SuppressWarnings("javadoc")
    public TestPropertiesFileConfigurationSource() {
        super("target/test-classes/config/module.properties");
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_MODULE_LEVEL;
    }
}
