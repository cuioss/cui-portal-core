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
package de.cuioss.portal.core.test.junit5;

import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Splitter;
import jakarta.enterprise.inject.spi.CDI;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import static de.cuioss.tools.base.Preconditions.checkArgument;

/**
 * JUnit 5 {@link Extension} that manages the configuration lifecycle for portal unit tests.
 * This extension works in conjunction with the {@link EnablePortalConfiguration} annotation
 * to provide a clean, isolated test environment for each test method.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Automatic configuration reset before each test</li>
 *   <li>CDI context management</li>
 *   <li>Property configuration support</li>
 *   <li>Fail-fast validation</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * &#64;EnablePortalConfiguration
 * class PortalConfigTest {
 *
 *     &#64;Test
 *     void shouldHaveCleanConfiguration() {
 *         // Each test starts with a fresh configuration
 *         assertNotNull(CDI.current().select(PortalTestConfiguration.class));
 *     }
 * }
 * </pre>
 *
 * <h2>Configuration Process</h2>
 * <ol>
 *   <li>Validates the test class is properly annotated</li>
 *   <li>Retrieves {@link PortalTestConfiguration} from CDI context</li>
 *   <li>Clears any existing configuration</li>
 *   <li>Applies test-specific configuration if specified</li>
 * </ol>
 *
 * <h2>Error Handling</h2>
 * <ul>
 *   <li>Throws {@link IllegalStateException} if test class cannot be determined</li>
 *   <li>Throws {@link IllegalStateException} if CDI context is not available</li>
 *   <li>Logs configuration process at debug level</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see EnablePortalConfiguration
 * @see PortalTestConfiguration
 */
public class PortalTestConfigurationExtension implements BeforeEachCallback {

    private static final CuiLogger LOGGER = new CuiLogger(PortalTestConfigurationExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        Class<?> testClass = context.getTestClass()
                .orElseThrow(() -> new IllegalStateException("Unable to determine Test-class"));
        LOGGER.debug("Processing test-class %s", testClass);

        CDI<Object> cdi;
        try {
            cdi = CDI.current();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("""
                    CDI not present, change the order of annotation and put @EnableAutoWeld above \
                    @EnablePortalConfiguration\
                    """, e);
        }

        var configuration = cdi.select(PortalTestConfiguration.class).get();
        LOGGER.debug("Resolved %s", configuration);

        configuration.clear();

        var annotation = AnnotationSupport.findAnnotation(testClass, EnablePortalConfiguration.class);
        if (annotation.isPresent()) {
            LOGGER.debug("Resolved annotation %s", annotation.get());
            for (String element : annotation.get().configuration()) {
                var splitted = Splitter.on(':').splitToList(element);
                checkArgument(2 <= splitted.size(), "Expected element in the form key:value, but was " + element);
                LOGGER.debug("Adding configuration entry: %s", element);
                configuration.update(splitted.get(0), element.substring(element.indexOf(':') + 1));
            }
        }

        LOGGER.debug("Finished processing instance %s", testClass);
    }
}
