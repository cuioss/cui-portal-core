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
package de.cuioss.portal.authentication;

import de.cuioss.tools.logging.CuiLogger;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

import static de.cuioss.portal.authentication.PortalAuthenticationLogMessages.AUTH;

/**
 * To signal successful / failed login and logout events (e.g., to the audit-logger).
 */
@Value
@Builder
public class LoginEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = -2436530653889693514L;

    /**
     * Identifies the concrete action that was executed
     */
    public enum Action {
        /**
         * Login was successful.
         */
        LOGIN_SUCCESS,

        /**
         * Login failed.
         */
        LOGIN_FAILED,
        /**
         * User logged out.
         */
        LOGOUT
    }

    // Only needed for LOGIN_FAILED
    String username;

    @NonNull
    Action action;

    private static final CuiLogger LOGGER = new CuiLogger(LoginEvent.class);

    /**
     * Logs the login event based on the action type.
     */
    public void logEvent() {
        switch (action) {
            case LOGIN_SUCCESS:
                LOGGER.info(AUTH.INFO.LOGIN_SUCCESS.format(username));
                break;
            case LOGIN_FAILED:
                LOGGER.warn(AUTH.WARN.LOGIN_FAILED.format(username));
                break;
            case LOGOUT:
                LOGGER.info(AUTH.INFO.LOGOUT.format(username));
                break;
            default:
                throw new IllegalStateException("Unknown action: " + action);
        }
    }
}
