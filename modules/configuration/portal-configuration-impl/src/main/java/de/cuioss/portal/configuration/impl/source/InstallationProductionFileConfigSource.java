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

import static de.cuioss.portal.configuration.PortalConfigurationKeys.PORTAL_CONFIG_PRODUCTION_FILENAME_DEFAULT;

/**
 * Configuration source for the application-production.yml file.
 *
 * @author Sven Haag, Sven Haag
 */
public class InstallationProductionFileConfigSource extends AbstractInstallationConfigSource {

    /** The name of this MicroProfile ConfigSource */
    public static final String NAME = "ApplicationProductionYamlConfigSource";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getFileName() {
        return PORTAL_CONFIG_PRODUCTION_FILENAME_DEFAULT;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }

    @Override
    public int getPortalPriority() {
        return super.getPortalPriority() + 5;
    }
}
