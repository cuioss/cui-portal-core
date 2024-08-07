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
 * Exception for a configuration key whose default value contains a placeholder
 * again for too many times.
 */
public final class ConfigKeyNestingException extends IllegalStateException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConfigKeyNestingException(String configKey) {
        super("Config key is nested too deep: " + configKey);
    }

    public ConfigKeyNestingException(String configKey, Throwable cause) {
        super("Config key is nested too deep: " + configKey, cause);
    }
}
