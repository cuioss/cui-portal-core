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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link PortalUserProducer} which handles the production of
 * authenticated user information in the portal context.
 */
@EnableAutoWeld
@AddBeanClasses({PortalAuthenticationFacadeMock.class})
@ActivateScopes(RequestScoped.class)
@DisplayName("PortalUserProducer Tests")
class PortalUserProducerTest {

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

    @Nested
    @DisplayName("User Production Tests")
    class UserProductionTests {

        @Test
        @DisplayName("Should produce authenticated user with correct information")
        void shouldProduceAuthenticatedUser() {
            var userInfo = userInfoProvider.get();
            assertNotNull(userInfo, "User info should not be null");
            assertTrue(userInfo.isAuthenticated(), "User should be authenticated");
            assertEquals(PortalAuthenticationFacadeMock.USER, userInfo.getDisplayName(),
                    "Display name should match mock user");
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests implements ShouldHandleObjectContracts<PortalUserProducer> {

        @Override
        public PortalUserProducer getUnderTest() {
            return underTest;
        }
    }
}
