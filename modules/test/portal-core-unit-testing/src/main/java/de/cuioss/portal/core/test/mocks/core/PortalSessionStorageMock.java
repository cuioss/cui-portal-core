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
package de.cuioss.portal.core.test.mocks.core;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.portal.core.storage.SessionStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Mock implementation of {@link PortalSessionStorage} for testing session-based storage functionality.
 * Provides an in-memory map-based storage implementation that simulates session storage
 * with additional testing capabilities for error scenarios.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>In-memory session storage simulation</li>
 *   <li>Support for any Serializable objects</li>
 *   <li>Configurable error simulation</li>
 *   <li>Application-scoped persistence</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic storage operations:
 * <pre>
 * &#64;Inject
 * &#64;PortalSessionStorage
 * private SessionStorage storage;
 *
 * void testStorage() {
 *     // Store serializable object
 *     storage.put("user", new User("John"));
 *
 *     // Retrieve object
 *     User user = (User) storage.get("user");
 *     assertEquals("John", user.getName());
 * }
 * </pre>
 *
 * Error simulation:
 * <pre>
 * &#64;Inject
 * private PortalSessionStorageMock storage;
 *
 * void testErrorScenario() {
 *     // Configure to throw exception on next access
 *     storage.setThrowIllegalStateOnAccessOnce(true);
 *
 *     assertThrows(IllegalStateException.class, () -> {
 *         storage.get("any-key");
 *     });
 *
 *     // Next access works fine (one-time error)
 *     storage.put("key", "value");
 *     assertEquals("value", storage.get("key"));
 * }
 *
 * void testPermanentError() {
 *     // Configure to always throw exception
 *     storage.setThrowIllegalStateOnAccess(true);
 *
 *     assertThrows(IllegalStateException.class, () -> {
 *         storage.get("key");
 *     });
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Extends {@link MapStorageImpl} for base functionality</li>
 *   <li>Supports any Serializable key-value pairs</li>
 *   <li>Provides error simulation for testing error handling</li>
 *   <li>Thread-safe through synchronized operations</li>
 *   <li>Maintains state during application lifecycle</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see PortalSessionStorage
 * @see SessionStorage
 * @see MapStorageImpl
 * @see Serializable
 */
@ApplicationScoped
@PortalSessionStorage
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PortalSessionStorageMock extends MapStorageImpl<Serializable, Serializable> implements SessionStorage {

    @Serial
    private static final long serialVersionUID = 2230273572072236755L;

    @Getter
    @Setter
    private boolean throwIllegalStateOnAccess = false;

    @Getter
    @Setter
    private boolean throwIllegalStateOnAccessOnce = false;

    @Override
    public boolean containsKey(final Serializable key) {
        checkThrowException();
        return super.containsKey(key);
    }

    @Override
    public Serializable get(final Serializable key) {
        checkThrowException();
        return super.get(key);
    }

    @Override
    public Serializable get(final Serializable key, final Serializable defaultValue) {
        checkThrowException();
        return super.get(key, defaultValue);
    }

    @Override
    public Serializable remove(final Serializable key) {
        checkThrowException();
        return super.remove(key);
    }

    @Override
    public void put(final Serializable key, final Serializable object) {
        checkThrowException();
        super.put(key, object);
    }

    private void checkThrowException() {
        if (throwIllegalStateOnAccessOnce) {
            throwIllegalStateOnAccessOnce = false;
            throw new IllegalStateException("Boom (Once)");
        }
        if (throwIllegalStateOnAccess) {
            throw new IllegalStateException("Boom");
        }
    }

}
