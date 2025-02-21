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
