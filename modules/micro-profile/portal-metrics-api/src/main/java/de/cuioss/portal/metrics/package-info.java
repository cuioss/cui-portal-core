/**
 * Provides MicroProfile Metrics integration for Portal applications, with a focus on Caffeine Cache metrics.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.metrics.CaffeineCacheMetrics} - Metrics collector for Caffeine caches</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Comprehensive cache metrics collection</li>
 *   <li>Hit/miss statistics tracking</li>
 *   <li>Load performance monitoring</li>
 *   <li>Size and eviction metrics</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * To collect metrics for a Caffeine cache:
 * 
 * <pre>
 * // Create cache with stats enabled
 * Cache&lt;String, String&gt; cache = Caffeine.newBuilder()
 *     .maximumSize(1000)
 *     .recordStats()
 *     .build();
 *     
 * // Register metrics
 * new CaffeineCacheMetrics("cache-name", cache, cacheConfig)
 *     .bindTo(metricRegistry);
 * </pre>
 * 
 * <h2>Configuration</h2>
 * Application name for metrics can be configured using:
 * <ul>
 *   <li>{@code mp.metrics.appName} - MicroProfile standard property</li>
 *   <li>{@code portal.metrics.appName} - Portal-specific property</li>
 *   <li>{@code portal.application.name} - Fallback application name</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are thread-safe and designed for concurrent access.
 * Metric collection is performed in a thread-safe manner using atomic operations.
 * 
 * @author Oliver Wolff
 * @see org.eclipse.microprofile.metrics.MetricRegistry
 * @see com.github.benmanes.caffeine.cache.Cache
 */
package de.cuioss.portal.metrics;
