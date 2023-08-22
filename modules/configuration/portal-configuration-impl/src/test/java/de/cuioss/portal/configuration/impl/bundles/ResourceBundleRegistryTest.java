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
package de.cuioss.portal.configuration.impl.bundles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.bundles.PortalResourceBundleRegistry;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ MediumPrioBundles.class, HighPrioBundles.class, DefectBundle.class })
class ResourceBundleRegistryTest {

    @Inject
    @Getter
    @PortalResourceBundleRegistry
    private ResourceBundleRegistryImpl underTest;

    @Test
    void shouldInitPortalResourceBundles() {
        assertNotNull(underTest);
        assertNotNull(underTest.getResolvedPaths());
        assertEquals(4, underTest.getResolvedPaths().size());
        assertEquals(HighPrioBundles.HIGH_1, underTest.getResolvedPaths().get(0));
        assertEquals(HighPrioBundles.HIGH_2, underTest.getResolvedPaths().get(1));
        assertEquals(MediumPrioBundles.MEDIUM_1, underTest.getResolvedPaths().get(2));
        assertEquals(MediumPrioBundles.MEDIUM_2, underTest.getResolvedPaths().get(3));
    }

    // TODO : verify log error msgs

}
