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

import de.cuioss.portal.core.storage.ClientStorage;
import de.cuioss.portal.core.storage.PortalClientStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.ToString;

import java.io.Serial;

/**
 * Mock Variant of {@link PortalClientStorage}
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalClientStorage
@ToString
public class PortalClientStorageMock extends MapStorageImpl<String, String> implements ClientStorage {

    @Serial
    private static final long serialVersionUID = -4658738277163105697L;

}
