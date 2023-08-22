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
package de.cuioss.portal.authentication.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.TypedGenerator;

@SuppressWarnings({ "rawtypes", "javadoc" })
public class ContextMapGenerator implements TypedGenerator<Map> {

    private final TypedGenerator<String> stringGenerator = Generators.nonEmptyStrings();
    private final TypedGenerator<Integer> integerGenerator = Generators.integers(0, 10);

    @Override
    public Map<Serializable, Serializable> next() {
        final Map<Serializable, Serializable> contextMap = new HashMap<>();

        for (var i = 0; i < integerGenerator.next(); i++) {
            contextMap.put(integerGenerator.next(), stringGenerator.next());
        }
        return contextMap;
    }

    @Override
    public Class<Map> getType() {
        return Map.class;
    }

}
