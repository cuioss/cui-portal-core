/**
 * Provides base test classes and utilities for testing Portal Core components.
 * 
 * <h2>Core Test Classes</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.tests.BaseModuleConsistencyTest} - Base class for module consistency tests</li>
 *   <li>{@link de.cuioss.portal.core.test.tests.BaseAssemblyConsistencyTest} - Base class for assembly verification</li>
 *   <li>{@link de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest} - Base class for configuration key testing</li>
 * </ul>
 * 
 * <h2>Test Categories</h2>
 * <ul>
 *   <li><strong>Module Tests</strong> - Verify module structure and dependencies</li>
 *   <li><strong>Assembly Tests</strong> - Ensure correct packaging and deployment</li>
 *   <li><strong>Configuration Tests</strong> - Validate configuration keys and values</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * class MyModuleTest extends BaseModuleConsistencyTest {
 *     &#64;Override
 *     protected String getModuleName() {
 *         return "my-module";
 *     }
 * }
 * </pre>
 *
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.tests;
