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
package de.cuioss.portal.configuration.util;

import java.io.Serial;

/**
 * Exception thrown when a configuration placeholder's nesting depth exceeds
 * the maximum allowed level (5 levels).
 * 
 * <h2>Nesting Depth Example</h2>
 * <pre>
 * # Level 1
 * app.home=${env.home}
 * 
 * # Level 2
 * env.home=${user.home}
 * 
 * # Level 3
 * user.home=${system.home:/opt}
 * 
 * # Level 4
 * system.home=${default.home}
 * 
 * # Level 5 (maximum)
 * default.home=/home/user
 * 
 * # Level 6 would throw ConfigKeyNestingException
 * </pre>
 * 
 * <h2>Usage Context</h2>
 * This exception is typically thrown by {@link ConfigurationPlaceholderHelper}
 * when resolving nested placeholders in configuration values. It helps prevent
 * infinite recursion and circular dependencies in configuration resolution.
 * 
 * @author Oliver Wolff
 */
public final class ConfigKeyNestingException extends IllegalStateException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception for a configuration key that exceeds the maximum
     * nesting depth.
     * 
     * @param configKey the configuration key that caused the nesting depth violation
     */
    public ConfigKeyNestingException(String configKey) {
        super("Config key is nested too deep: " + configKey);
    }

    /**
     * Creates a new exception for a configuration key that exceeds the maximum
     * nesting depth, with a cause.
     * 
     * @param configKey the configuration key that caused the nesting depth violation
     * @param cause the underlying cause of the nesting depth violation
     */
    public ConfigKeyNestingException(String configKey, Throwable cause) {
        super("Config key is nested too deep: " + configKey, cause);
    }
}
