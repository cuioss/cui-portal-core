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
package de.cuioss.portal.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests PortalResourceLoader utility")
class PortalResourceLoaderTest {

    private static final String EXISTING_RESOURCE = "/META-INF/beans.xml";
    private static final String NON_EXISTING_RESOURCE = "/not/present";

    @Nested
    @DisplayName("Resource Loading Tests")
    class ResourceLoadingTests {

        @Test
        @DisplayName("Should find existing resource")
        void shouldHandleExistingResource() {
            var resource = PortalResourceLoader.getResource(EXISTING_RESOURCE, getClass());
            assertTrue(resource.isPresent(), "Should find existing resource");
            assertNotNull(resource.get(), "Resource URL should not be null");
        }

        @Test
        @DisplayName("Should handle non-existing resource")
        void shouldHandleNonExistingResource() {
            assertFalse(PortalResourceLoader.getResource(NON_EXISTING_RESOURCE, getClass()).isPresent(),
                    "Should return empty Optional for non-existing resource");
        }

    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should reject null resource path")
        void shouldRejectNullResourcePath() {
            final var clazz = getClass();
            assertThrows(NullPointerException.class,
                    () -> PortalResourceLoader.getResource(null, clazz),
                    "Should throw NullPointerException for null resource path");
        }

        @Test
        @DisplayName("Should reject null calling class")
        void shouldRejectNullCallingClass() {
            assertThrows(NullPointerException.class,
                    () -> PortalResourceLoader.getResource(EXISTING_RESOURCE, null),
                    "Should throw NullPointerException for null calling class");
        }
    }
}
