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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * CDI qualifier for the Portal's implementation of {@link FileWatcherService}.
 * This qualifier is used to identify the specific implementation that handles
 * file system monitoring for the Portal configuration system.
 * <p>
 * Usage:
 * <pre>
 * &#64;Inject
 * &#64;PortalFileWatcherService
 * private FileWatcherService watcher;
 * </pre>
 * <p>
 * The qualified implementation provides:
 * <ul>
 *   <li>Integration with Portal's configuration system</li>
 *   <li>Configurable monitoring intervals</li>
 *   <li>CDI event-based change notifications</li>
 *   <li>Automatic cleanup and resource management</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @see FileWatcherService
 * @see FileChangedEvent
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PortalFileWatcherService {
}
