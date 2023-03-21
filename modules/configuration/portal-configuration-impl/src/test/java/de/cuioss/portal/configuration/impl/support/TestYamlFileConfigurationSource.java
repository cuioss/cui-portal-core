package de.cuioss.portal.configuration.impl.support;

import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.yaml.YamlConfigSource;

@SuppressWarnings("javadoc")
public class TestYamlFileConfigurationSource extends YamlConfigSource {

    public TestYamlFileConfigurationSource() {
        super("target/test-classes/config/yml/module.yaml");
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_MODULE_LEVEL;
    }
}
