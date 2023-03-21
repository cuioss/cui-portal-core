package de.cuioss.portal.configuration.impl;

import static de.cuioss.test.generator.Generators.fixedValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.impl.support.ExplodingFileLoader;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyConstructor;
import de.cuioss.test.valueobjects.api.property.PropertyConfig;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.property.PropertyReadWrite;

@PropertyConfig(name = "fileLoader", propertyClass = FileLoader.class, propertyReadWrite = PropertyReadWrite.WRITE_ONLY)
@VerifyConstructor(of = "fileLoader", required = "fileLoader")
@PropertyReflectionConfig(skip = true)
class PropertiesConfigurationProviderTest extends ValueObjectTest<PropertiesConfigurationProvider>
        implements TypedGenerator<FileLoader> {

    private static final String BASE = "src/test/resources/";
    private static final String ONE = BASE + "cui_logger.properties";
    private static final String TWO = BASE + "test.properties";
    private static final String INVALID_PROPERTIES = BASE + "keystore1.keystore";

    private static final TypedGenerator<String> VALID_PATHS = fixedValues(ONE, TWO);

    @Test
    void shouldProduceProperties() {
        final var provider = new PropertiesConfigurationProvider(TWO);
        assertEquals(1, provider.getConfigurationMap().size());
    }

    @Test
    void shouldHandleInvalidProperties() {
        final var provider = new PropertiesConfigurationProvider(INVALID_PROPERTIES);
        assertEquals(81, provider.getConfigurationMap().size());
    }

    @Test
    void shouldHandleNotExistingProperties() {
        final var provider = new PropertiesConfigurationProvider("not/there");
        assertEquals(0, provider.getConfigurationMap().size());
    }

    @Test
    void shouldHandleIOException() {
        final var provider = new PropertiesConfigurationProvider(new ExplodingFileLoader());
        assertEquals(0, provider.getConfigurationMap().size());
    }

    @Override
    public FileLoader next() {
        return FileLoaderUtility.getLoaderForPath(VALID_PATHS.next());
    }

    @Override
    public Class<FileLoader> getType() {
        return FileLoader.class;
    }

}
