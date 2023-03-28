package de.cuioss.portal.core.test.mocks.microprofile;

import static de.cuioss.test.generator.Generators.letterStrings;

import org.eclipse.microprofile.metrics.Tag;

import de.cuioss.test.generator.TypedGenerator;

class MetricTagGenerator implements TypedGenerator<Tag> {

    private static final TypedGenerator<String> names = letterStrings(1, 5);

    @Override
    public Tag next() {
        return new Tag(names.next(), names.next());
    }

}
