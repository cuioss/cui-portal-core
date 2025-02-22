/**
 * Provides base test classes for verifying Portal Core module consistency,
 * assembly structure, and configuration.
 *
 * <h2>Package Organization</h2>
 * <ul>
 *   <li>Module Testing - {@link de.cuioss.portal.core.test.tests.BaseModuleConsistencyTest}</li>
 *   <li>Assembly Testing - {@link de.cuioss.portal.core.test.tests.BaseAssemblyConsistencyTest}</li>
 *   <li>Configuration Testing - {@link de.cuioss.portal.core.test.tests.configuration}</li>
 * </ul>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><strong>Module Consistency</strong> - CDI container and beans.xml verification</li>
 *   <li><strong>Assembly Structure</strong> - Web application packaging validation</li>
 *   <li><strong>Configuration Keys</strong> - Configuration source and key validation</li>
 * </ul>
 *
 * For detailed documentation and examples, see the individual class documentation:
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.tests.BaseModuleConsistencyTest} for module testing</li>
 *   <li>{@link de.cuioss.portal.core.test.tests.BaseAssemblyConsistencyTest} for assembly testing</li>
 *   <li>{@link de.cuioss.portal.core.test.tests.configuration.AbstractConfigurationKeyVerifierTest} for configuration testing</li>
 * </ul>
 *
 * @see de.cuioss.portal.core.test.tests.configuration
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.tests;
