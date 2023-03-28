package de.cuioss.portal.core.test.mocks.microprofile;

import static de.cuioss.test.generator.Generators.booleans;
import static de.cuioss.test.generator.Generators.letterStrings;

import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;

import de.cuioss.test.generator.TypedGenerator;

class MetricIDGenerator implements TypedGenerator<MetricID> {

    private static final TypedGenerator<String> names = letterStrings(1, 5);
    private static final TypedGenerator<Tag> tags = new MetricTagGenerator();

    @Override
    public MetricID next() {
        if (booleans().next().booleanValue()) {
            return new MetricID(names.next());
        }
        return new MetricID(names.next(), tags.next());
    }

}
