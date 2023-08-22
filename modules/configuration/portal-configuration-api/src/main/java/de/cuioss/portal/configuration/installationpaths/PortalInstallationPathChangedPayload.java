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
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Payload of a {@link PortalInstallationPathChangedEvent}. Contains an enum
 * describing which path was changed and the new path.
 *
 * @author Matthias Walliczek
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PortalInstallationPathChangedPayload {

    @Getter
    private final PortalInstallationPathType type;
    private final Path oldPath;
    private final Path newPath;

    /**
     * @return the old path
     */
    public Optional<Path> getOldPath() {
        return Optional.ofNullable(oldPath);
    }

    /**
     * @return the new path
     */
    public Optional<Path> getNewPath() {
        return Optional.ofNullable(newPath);
    }
}
