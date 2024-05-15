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

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.portal.core.storage.SessionStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Mock Variant of {@link PortalSessionStorage}
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalSessionStorage
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PortalSessionStorageMock extends MapStorageImpl<Serializable, Serializable> implements SessionStorage {

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
