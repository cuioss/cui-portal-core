package de.cuioss.portal.core.test.mocks.microprofile;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.metrics.Counter;

/**
 * Thread-safe implementation of MicroProfile {@link Counter} for testing purposes.
 * Provides a simple counter implementation that tracks increments using atomic operations.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Thread-safe counting operations</li>
 *   <li>Support for single and bulk increments</li>
 *   <li>Zero-based counter initialization</li>
 *   <li>Atomic operations for consistency</li>
 *   <li>No-loss counting up to Long.MAX_VALUE</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic counting:
 * <pre>
 * Counter counter = new PortalTestCounter();
 *
 * // Single increments
 * counter.inc();
 * assertEquals(1, counter.getCount());
 *
 * // Multiple increments
 * counter.inc(5);
 * assertEquals(6, counter.getCount());
 * </pre>
 *
 * Thread-safe operations:
 * <pre>
 * Counter counter = new PortalTestCounter();
 *
 * // Concurrent increments
 * ExecutorService executor = Executors.newFixedThreadPool(2);
 * executor.submit(() -> counter.inc(100));
 * executor.submit(() -> counter.inc(200));
 * executor.shutdown();
 * executor.awaitTermination(1, TimeUnit.SECONDS);
 *
 * assertEquals(300, counter.getCount());
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Uses {@link AtomicLong} for thread-safe operations</li>
 *   <li>Initializes counter to zero</li>
 *   <li>Implements MicroProfile Metrics {@link Counter} interface</li>
 *   <li>Suitable for high-concurrency test scenarios</li>
 *   <li>No upper bound checking on increments</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see Counter
 * @see AtomicLong
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
