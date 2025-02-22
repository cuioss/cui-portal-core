/**
 * Root package for Portal Core unit testing utilities and support classes.
 * Provides a comprehensive set of testing tools, mocks, and base test classes
 * for Portal Core components.
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.mocks} - Mock implementations for Portal Core components
 *       <ul>
 *         <li>Authentication mocks</li>
 *         <li>Configuration mocks</li>
 *         <li>Storage mocks</li>
 *         <li>MicroProfile mocks</li>
 *       </ul>
 *   </li>
 *   <li>{@link de.cuioss.portal.core.test.tests} - Base test classes and utilities
 *       <ul>
 *         <li>Module consistency tests</li>
 *         <li>Assembly verification</li>
 *         <li>Configuration testing</li>
 *       </ul>
 *   </li>
 *   <li>{@link de.cuioss.portal.core.test.junit5} - JUnit 5 specific extensions
 *       <ul>
 *         <li>MockWebServer support</li>
 *         <li>Request/Response dispatchers</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h2>Usage</h2>
 * This module is designed to be used as a test dependency:
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;de.cuioss.portal.core&lt;/groupId&gt;
 *     &lt;artifactId&gt;portal-core-unit-testing&lt;/artifactId&gt;
 *     &lt;scope&gt;test&lt;/scope&gt;
 * &lt;/dependency&gt;
 * </pre>
 *
 * @see de.cuioss.portal.core.test.mocks
 * @see de.cuioss.portal.core.test.tests
 * @see de.cuioss.portal.core.test.junit5
 */
package de.cuioss.portal.core.test;
