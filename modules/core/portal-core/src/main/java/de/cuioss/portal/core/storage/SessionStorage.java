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
package de.cuioss.portal.core.storage;

import java.io.Serializable;

/**
 * Provides session-scoped storage capabilities for serializable objects within the portal.
 * This interface extends {@link MapStorage} with session-specific semantics, ensuring
 * that stored data persists only for the duration of a user's session.
 *
 * <p><strong>Key characteristics:</strong></p>
 * <ul>
 *   <li>Session-scoped persistence</li>
 *   <li>Automatic cleanup on session expiration</li>
 *   <li>Thread-safe operations</li>
 *   <li>Serializable storage for session replication</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * &#64;Inject
 * SessionStorage storage;
 *
 * // Store user preferences
 * UserPreferences prefs = new UserPreferences();
 * storage.put("userPrefs", prefs);
 *
 * // Retrieve with default value
 * UserPreferences stored = storage.get("userPrefs", new UserPreferences());
 * </pre>
 *
 * <p><strong>Implementation notes:</strong></p>
 * <ul>
 *   <li>All keys and values must be {@link Serializable}</li>
 *   <li>Implementations should handle concurrent access</li>
 *   <li>Large objects should be used with caution to avoid session bloat</li>
 * </ul>
 *
 * @see MapStorage
 * @see Serializable
 * @since 1.0
 */
public interface SessionStorage extends MapStorage<Serializable, Serializable> {

}
