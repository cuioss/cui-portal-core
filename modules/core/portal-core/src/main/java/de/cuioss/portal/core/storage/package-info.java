/**
 * Provides a type-safe, thread-safe storage abstraction layer for managing portal data
 * across different scopes. This package offers interfaces and implementations for storing
 * data without direct coupling to the underlying storage mechanism.
 *
 * <p><strong>Key Components:</strong></p>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.storage.MapStorage} - Base interface for key-value storage</li>
 *   <li>{@link de.cuioss.portal.core.storage.SessionStorage} - Session-scoped storage</li>
 *   <li>{@link de.cuioss.portal.core.storage.ClientStorage} - Client-side storage</li>
 *   <li>{@link de.cuioss.portal.core.storage.impl.MapStorageImpl} - Thread-safe implementation</li>
 * </ul>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Type-safe storage operations</li>
 *   <li>Concurrent access support</li>
 *   <li>Multiple storage scopes</li>
 *   <li>Serialization support</li>
 * </ul>
 *
 * <p><strong>Usage Contexts:</strong></p>
 * <ul>
 *   <li>User session data</li>
 *   <li>Client preferences</li>
 *   <li>Temporary application state</li>
 *   <li>Distributed session data</li>
 * </ul>
 *
 * @see jakarta.servlet.http.HttpSession
 * @see java.io.Serializable
 * @see java.util.concurrent.ConcurrentHashMap
 * @since 1.0
 */
package de.cuioss.portal.core.storage;
