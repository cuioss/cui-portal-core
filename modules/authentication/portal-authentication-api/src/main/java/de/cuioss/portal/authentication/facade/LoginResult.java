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
import de.cuioss.uimodel.nameprovider.IDisplayNameProvider;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Represents the result of a login attempt. This is a sealed interface with two
 * variants: {@link Success} for successful logins and {@link Failure} for failed
 * login attempts.
 *
 * <p>Use {@link #isValid()} for simple boolean checks, or pattern matching for
 * accessing variant-specific data:
 * <pre>{@code
 * switch (loginResult) {
 *     case LoginResult.Success s -> handleSuccess(s.authenticatedUserInfo());
 *     case LoginResult.Failure f -> handleFailure(f.errorReason());
 * }
 * }</pre>
 *
 * @see AuthenticationResults
 */
public sealed interface LoginResult permits LoginResult.Success, LoginResult.Failure {

    /**
     * @return {@code true} if the login was successful, {@code false} otherwise.
     */
    boolean isValid();

    /**
     * Represents a successful login result containing the authenticated user info.
     *
     * @param authenticatedUserInfo the authenticated user, must not be null
     */
    record Success(AuthenticatedUserInfo authenticatedUserInfo) implements LoginResult {

        public Success {
            requireNonNull(authenticatedUserInfo);
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    /**
     * Represents a failed login result containing the error reason and optional context.
     *
     * @param errorReason the displayable error reason (i18n-ready), must not be null
     * @param username    the attempted username for audit purposes, may be null
     * @param cause       the optional underlying cause, may be null
     */
    record Failure(IDisplayNameProvider<?> errorReason,
            @Nullable
            String username,
            @Nullable
            Throwable cause) implements LoginResult {

        public Failure {
            requireNonNull(errorReason);
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }
}
