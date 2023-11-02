package de.cuioss.portal.common.bundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.cuioss.portal.common.bundle.support.HighPrioBundles;
import de.cuioss.portal.common.bundle.support.InvalidBundelPath;
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
        assertFalse(new InvalidBundelPath().getBundle(Locale.GERMANY).isPresent());
    }
}
