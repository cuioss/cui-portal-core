package de.cuioss.portal.configuration.yaml;

import static de.cuioss.tools.collect.CollectionLiterals.immutableSet;

import java.util.Optional;
import java.util.Set;

import de.cuioss.portal.configuration.ConfigurationSource;
import de.cuioss.portal.configuration.source.ConfigurationSourceResolver;
import de.cuioss.tools.io.FileLoader;

/**
 * Resolves a {@link YamlConfigurationProvider} if available
 */
public class YamlConfigurationSourceResolver implements ConfigurationSourceResolver {

    @Override
    public Optional<ConfigurationSource> resolve(FileLoader source) {
        return YamlConfigurationProvider.createFromFile(source);
    }

    @Override
    public Set<String> supportedSuffixes() {
        return immutableSet("yml", "yaml");
    }

}
