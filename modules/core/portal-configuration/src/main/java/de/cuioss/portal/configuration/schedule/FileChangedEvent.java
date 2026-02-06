/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.schedule;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier for events fired when a monitored file or directory changes.
 * This event system is a core part of the Portal's dynamic configuration update mechanism.
 * <p>
 * The event payload is the {@link Path} that was modified. Event observers can use this
 * path to:
 * <ul>
 *   <li>Reload configuration files</li>
 *   <li>Update cached resources</li>
 *   <li>Trigger application reconfiguration</li>
 *   <li>Log configuration changes</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 * public class ConfigurationObserver {
 *     
 *     private static final CuiLogger log = new CuiLogger(ConfigurationObserver.class);
 *     
 *     public void onFileChanged(&#64;Observes &#64;FileChangedEvent Path path) {
 *         log.info(INFO.CONFIG_FILE_CHANGED.format(path));
 *         // Handle configuration update
 *     }
 * }
 * </pre>
 *
 * @author Oliver Wolff
 * @see FileWatcherService
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface FileChangedEvent {

}
