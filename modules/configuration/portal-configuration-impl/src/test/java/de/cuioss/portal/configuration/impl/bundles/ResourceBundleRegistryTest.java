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
@AddBeanClasses({MediumPrioBundles.class, HighPrioBundles.class, DefectBundle.class})
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
