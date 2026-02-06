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
/**
 * Provides the file watching and scheduling infrastructure for the Portal configuration system.
 * This package enables dynamic configuration updates by monitoring file system changes.
 * <p>
 * Key components:
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.schedule.FileWatcherService} - Core interface for file monitoring</li>
 *   <li>{@link de.cuioss.portal.configuration.schedule.FileChangedEvent} - CDI event for file changes</li>
 *   <li>{@link de.cuioss.portal.configuration.schedule.PortalFileWatcherService} - Portal-specific file watcher</li>
 * </ul>
 * <p>
 * The file watching system supports:
 * <ul>
 *   <li>Directory and file monitoring</li>
 *   <li>Configurable watch intervals</li>
 *   <li>Event-based change notifications</li>
 *   <li>Automatic configuration reloading</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.schedule;
