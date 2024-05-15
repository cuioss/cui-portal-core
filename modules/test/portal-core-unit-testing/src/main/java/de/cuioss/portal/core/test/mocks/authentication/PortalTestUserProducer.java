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
package de.cuioss.portal.core.test.mocks.authentication;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUser;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo.BaseAuthenticatedUserInfoBuilder;
import jakarta.annotation.PostConstruct;
import lombok.experimental.Delegate;

/**
 * Replacement for instances of {@link PortalUser} producer, enabling finer
 * control without using an underlying service. <em>Caution: </em> This bean is
 * application scoped in order not to introduce problems with improper scoping.
 *
 * @author Oliver Wolff
 */
@Named
@ApplicationScoped
public class PortalTestUserProducer {

    private static final String USER = "user";

    /** "portalUser" */
    public static final String BEAN_NAME = "portalUser";

    @Delegate
    private final BaseAuthenticatedUserInfoBuilder userInfoBuilder = BaseAuthenticatedUserInfo.builder()
            .displayName(USER).identifier(USER).qualifiedIdentifier(USER);

    /**
     * Sets an authenticated default user.
     */
    @PostConstruct
    public void init() {
        authenticated(true);
    }

    @SuppressWarnings("cdi-ambiguous-name")
    @Produces
    @Named(PortalTestUserProducer.BEAN_NAME)
    @PortalUser
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfoBuilder.build();
    }

}
