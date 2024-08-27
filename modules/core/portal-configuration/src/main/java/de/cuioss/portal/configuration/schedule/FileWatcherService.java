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
package de.cuioss.portal.configuration.schedule;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Oliver Wolff
 */
public interface FileWatcherService {

    /**
     * Registers one or more {@link Path}s to be watched.
     *
     * @param paths to be registered.
     */
    void register(Path... paths);

    /**
     * Unregisters one or more {@link Path}s to be watched. Not existing paths will
     * be silently ignored
     *
     * @param paths to be unregistered.
     */
    void unregister(Path... paths);

    /**
     * @return the paths currently under control of this fileWatcher
     */
    List<Path> getRegisteredPaths();
}
