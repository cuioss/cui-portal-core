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
package de.cuioss.portal.configuration.installationpaths;

import java.nio.file.Path;

/**
 * Describing the type of installation path that was changed.
 *
 * @author Matthias Walliczek
 */
public enum PortalInstallationPathType {

    /** The customization directory was changed */
    CUSTOMIZATION_DIRECTORY;

    /**
     * Simple Helper method for creating
     * {@link PortalInstallationPathChangedPayload}s
     *
     * @param oldPath may be null
     * @param newPath may be null
     *
     * @return {@link PortalInstallationPathChangedPayload} with the given path
     */
    public PortalInstallationPathChangedPayload ofPath(Path oldPath, Path newPath) {
        return new PortalInstallationPathChangedPayload(this, oldPath, newPath);
    }
}
