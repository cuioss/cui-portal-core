/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * JUnit 5 specific extensions and test infrastructure for Portal Core testing.
 *
 * <h2>Package Organization</h2>
 * <ul>
 *   <li>Configuration Support
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.EnablePortalConfiguration} - Test configuration activation</li>
 *       <li>{@link de.cuioss.portal.core.test.junit5.PortalTestConfigurationExtension} - Configuration extension</li>
 *     </ul>
 *   </li>
 *   <li>HTTP Testing Support
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver} - Mock server and request handling</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><strong>Portal Configuration</strong> - Automated test configuration setup</li>
 *   <li><strong>HTTP Mocking</strong> - Web service interaction testing</li>
 *   <li><strong>JUnit Integration</strong> - Seamless JUnit 5 extension support</li>
 * </ul>
 *
 * For detailed documentation and examples, see:
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.EnablePortalConfiguration} for configuration setup</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver} for HTTP testing</li>
 * </ul>
 *
 * @see de.cuioss.portal.core.test.junit5.mockwebserver
 * @see org.junit.jupiter.api.extension.Extension
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.junit5;
