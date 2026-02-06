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

/**
 * Provides client-side storage capabilities for string-based key-value pairs.
 * This interface extends {@link MapStorage} specifically for client-side data
 * persistence, typically implemented using browser storage mechanisms.
 *
 * <p><strong>Key characteristics:</strong></p>
 * <ul>
 *   <li>Client-side persistence</li>
 *   <li>String-based storage only</li>
 *   <li>Browser storage integration</li>
 *   <li>Cross-request data preservation</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * &#64;Inject
 * ClientStorage storage;
 *
 * // Store user preference
 * storage.put("theme", "dark");
 *
 * // Retrieve with default
 * String theme = storage.get("theme", "light");
 * </pre>
 *
 * <p><strong>Implementation notes:</strong></p>
 * <ul>
 *   <li>Storage is limited to string values only</li>
 *   <li>Data persists across browser sessions</li>
 *   <li>Storage capacity depends on browser limitations</li>
 *   <li>Sensitive data should not be stored</li>
 * </ul>
 *
 * @see MapStorage
 * @see SessionStorage
 * @since 1.0
 */
public interface ClientStorage extends MapStorage<String, String> {
}
