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
package de.cuioss.portal.restclient;

import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the {@link BasicAuthenticationFilter} class focusing on parameter validation
 * and error handling.
 */
@EnableTestLogger
@DisplayName("BasicAuthenticationFilter Tests")
class BasicAuthenticationFilterTest {


    @Test
    @DisplayName("Should throw NPE for null constructor parameters")
    void shouldHandleNullValues() {
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationFilter(null, null),
                "Should throw NPE for null username and password");
        
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationFilter("user", null),
                "Should throw NPE for null password");
                
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationFilter(null, "pass"),
                "Should throw NPE for null username");
    }

    @Test
    @DisplayName("Should create filter with valid parameters")
    void shouldCreateFilterWithValidParameters() {
        var filter = new BasicAuthenticationFilter("testUser", "testPass");
        assertNotNull(filter, "Filter should be created with valid parameters");
    }
}
