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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.common.bundle.support.HighPrioBundles;
import de.cuioss.portal.common.bundle.support.InvalidBundlePath;
import de.cuioss.portal.common.bundle.support.MissingBundle;

class ResourceBundleLocatorTest {

    @Test
    void shouldHandleHappyCase() {
        assertTrue(new HighPrioBundles().getBundle(Locale.GERMANY).isPresent());
    }

    @Test
    void shouldHandleMissingPath() {
        assertFalse(new MissingBundle().getBundle(Locale.GERMANY).isPresent());
    }

    @Test
    void shouldHandleInvalidPath() {
        assertFalse(new InvalidBundlePath().getBundle(Locale.GERMANY).isPresent());
    }
}
