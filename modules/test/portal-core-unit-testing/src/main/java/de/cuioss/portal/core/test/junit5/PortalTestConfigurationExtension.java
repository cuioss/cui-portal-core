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

import static de.cuioss.tools.base.Preconditions.checkArgument;

import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.Splitter;
import jakarta.enterprise.inject.spi.CDI;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Junit 5 {@link Extension} controlling the initialization process of a
 * unit-test annotated with {@link EnablePortalConfiguration}. The algorithm:
 * <ul>
 * <li>Access the instance of {@link PortalTestConfiguration} by using
 * CDI.current().select()</li>
 * <li>Calling {@link PortalTestConfiguration#clear()}</li>
 * </ul>
 *
 * @author Oliver Wolff
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
