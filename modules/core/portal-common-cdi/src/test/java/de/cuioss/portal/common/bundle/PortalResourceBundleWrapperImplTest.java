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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

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

import de.cuioss.portal.common.bundle.support.PortalMessages;
import de.cuioss.portal.common.locale.LocaleChangeEvent;
import de.cuioss.portal.common.locale.PortalLocale;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import de.cuioss.uimodel.application.CuiProjectStage;
import lombok.Getter;

@EnableAutoWeld
@AddBeanClasses({ PortalMessages.class, ResourceBundleRegistry.class, ResourceBundleWrapperImpl.class })
@ActivateScopes({ RequestScoped.class, SessionScoped.class })
class PortalResourceBundleWrapperImplTest implements ShouldHandleObjectContracts<ResourceBundleWrapper> {

    public static final String PORTAL_TITLE = "CUI Oss Portal";

    @Inject
    @Getter
    private ResourceBundleWrapper underTest;

    @Produces
    private CuiProjectStage projectStage;

    @Produces
    @PortalLocale
    private Locale locale;

    @Inject
    @LocaleChangeEvent
    Event<Locale> localeChangeEvent;

    @BeforeEach
    void beforeEach() {
        projectStage = ProjectStage.PRODUCTION;
        locale = Locale.ENGLISH;
    }

    @Test
    void shouldReturnCorrectMessage() {
        // portal_messages
        assertEquals("Logout", underTest.getString("page.logout.srHeader"));
        // vendor_messages
        assertEquals(PORTAL_TITLE, underTest.getString("portal.title"));
    }

    @Test
    void shouldSwitchMessageBundleOnLocaleChange() {
        assertEquals("Internal server error", underTest.getString("page.error.title"));
        locale = Locale.GERMAN;
        localeChangeEvent.fire(Locale.GERMAN);

        assertEquals("Interner Server Fehler", underTest.getString("page.error.title"));
    }

    @Test
    void shouldFailOnInvalidKey() {
        projectStage = ProjectStage.DEVELOPMENT;
        assertThrows(MissingResourceException.class, () -> {
            underTest.getString("not.there");
        });
    }

    void shouldLogOnInvalidKey() {
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.WARN, PortalResourceBundleWrapperImplTest.class);
        final var missing_key = "not.there";
        assertEquals(underTest.getString(missing_key), "??" + missing_key + "??");

        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, "Portal-003");
    }

    @Test
    void testGetKeys() {
        final List<String> keys = Collections.list(underTest.getKeys());
        assertNotNull(keys);
        assertTrue(keys.size() > 60);
    }
}
