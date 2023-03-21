package de.cuioss.portal.configuration.impl.source;

import de.cuioss.portal.configuration.common.PortalPriorities;
import de.cuioss.portal.configuration.yaml.YamlConfigSource;

@SuppressWarnings("javadoc")
final public class FancyYamlFileConfigSource extends YamlConfigSource {

    public FancyYamlFileConfigSource() {
        super("classpath:/config/yml/application.yml");
    }

    @Override
    public int getPortalPriority() {
        return PortalPriorities.PORTAL_MODULE_LEVEL;
    }
}
