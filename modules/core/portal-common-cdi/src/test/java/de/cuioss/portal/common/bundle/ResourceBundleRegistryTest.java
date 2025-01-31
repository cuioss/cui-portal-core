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

import de.cuioss.portal.common.bundle.support.HighPrioBundles;
import de.cuioss.portal.common.bundle.support.MediumPrioBundles;
import de.cuioss.portal.common.bundle.support.MissingBundle;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddBeanClasses({MediumPrioBundles.class, HighPrioBundles.class, MissingBundle.class})
class ResourceBundleRegistryTest implements ShouldHandleObjectContracts<ResourceBundleRegistry> {

    @Inject
    @Getter
    private ResourceBundleRegistry underTest;

    @Test
    void shouldInitPortalResourceBundles() {
        assertNotNull(underTest);
        assertNotNull(underTest.getResolvedPaths());
        assertEquals(2, underTest.getResolvedPaths().size());
        assertEquals(HighPrioBundles.HIGH_1, underTest.getResolvedPaths().get(0).getBundlePath().get());
        assertEquals(MediumPrioBundles.MEDIUM_1, underTest.getResolvedPaths().get(1).getBundlePath().get());
    }

    // TODO : verify log error msgs

}
