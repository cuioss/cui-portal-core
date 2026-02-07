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
package de.cuioss.portal.authentication.facade;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo;
import de.cuioss.uimodel.nameprovider.DisplayName;
import de.cuioss.uimodel.nameprovider.LabeledKey;
import lombok.experimental.UtilityClass;

/**
 * Provides constants for all authentication-modules as there are
 * {@link AuthenticatedUserInfo} for not logged-in user and keys for the
 * resource bundles, defined at cdi-portal-core-impl module
 *
 * @author Oliver Wolff
 */
@UtilityClass
public class AuthenticationResults {

    private static final String NOT_LOGGED_IN_MESSAGE = "Not logged in";

    /**
     * An {@link AuthenticatedUserInfo} with
     * {@link AuthenticatedUserInfo#isAuthenticated()} returning false
     */
    public static final AuthenticatedUserInfo NOT_LOGGED_IN = BaseAuthenticatedUserInfo.builder()
            .displayName(NOT_LOGGED_IN_MESSAGE).authenticated(false).build();

    /**
     * The key for the message "The credentials you provided are not complete".
     */
    public static final String KEY_INCOMPLETE_CREDENTIALS = "portal.authentication.error.incomplete_credentials";

    /**
     * The key for the message "The system is not configured properly, contact your
     * administrator".
     */
    public static final String KEY_INVALID_CONFIGURATION = "portal.authentication.error.invalid_configuration";

    /**
     * The key for the message "Cannot log in with the supplied user details. Please
     * check your inputs".
     */
    public static final String KEY_INVALID_CREDENTIALS = "page.login.message.loginFailure";

    /**
     * The key for the general Cannot log in message.
     */
    public static final String KEY_UNABLE_TO_LOGIN = "portal.authentication.error.unable_to_login";

    /**
     * {@link LoginResult} representing a failed login due to incomplete credentials,
     * using the key {@link #KEY_INCOMPLETE_CREDENTIALS}.
     */
    public static final LoginResult RESULT_INCOMPLETE_CREDENTIALS = invalidResultKey(
            KEY_INCOMPLETE_CREDENTIALS, null, null);

    /**
     * {@link LoginResult} representing a failed login due to invalid configuration,
     * using the key {@link #KEY_INVALID_CONFIGURATION}.
     */
    public static final LoginResult RESULT_INVALID_CONFIGURATION = invalidResultKey(
            KEY_INVALID_CONFIGURATION, null, null);

    /**
     * @param reason   the text to be displayed as reason.
     * @param username the username entered by the user (e.g., to be added to the
     *                 atna event)
     * @param cause    the optional throwable to be wrapped
     *
     * @return {@link LoginResult.Failure} with the given reason as a displayable
     *         message
     */
    public static LoginResult invalidResult(final String reason, final String username,
            final Throwable cause) {
        return new LoginResult.Failure(new DisplayName(reason), username, cause);
    }

    /**
     * @param reasonKey the key for resolving the reason text.
     * @param username  the username entered by the user (e.g., to be added to the
     *                  atna event)
     * @param cause     the optional throwable to be wrapped
     *
     * @return {@link LoginResult.Failure} with the given reason key as a resolvable
     *         message
     */
    public static LoginResult invalidResultKey(final String reasonKey,
            final String username, final Throwable cause) {
        return new LoginResult.Failure(new LabeledKey(reasonKey), username, cause);
    }

    /**
     * @param userInfo must not be null and should be an authenticated User
     *
     * @return {@link LoginResult.Success} wrapping the given userInfo
     */
    public static LoginResult validResult(final AuthenticatedUserInfo userInfo) {
        return new LoginResult.Success(userInfo);
    }
}
