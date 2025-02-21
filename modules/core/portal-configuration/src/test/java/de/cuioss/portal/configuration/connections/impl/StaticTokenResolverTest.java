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
package de.cuioss.portal.configuration.connections.impl;

import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyConstructor;
import de.cuioss.test.valueobjects.api.property.PropertyConfig;
import de.cuioss.tools.property.PropertyReadWrite;
import org.junit.jupiter.api.Test;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PropertyConfig(name = "token", propertyClass = String.class, propertyReadWrite = PropertyReadWrite.WRITE_ONLY)
@VerifyConstructor(of = {"key", "token"})
class StaticTokenResolverTest extends ValueObjectTest<StaticTokenResolver> {

    @Test
    void shouldResolveToken() {
        var token = nonEmptyStrings().next();
        assertEquals(token, new StaticTokenResolver("key", token).resolve());
    }
}
