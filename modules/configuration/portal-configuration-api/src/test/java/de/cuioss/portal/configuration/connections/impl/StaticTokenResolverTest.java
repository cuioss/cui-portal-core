package de.cuioss.portal.configuration.connections.impl;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyConstructor;
import de.cuioss.test.valueobjects.api.property.PropertyConfig;
import de.cuioss.tools.property.PropertyReadWrite;

@PropertyConfig(name = "token", propertyClass = String.class, propertyReadWrite = PropertyReadWrite.WRITE_ONLY)
@VerifyConstructor(of = { "key", "token" })
class StaticTokenResolverTest extends ValueObjectTest<StaticTokenResolver> {

    @Test
    void shouldResolveToken() {
        var token = nonEmptyStrings().next();
        assertEquals(token, new StaticTokenResolver("key", token).resolve());
    }
}
