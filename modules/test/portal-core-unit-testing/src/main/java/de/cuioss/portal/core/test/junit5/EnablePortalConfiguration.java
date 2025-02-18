/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.test.junit5;

import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import io.smallrye.config.inject.ConfigProducer;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * JUnit 5 annotation that enables Portal configuration support in unit tests.
 * This annotation provides the infrastructure for configuration-based testing by
 * setting up CDI beans and configuration producers.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>CDI configuration support</li>
 *   <li>MicroProfile Config integration</li>
 *   <li>Portal-specific configuration</li>
 *   <li>Property-based configuration</li>
 * </ul>
 *
 * <h2>Included Components</h2>
 * <ul>
 *   <li>{@link ConfigProducer} - MicroProfile Config producer</li>
 *   <li>{@link PortalTestConfiguration} - Portal-specific test configuration</li>
 *   <li>{@link PortalConfigProducer} - Portal configuration producer</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * <pre>
 * // Basic usage with default configuration
 * &#64;EnablePortalConfiguration
 * class BasicConfigTest {
 *     &#64;Inject
 *     PortalTestConfiguration configuration;
 *
 *     &#64;Test
 *     void shouldHaveDefaultConfig() {
 *         assertNotNull(configuration);
 *     }
 * }
 *
 * // Usage with custom configuration properties
 * &#64;EnablePortalConfiguration(configuration = {
 *     "portal.some.key=value",
 *     "portal.other.key=123"
 * })
 * class CustomConfigTest {
 *     &#64;Inject
 *     PortalTestConfiguration configuration;
 *
 *     &#64;Test
 *     void shouldHaveCustomConfig() {
 *         assertEquals("value", configuration.getString("portal.some.key"));
 *         assertEquals(123, configuration.getInteger("portal.other.key"));
 *     }
 * }
 * </pre>
 *
 * <h2>Configuration Format</h2>
 * <ul>
 *   <li>Properties use key=value format</li>
 *   <li>Keys should follow portal naming convention</li>
 *   <li>Values are parsed according to their type</li>
 *   <li>Multiple properties can be specified</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see PortalTestConfiguration
 * @see PortalTestConfigurationExtension
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@ExtendWith(PortalTestConfigurationExtension.class)
@AddBeanClasses({PortalTestConfiguration.class, PortalConfigProducer.class, ConfigProducer.class})
public @interface EnablePortalConfiguration {

    /**
     * @return an array of Strings representing additional configuration elements to
     * be applied for each test. The individual Strings are expected in the
     * form "key:value", e.g. "portal.locale.default:de"
     */
    String[] configuration() default {};
}
