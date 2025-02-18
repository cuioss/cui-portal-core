package de.cuioss.portal.core.test.mocks.microprofile;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.metrics.Counter;

/**
 * Simple implementation of {@link Counter} for testing purposes.
 */
public class PortalTestCounter implements Counter {

    private final AtomicLong count = new AtomicLong(0);

    @Override
    public void inc() {
        count.incrementAndGet();
    }

    @Override
    public void inc(long n) {
        count.addAndGet(n);
    }

    @Override
    public long getCount() {
        return count.get();
    }
}
