package de.cuioss.portal.configuration.impl.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.tools.collect.MapBuilder;

public abstract class AbstractReloadableConfigSource implements ConfigSource, FileConfigurationSource {

    private final Map<String, String> properties = new HashMap<>();

    private final Map<String, String> reloadProperties = new HashMap<>();

    @Override
    public Map<String, String> getProperties() {
        return MapBuilder.copyFrom(properties).toImmutableMap();
    }

    public void addProperty(final String key, final String value) {
        properties.put(key, value);
    }

    public void addReloadedProperty(final String key, final String value) {
        reloadProperties.put(key, value);
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public String getValue(String key) {
        return getProperties().get(key);
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void reload() {
        properties.putAll(reloadProperties);
        reloadProperties.clear();
    }
}
