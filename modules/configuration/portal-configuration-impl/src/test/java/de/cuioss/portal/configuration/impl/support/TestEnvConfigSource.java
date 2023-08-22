/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.impl.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.cuioss.tools.collect.MapBuilder;
import io.smallrye.config.common.AbstractConfigSource;
import lombok.Getter;

/**
 * Copy of SmallRye EnvConfigSource. Allows additional properties. Objective:
 * Allow addition of "environment" properties. It should eliminate calls to the
 * SmallRye EnvConfigSource. Rational: We cannot modify environment properties
 * in unit tests.
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
        // 399 is just below SysPropConfigSource, which is the highest SmallRye config
        // source
        super("TestEnvConfigSource", 399);
    }

    @Override
    public Map<String, String> getProperties() {
        return MapBuilder.copyFrom(System.getenv()).putAll(additionalProperties).toImmutableMap();
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
