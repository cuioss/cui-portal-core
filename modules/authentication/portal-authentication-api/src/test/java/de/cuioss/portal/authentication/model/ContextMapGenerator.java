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
