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
package de.cuioss.portal.authentication.oauth;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized message bundle for OAuth2 authentication module logging.
 * Provides structured log messages for OAuth2-related events and operations.
 *
 * <p>Message categories and identifier ranges:
 * <ul>
 *   <li>INFO (001-099): Normal operational events like configuration loading and token processing</li>
 *   <li>WARN (100-199): Potential issues that don't prevent operation but require attention</li>
 *   <li>ERROR (200-299): Errors that prevent normal operation or authentication flow</li>
 * </ul>
 *
 * <p>All messages use the prefix "PORTAL_OAUTH" for easy filtering and identification.
 *
 * @see de.cuioss.portal.authentication.oauth.impl.Oauth2AuthenticationFacadeImpl
 * @see de.cuioss.portal.authentication.oauth.impl.Oauth2ServiceImpl
 */
@UtilityClass
public class PortalAuthenticationOauthLogMessages {

    public static final String PREFIX = "PORTAL_OAUTH";

    @UtilityClass
    public static final class INFO {
        /**
         * Logged when a new OAuth configuration is successfully created.
         * Format: "OAuth configuration created: %s"
         * Parameters:
         * - Configuration details
         */
        public static final LogRecord CONFIG_CREATED = LogRecordModel.builder()
                .template("OAuth configuration created: %s")
                .prefix(PREFIX)
                .identifier(1)
                .build();

        /**
         * Logged when an ID token cannot be split into its components.
         * Format: "idToken can not be split: %s"
         * Parameters:
         * - Token value or error details
         */
        public static final LogRecord ID_TOKEN_SPLIT_FAILED = LogRecordModel.builder()
                .template("idToken can not be split: %s")
                .prefix(PREFIX)
                .identifier(10)
                .build();

        /**
         * Logged when an ID token cannot be parsed into claims.
         * Format: "idToken %s can not be parsed"
         * Parameters:
         * - Token identifier or value
         */
        public static final LogRecord ID_TOKEN_PARSE_FAILED = LogRecordModel.builder()
                .template("idToken %s can not be parsed")
                .prefix(PREFIX)
                .identifier(11)
                .build();
    }

    @UtilityClass
    public static final class WARN {
        /**
         * Logged when the logout URI configuration is missing.
         * Format: "Missing config for logout URI. Check the end_session_endpoint property from userinfo endpoint."
         */
        public static final LogRecord NO_LOGOUT_URI = LogRecordModel.builder()
                .template("Missing config for logout URI. Check the end_session_endpoint property from userinfo endpoint.")
                .prefix(PREFIX)
                .identifier(100)
                .build();

        /**
         * Logged when OAuth config keys are not set, trying fallback.
         * Format: "OAuth config keys '%s' and/or '%s' not set, trying fallback"
         * Parameters:
         * - Key names
         */
        public static final LogRecord CONFIG_KEYS_NOT_SET = LogRecordModel.builder()
                .template("OAuth config keys '%s' and/or '%s' not set, trying fallback")
                .prefix(PREFIX)
                .identifier(101)
                .build();

        /**
         * Logged when required parameters (state or code) are missing.
         * Format: "Missing required parameters state or code"
         */
        public static final LogRecord MISSING_PARAMETERS = LogRecordModel.builder()
                .template("Missing required parameters state or code")
                .prefix(PREFIX)
                .identifier(102)
                .build();

        /**
         * Logged when the state parameter is invalid.
         * Format: "Invalid state parameter"
         */
        public static final LogRecord INVALID_STATE = LogRecordModel.builder()
                .template("Invalid state parameter")
                .prefix(PREFIX)
                .identifier(103)
                .build();

        /**
         * Logged when no scopes are found in the session.
         * Format: "No scopes found in session"
         */
        public static final LogRecord NO_SCOPES_IN_SESSION = LogRecordModel.builder()
                .template("No scopes found in session")
                .prefix(PREFIX)
                .identifier(104)
                .build();

        /**
         * Logged when no token is received from the OAuth provider.
         * Format: "No token received from OAuth provider"
         */
        public static final LogRecord NO_TOKEN_RECEIVED = LogRecordModel.builder()
                .template("No token received from OAuth provider")
                .prefix(PREFIX)
                .identifier(105)
                .build();

        /**
         * Logged when no user info is received from the OAuth provider.
         * Format: "No user info received from OAuth provider"
         */
        public static final LogRecord NO_USERINFO_RECEIVED = LogRecordModel.builder()
                .template("No user info received from OAuth provider")
                .prefix(PREFIX)
                .identifier(106)
                .build();

        /**
         * Logged when requesting a token from the OAuth provider fails.
         * Format: "Failed to request token from OAuth provider"
         */
        public static final LogRecord REQUEST_TOKEN_FAILED = LogRecordModel.builder()
                .template("Failed to request token from OAuth provider")
                .prefix(PREFIX)
                .identifier(107)
                .build();

        /**
         * Logged when obtaining a client token fails.
         * Format: "Failed to obtain client token"
         */
        public static final LogRecord CLIENT_TOKEN_FAILED = LogRecordModel.builder()
                .template("Failed to obtain client token")
                .prefix(PREFIX)
                .identifier(108)
                .build();

        /**
         * Logged when getting user info from the OAuth provider fails.
         * Format: "Failed to get user info from OAuth provider"
         */
        public static final LogRecord GET_USERINFO_FAILED = LogRecordModel.builder()
                .template("Failed to get user info from OAuth provider")
                .prefix(PREFIX)
                .identifier(109)
                .build();

        /**
         * Logged when redirecting fails.
         * Format: "Failed to redirect"
         */
        public static final LogRecord REDIRECT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect")
                .prefix(PREFIX)
                .identifier(110)
                .build();

        /**
         * Logged when an invalid scope is encountered.
         * Format: "Invalid scope: %s"
         * Parameters:
         * - Scope value
         */
        public static final LogRecord INVALID_SCOPE = LogRecordModel.builder()
                .template("Invalid scope: %s")
                .prefix(PREFIX)
                .identifier(111)
                .build();

        /**
         * Logged when an OAuth login error occurs.
         * Format: "OAuth login error: %s"
         * Parameters:
         * - Error details
         */
        public static final LogRecord LOGIN_ERROR = LogRecordModel.builder()
                .template("OAuth login error: %s")
                .prefix(PREFIX)
                .identifier(112)
                .build();

        /**
         * Logged when an unknown state is encountered.
         * Format: "Unknown state: %s"
         * Parameters:
         * - State value
         */
        public static final LogRecord UNKNOWN_STATE = LogRecordModel.builder()
                .template("Unknown state: %s")
                .prefix(PREFIX)
                .identifier(113)
                .build();

        /**
         * Logged when no ID token is available.
         * Format: "could not get id-token. no user context available."
         */
        public static final LogRecord NO_ID_TOKEN = LogRecordModel.builder()
                .template("could not get id-token. no user context available.")
                .prefix(PREFIX)
                .identifier(114)
                .build();
    }

    @UtilityClass
    public static final class ERROR {
        /**
         * Logged when auto-discovery of OAuth config fails.
         * Format: "Auto discovery of oauth config failed, using URI: %s"
         * Parameters:
         * - URI value
         */
        public static final LogRecord DISCOVERY_FAILED = LogRecordModel.builder()
                .template("Auto discovery of oauth config failed, using URI: %s")
                .prefix(PREFIX)
                .identifier(200)
                .build();

        /**
         * Logged when redirecting to a specific URI fails.
         * Format: "Failed to redirect to %s"
         * Parameters:
         * - URI value
         */
        public static final LogRecord REDIRECT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect to %s")
                .prefix(PREFIX)
                .identifier(201)
                .build();

        /**
         * Logged when generating a code challenge fails.
         * Format: "Cannot generate code_challenge"
         */
        public static final LogRecord CODE_CHALLENGE_GENERATION_FAILED = LogRecordModel.builder()
                .template("Cannot generate code_challenge")
                .prefix(PREFIX)
                .identifier(201)
                .build();

        /**
         * Logged when logging out fails.
         * Format: "Failed to redirect to logout URL %s"
         * Parameters:
         * - Logout URL
         */
        public static final LogRecord LOGOUT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect to logout URL %s")
                .prefix(PREFIX)
                .identifier(202)
                .build();

        /**
         * Logged when the token expires_in value is not a valid number.
         * Format: "Oauth2 token.expires_in not a valid number"
         */
        public static final LogRecord TOKEN_EXPIRES_IN_INVALID = LogRecordModel.builder()
                .template("Oauth2 token.expires_in not a valid number")
                .prefix(PREFIX)
                .identifier(202)
                .build();

        /**
         * Logged when an invalid scope error occurs.
         * Format: "Invalid scope error: %s"
         * Parameters:
         * - Error details
         */
        public static final LogRecord INVALID_SCOPE = LogRecordModel.builder()
                .template("Invalid scope error: %s")
                .prefix(PREFIX)
                .identifier(203)
                .build();

        /**
         * Logged when an OAuth login error occurs.
         * Format: "OAuth login error: %s"
         * Parameters:
         * - Error details
         */
        public static final LogRecord LOGIN_ERROR = LogRecordModel.builder()
                .template("OAuth login error: %s")
                .prefix(PREFIX)
                .identifier(204)
                .build();

        /**
         * Logged when an unexpected login call with an unknown state occurs.
         * Format: "Unexpected login call with unknown state %s, redirecting to login"
         * Parameters:
         * - State value
         */
        public static final LogRecord UNEXPECTED_LOGIN_CALL = LogRecordModel.builder()
                .template("Unexpected login call with unknown state %s, redirecting to login")
                .prefix(PREFIX)
                .identifier(205)
                .build();

        /**
         * Logged when generating a code challenge fails.
         * Format: "Cannot generate code_challenge"
         */
        public static final LogRecord CANNOT_GENERATE_CODE_CHALLENGE = LogRecordModel.builder()
                .template("Cannot generate code_challenge")
                .prefix(PREFIX)
                .identifier(206)
                .build();

        /**
         * Logged when the token expires_in value is not a valid number.
         * Format: "Token expires_in is not a valid number"
         */
        public static final LogRecord TOKEN_EXPIRES_IN_NOT_A_VALID_NUMBER = LogRecordModel.builder()
                .template("Token expires_in is not a valid number")
                .prefix(PREFIX)
                .identifier(207)
                .build();

        /**
         * Logged when an IO exception occurs during an OAuth operation.
         * Format: "IO Exception occurred during OAuth operation"
         */
        public static final LogRecord IO_EXCEPTION = LogRecordModel.builder()
                .template("IO Exception occurred during OAuth operation")
                .prefix(PREFIX)
                .identifier(208)
                .build();

        /**
         * Logged when an error occurs during authentication.
         * Format: "Error during authentication: %s - %s"
         * Parameters:
         * - Error details
         */
        public static final LogRecord ERROR_DURING_AUTHENTICATION = LogRecordModel.builder()
                .template("Error during authentication: %s - %s")
                .prefix(PREFIX)
                .identifier(209)
                .build();
    }
}
