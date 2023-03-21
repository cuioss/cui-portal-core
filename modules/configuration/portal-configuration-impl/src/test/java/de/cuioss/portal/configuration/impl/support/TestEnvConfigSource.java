package de.cuioss.portal.configuration.impl.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.cuioss.tools.collect.MapBuilder;
import de.cuioss.tools.lang.SecuritySupport;
import io.smallrye.config.common.AbstractConfigSource;
import lombok.Getter;

/**
 * Copy of SmallRye EnvConfigSource.
 * Allows additional properties.
 * Objective: Allow addition of "environment" properties. It should eliminate calls to the SmallRye
 * EnvConfigSource.
 * Rational: We cannot modify environment properties in unit tests.
 *
 * @author Sven Haag
 */
public class TestEnvConfigSource extends AbstractConfigSource {

    private static final long serialVersionUID = -5057298427034467643L;

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

    @Getter
    private static final Map<String, String> additionalProperties = new HashMap<>();

    /**
     * Constructor
     */
    public TestEnvConfigSource() {
        // 399 is just below SysPropConfigSource, which is the highest SmallRye config source
        super("TestEnvConfigSource", 399);
    }

    @Override
    public Map<String, String> getProperties() {
        return MapBuilder
                .copyFrom(SecuritySupport.accessSystemEnv())
                .putAll(additionalProperties)
                .toImmutableMap();
    }

    @Override
    public Set<String> getPropertyNames() {
        return additionalProperties.keySet();
    }

    @Override
    public String getValue(final String name) {
        if (name == null) {
            return null;
        }

        final var properties = getProperties();

        // exact match
        var value = properties.get(name);
        if (value != null) {
            return value;
        }

        // replace non-alphanumeric characters by underscores
        final var sanitizedName = PATTERN.matcher(name).replaceAll("_");

        value = properties.get(sanitizedName);
        if (value != null) {
            return value;
        }

        // replace non-alphanumeric characters by underscores and convert to uppercase
        return properties.get(sanitizedName.toUpperCase());
    }
}
