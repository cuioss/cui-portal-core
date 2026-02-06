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
package de.cuioss.portal.configuration.connections.impl.generator;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;

import java.io.File;

import static de.cuioss.portal.configuration.connections.impl.generator.KeyStoreInfoGenerator.BASE_PATH;
import static de.cuioss.portal.configuration.connections.impl.generator.KeyStoreInfoGenerator.PASSWORD;
import static de.cuioss.test.generator.Generators.fixedValues;

public class TrustStoreInfoGenerator implements TypedGenerator<KeyStoreProvider> {

    private final TypedGenerator<File> trustStores = fixedValues(
            BASE_PATH.resolve("truststore1.keystore").toFile(),
            BASE_PATH.resolve("truststore2.keystore").toFile());

    @Override
    public KeyStoreProvider next() {
        return KeyStoreProvider.builder().keyStoreType(KeyStoreType.TRUST_STORE)
                .location(trustStores.next().getAbsoluteFile()).storePassword(PASSWORD).build();
    }

    @Override
    public Class<KeyStoreProvider> getType() {
        return KeyStoreProvider.class;
    }

}
