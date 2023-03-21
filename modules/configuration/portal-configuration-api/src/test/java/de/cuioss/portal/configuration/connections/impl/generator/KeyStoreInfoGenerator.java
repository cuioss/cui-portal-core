package de.cuioss.portal.configuration.connections.impl.generator;

import static de.cuioss.test.generator.Generators.fixedValues;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;

@SuppressWarnings("javadoc")
public class KeyStoreInfoGenerator implements TypedGenerator<KeyStoreProvider> {

    static final String PASSWORD = "initinit";

    static final Path BASE_PATH = Paths.get("src/test/resources");

    private final TypedGenerator<File> keystores = fixedValues(BASE_PATH.resolve("keystore1.keystore").toFile(),
            BASE_PATH.resolve("keystore2.keystore").toFile());

    @Override
    public KeyStoreProvider next() {
        return KeyStoreProvider.builder().keyStoreType(KeyStoreType.KEY_STORE)
                .location(keystores.next().getAbsoluteFile())
                .storePassword(PASSWORD).build();
    }

    @Override
    public Class<KeyStoreProvider> getType() {
        return KeyStoreProvider.class;
    }

}
