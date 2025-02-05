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
package de.cuioss.portal.common.bundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;

import de.cuioss.portal.common.bundle.support.HighPrioBundles;
import de.cuioss.portal.common.bundle.support.MediumPrioBundles;
import de.cuioss.portal.common.bundle.support.MissingBundle;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@EnableTestLogger(debug = {ResourceBundleRegistry.class, ResourceBundleLocator.class, ResourceBundleRegistryTest.class})
@AddBeanClasses({MediumPrioBundles.class, HighPrioBundles.class, MissingBundle.class})
@DisplayName("Tests the ResourceBundleRegistry functionality")
class ResourceBundleRegistryTest implements ShouldHandleObjectContracts<ResourceBundleRegistry> {

    @Inject
    @Getter
    private ResourceBundleRegistry underTest;

    @Nested
    @DisplayName("Bundle Resolution Tests")
    class BundleResolutionTests {
        
        @Test
        @DisplayName("Should initialize and sort portal resource bundles")
        void shouldInitPortalResourceBundles() {
            assertAll("Bundle initialization verification",
                () -> assertNotNull(underTest, "Registry should not be null"),
                () -> assertNotNull(underTest.getResolvedPaths(), "Resolved paths should not be null"),
                () -> assertEquals(2, underTest.getResolvedPaths().size(),
                    "Should resolve exactly two bundles"),
                () -> assertEquals(HighPrioBundles.HIGH_1, 
                    underTest.getResolvedPaths().get(0).getBundlePath().get(),
                    "First bundle should be high priority"),
                () -> assertEquals(MediumPrioBundles.MEDIUM_1,
                    underTest.getResolvedPaths().get(1).getBundlePath().get(),
                    "Second bundle should be medium priority")
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should log warning for missing bundle")
        void shouldLogWarningForMissingBundle() {
            // The MissingBundle class is already added to the context
            // This will trigger warning logging during initialization
            underTest.initBean();
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN,
                "Ignoring 'MissingBundle()'");
        }
    }
}
