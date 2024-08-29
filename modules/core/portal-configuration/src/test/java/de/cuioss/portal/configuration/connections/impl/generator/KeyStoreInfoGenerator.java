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
package de.cuioss.portal.configuration.connections.impl.generator;

import static de.cuioss.test.generator.Generators.fixedValues;

import java.io.File;
import java.nio.file.Path;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;

public class KeyStoreInfoGenerator implements TypedGenerator<KeyStoreProvider> {

    static final String PASSWORD = "initinit";

    static final Path BASE_PATH = Path.of("src/test/resources");

    private final TypedGenerator<File> keystores = fixedValues(BASE_PATH.resolve("keystore1.keystore").toFile(),
            BASE_PATH.resolve("keystore2.keystore").toFile());

    @Override
    public KeyStoreProvider next() {
        return KeyStoreProvider.builder().keyStoreType(KeyStoreType.KEY_STORE)
                .location(keystores.next().getAbsoluteFile()).storePassword(PASSWORD).build();
    }

    @Override
    public Class<KeyStoreProvider> getType() {
        return KeyStoreProvider.class;
    }

}
