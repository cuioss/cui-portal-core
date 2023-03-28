package de.cuioss.portal.core.test.mocks.microprofile;

import static de.cuioss.test.generator.Generators.enumValues;
import static de.cuioss.test.generator.Generators.letterStrings;

import org.eclipse.microprofile.metrics.DefaultMetadata;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricType;

import de.cuioss.test.generator.TypedGenerator;

class MetricMetadataGenerator implements TypedGenerator<Metadata> {

    @Override
    public Metadata next() {
        return new GeneratorMetadata();
    }
}

class GeneratorMetadata extends DefaultMetadata {

    private static final TypedGenerator<String> names = letterStrings(1, 5);

    public GeneratorMetadata() {
        super(names.next(), names.next(), names.next(), enumValues(MetricType.class).next(),
                names.next());
    }

}
