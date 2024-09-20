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
package de.cuioss.portal.authentication.dummy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.authentication.facade.PortalAuthenticationFacade;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class DummyAuthenticationFacadeTest implements ShouldBeNotNull<DummyAuthenticationFacade> {

    @Inject
    @PortalAuthenticationFacade
    @Getter
    private DummyAuthenticationFacade underTest;

    @Test
    void shouldAlwaysReturnDummyUser() {

        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.retrieveCurrentAuthenticationContext(null));
        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.login(null, null).getResult());

        assertFalse(underTest.logout(EasyMock.createNiceMock(HttpServletRequest.class)));

        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.retrieveCurrentAuthenticationContext(null));
        assertEquals(DummyAuthenticationFacade.NOT_LOGGED_IN, underTest.login(null, null).getResult());
    }

    @Test
    void shouldProvideSensibleDefaults() {
        assertFalse(DummyAuthenticationFacade.NOT_LOGGED_IN.isAuthenticated());
        assertNotNull(underTest.getAvailableUserStores());
        assertTrue(underTest.getAvailableUserStores().isEmpty());
    }
}
