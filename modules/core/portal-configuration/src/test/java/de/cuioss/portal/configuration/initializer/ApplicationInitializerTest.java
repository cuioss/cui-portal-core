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
package de.cuioss.portal.configuration.initializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationInitializerTest {

    @Test
    void shouldHandleCompareCorrectly() {
        var early = new TestInitializer(ApplicationInitializer.ORDER_EARLY);
        var intermediate = new TestInitializer(ApplicationInitializer.ORDER_INTERMEDIATE);
        var late = new TestInitializer(ApplicationInitializer.ORDER_LATE);

        //noinspection EqualsWithItself
        assertEquals(0, intermediate.compareTo(intermediate));
        assertEquals(-1, early.compareTo(intermediate));
        assertEquals(1, late.compareTo(intermediate));

    }

    @Test
    void shouldImplementNoOpDestroy() {
        assertDoesNotThrow(() -> new TestInitializer(ApplicationInitializer.ORDER_EARLY).destroy());
    }

    @Test
    void shouldDefaultToIntermediate() {
        ApplicationInitializer actual = () -> {
        };
        assertEquals(ApplicationInitializer.ORDER_INTERMEDIATE, actual.getOrder());
    }

    @AllArgsConstructor
    static class TestInitializer implements ApplicationInitializer {

        @Getter
        private Integer order;

        @Override
        public void initialize() {
            // Noop implementation: Test only
        }

    }
}
