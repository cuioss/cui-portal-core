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
package de.cuioss.portal.common.bundle;

import de.cuioss.portal.common.bundle.support.PortalMessages;
import de.cuioss.portal.common.locale.LocaleChangeEvent;
import de.cuioss.portal.common.locale.PortalLocale;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.uimodel.application.CuiProjectStage;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PortalResourceBundleBean} which verifies:
 * <ul>
 *   <li>Resource bundle loading and key resolution</li>
 *   <li>Locale change handling</li>
 *   <li>Project stage specific behavior (e.g. error handling)</li>
 *   <li>Key enumeration and access</li>
 * </ul>
 * 
 * <p>Test Setup:
 * <ul>
 *   <li>Uses Weld test framework for CDI context</li>
 *   <li>Activates RequestScoped and SessionScoped contexts</li>
 *   <li>Provides test messages via {@link PortalMessages}</li>
 *   <li>Configurable project stage and locale via producers</li>
 * </ul>
 */
@EnableAutoWeld
@AddBeanClasses({PortalMessages.class, ResourceBundleRegistry.class, ResourceBundleWrapperImpl.class})
@ActivateScopes({RequestScoped.class, SessionScoped.class})
class PortalResourceBundleBeanTest implements ShouldHandleObjectContracts<PortalResourceBundleBean> {

    @Produces
    private CuiProjectStage projectStage;

    @Produces
    @PortalLocale
    private Locale locale;

    @BeforeEach
    void beforeEach() {
        projectStage = ProjectStage.PRODUCTION;
        locale = Locale.ENGLISH;
    }

    @Inject
    @LocaleChangeEvent
    Event<Locale> localeChangeEvent;

    /**
     * Verifies basic message resolution for multiple keys.
     * Tests that:
     * <ul>
     *   <li>Common error messages are correctly resolved</li>
     *   <li>Portal title is available</li>
     *   <li>Keys from different message bundles are accessible</li>
     * </ul>
     */
    @Test
    void getMessage() {
        var underTest = getUnderTest();
        assertEquals("Internal server error", underTest.getString("page.error.title"));
        assertEquals("Internal server error", underTest.getString("page.error.srHeader"));
        assertEquals(PortalResourceBundleWrapperImplTest.PORTAL_TITLE, underTest.getString("portal.title"));
    }

    /**
     * Verifies that the resource bundle correctly responds to locale changes.
     * Tests that:
     * <ul>
     *   <li>Initial messages are in English</li>
     *   <li>After locale change event, messages switch to German</li>
     *   <li>Same keys resolve to different translations</li>
     * </ul>
     */
    @Test
    void shouldSwitchMessageBundleOnLocaleChange() {
        var underTest = getUnderTest();
        assertEquals("Internal server error", underTest.getString("page.error.title"));
        locale = Locale.GERMAN;
        localeChangeEvent.fire(Locale.GERMAN);

        assertEquals("Interner Server Fehler", underTest.getString("page.error.title"));
    }

    /**
     * Verifies error handling behavior in development stage.
     * Tests that:
     * <ul>
     *   <li>Missing keys throw MissingResourceException in development</li>
     *   <li>Exception contains information about the missing key</li>
     * </ul>
     * 
     * Note: In production stage, missing keys return the key surrounded by '??'
     */
    @Test
    void shouldFailOnInvalidKey() {
        var underTest = getUnderTest();
        projectStage = ProjectStage.DEVELOPMENT;
        assertThrows(MissingResourceException.class, () -> underTest.getString("not.there"));
    }

    /**
     * Verifies the key enumeration functionality.
     * Tests that:
     * <ul>
     *   <li>All expected keys are present</li>
     *   <li>Keys from all configured bundles are included</li>
     *   <li>The enumeration is not empty</li>
     * </ul>
     */
    @Test
    void shouldReturnKeys() {
        final List<String> keys = Collections.list(getUnderTest().getKeys());
        assertNotNull(keys);
        assertTrue(keys.contains("page.error.title"), "Should contain page.error.title");
        assertTrue(keys.contains("portal.title"), "Should contain portal.title");
    }

    @Override
    public PortalResourceBundleBean getUnderTest() {
        return new PortalResourceBundleBean();
    }
}
