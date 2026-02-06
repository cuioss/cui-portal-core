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
package de.cuioss.portal.core.test.mocks.core;

import de.cuioss.portal.core.storage.ClientStorage;
import de.cuioss.portal.core.storage.PortalClientStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.ToString;

import java.io.Serial;

/**
 * Mock implementation of {@link PortalClientStorage} for testing client-side storage functionality.
 * Provides an in-memory map-based storage implementation that simulates client-side
 * data persistence in a test environment.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>In-memory key-value storage</li>
 *   <li>Thread-safe operations</li>
 *   <li>Serializable storage</li>
 *   <li>Application-scoped persistence</li>
 *   <li>String-based storage model</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic storage operations:
 * <pre>
 * &#64;Inject
 * &#64;PortalClientStorage
 * private ClientStorage storage;
 *
 * void testStorage() {
 *     // Store value
 *     storage.put("key", "value");
 *
 *     // Retrieve value
 *     assertEquals("value", storage.get("key"));
 *
 *     // Remove value
 *     storage.remove("key");
 *     assertNull(storage.get("key"));
 * }
 * </pre>
 *
 * Bulk operations:
 * <pre>
 * void testBulkOperations() {
 *     // Store multiple values
 *     Map&lt;String, String&gt; data = new HashMap&lt;&gt;();
 *     data.put("key1", "value1");
 *     data.put("key2", "value2");
 *     storage.putAll(data);
 *
 *     // Check storage size
 *     assertEquals(2, storage.size());
 *
 *     // Clear storage
 *     storage.clear();
 *     assertTrue(storage.isEmpty());
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Extends {@link MapStorageImpl} for base functionality</li>
 *   <li>Uses synchronized operations for thread safety</li>
 *   <li>Maintains state during application lifecycle</li>
 *   <li>Supports standard Map operations</li>
 *   <li>Implements {@link ClientStorage} interface</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see PortalClientStorage
 * @see ClientStorage
 * @see MapStorageImpl
 */
@ApplicationScoped
@PortalClientStorage
@ToString
public class PortalClientStorageMock extends MapStorageImpl<String, String> implements ClientStorage {

    @Serial
    private static final long serialVersionUID = -4658738277163105697L;

}
