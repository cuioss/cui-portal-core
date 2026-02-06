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
package de.cuioss.portal.core.test.support;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo.BaseAuthenticatedUserInfoBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import lombok.experimental.Delegate;

/**
 * Replacement for instances of {@link AuthenticatedUserInfo} producer, enabling finer
 * control without using an underlying service. <em>Caution: </em> This bean is
 * application scoped in order not to introduce problems with improper scoping.
 *
 * @author Oliver Wolff
 */
@Named
@Alternative
@ApplicationScoped
public class PortalUserProducerMock {

    /**
     * "portalUser"
     */
    public static final String BEAN_NAME = "portalUser";

    @Delegate
    private final BaseAuthenticatedUserInfoBuilder userInfoBuilder = BaseAuthenticatedUserInfo.builder()
            .displayName("user").identifier("user").qualifiedIdentifier("user");

    /**
     * Sets an authenticated default user.
     */
    @PostConstruct
    public void init() {
        authenticated(true);
    }

    @Produces
    @Named(PortalUserProducerMock.BEAN_NAME)
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfoBuilder.build();
    }

}
