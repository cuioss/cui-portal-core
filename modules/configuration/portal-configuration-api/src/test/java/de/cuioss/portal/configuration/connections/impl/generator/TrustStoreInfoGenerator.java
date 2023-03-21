package de.cuioss.portal.configuration.connections.impl.generator;

import static de.cuioss.portal.configuration.connections.impl.generator.KeyStoreInfoGenerator.BASE_PATH;
import static de.cuioss.portal.configuration.connections.impl.generator.KeyStoreInfoGenerator.PASSWORD;
import static de.cuioss.test.generator.Generators.fixedValues;

import java.io.File;

import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;

@SuppressWarnings("javadoc")
public class TrustStoreInfoGenerator implements TypedGenerator<KeyStoreProvider> {

    private final TypedGenerator<File> trustores = fixedValues(BASE_PATH.resolve("truststore1.keystore").toFile(),
            BASE_PATH.resolve("truststore2.keystore").toFile());

    @Override
    public KeyStoreProvider next() {
        return KeyStoreProvider.builder().keyStoreType(KeyStoreType.TRUST_STORE)
                .location(trustores.next().getAbsoluteFile())
                .storePassword(PASSWORD).build();
    }

    @Override
    public Class<KeyStoreProvider> getType() {
        return KeyStoreProvider.class;
    }

}
