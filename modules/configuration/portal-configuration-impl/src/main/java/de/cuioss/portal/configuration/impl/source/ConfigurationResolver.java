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
package de.cuioss.portal.configuration.impl.source;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import de.cuioss.portal.configuration.util.ConfigurationHelper;

/**
 * Expose the application configuration as resource bundle to allow EL
 * expressions like
 *
 * <pre>
 * #{configuration['xzy']}
 * </pre>
 *
 * .
 *
 * @author Matthias Walliczek
 *
 */
@Named("configuration")
@Dependent
public class ConfigurationResolver extends ResourceBundle {

    @Override
    protected Object handleGetObject(final String key) {
        return ConfigurationHelper.resolveConfigProperty(key).orElse("Undefined");
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(ConfigurationHelper.resolveConfigPropertyNames());
    }

}
