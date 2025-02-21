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

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo.BaseAuthenticatedUserInfoBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import lombok.experimental.Delegate;

/**
 * CDI producer for {@link AuthenticatedUserInfo} instances in test environments.
 * Provides a configurable user producer that allows fine-grained control over user
 * properties without requiring an actual authentication service.
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Produces pre-configured user instances</li>
 *   <li>Supports runtime user property modification</li>
 *   <li>Application-scoped for consistent state</li>
 *   <li>Named bean access via EL</li>
 *   <li>Delegate pattern for builder access</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * Basic injection:
 * <pre>
 * &#64;Inject
 * &#64;Named(PortalTestUserProducer.BEAN_NAME)
 * private AuthenticatedUserInfo user;
 *
 * void testUser() {
 *     assertEquals("user", user.getIdentifier());
 *     assertEquals("user", user.getDisplayName());
 * }
 * </pre>
 *
 * Customizing user properties:
 * <pre>
 * &#64;Inject
 * private PortalTestUserProducer userProducer;
 *
 * void setupCustomUser() {
 *     userProducer
 *         .displayName("John Doe")
 *         .identifier("jdoe")
 *         .email("john@example.com")
 *         .addRole("admin")
 *         .addAttribute("department", "IT");
 * }
 * </pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Application scoped to prevent inconsistent state</li>
 *   <li>Uses builder pattern via delegation</li>
 *   <li>Produces dependent-scoped instances</li>
 *   <li>Available as named bean "portalUser"</li>
 *   <li>Initializes with default "user" properties</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see AuthenticatedUserInfo
 * @see BaseAuthenticatedUserInfo
 */
@Named
@ApplicationScoped
public class PortalTestUserProducer {

    private static final String USER = "user";

    /**
     * "portalUser"
     */
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
    @Dependent
    AuthenticatedUserInfo produceAuthenticatedUserInfo() {
        return userInfoBuilder.build();
    }

}
