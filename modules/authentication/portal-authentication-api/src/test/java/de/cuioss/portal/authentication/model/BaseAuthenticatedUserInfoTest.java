package de.cuioss.portal.authentication.model;

import static de.cuioss.test.generator.Generators.letterStrings;
import static de.cuioss.tools.collect.CollectionLiterals.immutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBuilder;
import de.cuioss.test.valueobjects.api.generator.PropertyGenerator;

@PropertyGenerator(ContextMapGenerator.class)
@VerifyBuilder
class BaseAuthenticatedUserInfoTest extends ValueObjectTest<BaseAuthenticatedUserInfo> {

    private final TypedGenerator<String> keys = Generators.nonEmptyStrings();
    // Values may be empty
    private final TypedGenerator<String> values = Generators.strings();
    private final TypedGenerator<Boolean> booleanGenerator = Generators.booleans();

    @Test
    void shouldHandleContextMap() {
        var info =
            BaseAuthenticatedUserInfo.builder().authenticated(booleanGenerator.next())
                    .displayName(keys.next())
                    .identifier(keys.next()).build();
        assertTrue(info.getContextMap().isEmpty());

        info = BaseAuthenticatedUserInfo.builder()
                .contextMap(new HashMap<>()).build();
        assertTrue(info.getContextMap().isEmpty());

        var distinct = Generators.asCollectionGenerator(keys).set(10).iterator();
        info = BaseAuthenticatedUserInfo.builder()
                .contextMapElement(distinct.next(), values.next())
                .contextMapElement(distinct.next(), values.next())
                .build();

        assertEquals(2, info.getContextMap().size());
    }

    @Test
    void shouldHandleRoles() {
        var someRole = letterStrings(2, 5).next();
        var info = BaseAuthenticatedUserInfo.builder().authenticated(booleanGenerator.next())
                .displayName(keys.next())
                .identifier(keys.next()).build();
        assertFalse(info.isUserInRole(someRole));

        info = BaseAuthenticatedUserInfo.builder().authenticated(booleanGenerator.next())
                .displayName(keys.next()).roles(immutableList(someRole))
                .identifier(keys.next()).build();
        assertTrue(info.isUserInRole(someRole));
    }

}
