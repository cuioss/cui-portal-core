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
package de.cuioss.portal.authentication.facade;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.UserStore;
import de.cuioss.uimodel.application.LoginCredentials;
import de.cuioss.uimodel.result.ResultObject;

/**
 * Extension to {@link AuthenticationFacade} that supports form-based logins
 *
 */
public interface FormBasedAuthenticationFacade extends AuthenticationFacade {

    /**
     * Authenticates a user with the provided credentials. The implementation is
     * responsible for invalidating the old and creating a new session.
     *
     * @param request     The current {@link HttpServletRequest} that needs to be
     *                    authenticated.
     * @param credentials The credentials for authentication
     * @return A {@link ResultObject} with the corresponding
     *         {@link AuthenticatedUserInfo} with
     *         {@link AuthenticatedUserInfo#isAuthenticated()} being {@code true} in
     *         case of successful logins, otherwise it will provide the
     *         corresponding error message to be displayed.
     */
    ResultObject<AuthenticatedUserInfo> login(HttpServletRequest request, LoginCredentials credentials);

    /**
     * Provides list of available systems. Please note that no particular ordering
     * is added on top. The content is displayed on ui level as provided.
     *
     * @return The map providing the available system data. Must not be null but may
     *         be empty.
     */
    List<UserStore> getAvailableUserStores();

}
