package de.cuioss.portal.core.test.mocks.microprofile;

import org.eclipse.microprofile.metrics.Metric;

import de.cuioss.test.generator.TypedGenerator;

class MetricGenerator implements TypedGenerator<Metric> {

    @Override
    public Metric next() {
        return new Metric() {
        };
    }

}
