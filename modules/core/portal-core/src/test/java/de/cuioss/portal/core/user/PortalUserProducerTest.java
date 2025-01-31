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
package de.cuioss.portal.core.user;

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.core.test.support.PortalAuthenticationFacadeMock;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.easymock.EasyMock;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddBeanClasses({PortalAuthenticationFacadeMock.class})
@ActivateScopes(RequestScoped.class)
class PortalUserProducerTest implements ShouldHandleObjectContracts<PortalUserProducer> {

    @Produces
    @RequestScoped
    HttpServletRequest produceEasyMockServletRequest() {
        return EasyMock.createNiceMock(HttpServletRequest.class);
    }

    @Inject
    @Getter
    private PortalUserProducer underTest;

    @Inject
    private Provider<AuthenticatedUserInfo> userInfoProvider;

    /**
     * Checks whether the correct user is produced.
     */
    @Test
    void shouldProduceUser() {
        var userInfo = userInfoProvider.get();
        assertNotNull(userInfo);
        assertTrue(userInfo.isAuthenticated());
        assertEquals(PortalAuthenticationFacadeMock.USER, userInfo.getDisplayName());
    }
}
