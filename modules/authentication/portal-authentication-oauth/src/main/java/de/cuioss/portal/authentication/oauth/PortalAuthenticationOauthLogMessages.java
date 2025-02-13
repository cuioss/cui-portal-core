package de.cuioss.portal.authentication.oauth;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PortalAuthenticationOauthLogMessages {

    public static final String PREFIX = "PORTAL_OAUTH";

    @UtilityClass
    public static final class INFO {
        public static final LogRecord CONFIG_CREATED = LogRecordModel.builder()
                .template("OAuth configuration created: %s")
                .prefix(PREFIX)
                .identifier(1)
                .build();
    }

    @UtilityClass
    public static final class WARN {
        public static final LogRecord NO_LOGOUT_URI = LogRecordModel.builder()
                .template("No logout URI configured")
                .prefix(PREFIX)
                .identifier(100)
                .build();

        public static final LogRecord CONFIG_KEYS_NOT_SET = LogRecordModel.builder()
                .template("OAuth config keys '%s' and/or '%s' not set, trying fallback")
                .prefix(PREFIX)
                .identifier(101)
                .build();

        public static final LogRecord MISSING_PARAMETERS = LogRecordModel.builder()
                .template("Missing required parameters state or code")
                .prefix(PREFIX)
                .identifier(102)
                .build();

        public static final LogRecord INVALID_STATE = LogRecordModel.builder()
                .template("Invalid state parameter")
                .prefix(PREFIX)
                .identifier(103)
                .build();

        public static final LogRecord NO_SCOPES_IN_SESSION = LogRecordModel.builder()
                .template("No scopes found in session")
                .prefix(PREFIX)
                .identifier(104)
                .build();

        public static final LogRecord NO_TOKEN_RECEIVED = LogRecordModel.builder()
                .template("No token received from OAuth provider")
                .prefix(PREFIX)
                .identifier(105)
                .build();

        public static final LogRecord NO_USERINFO_RECEIVED = LogRecordModel.builder()
                .template("No user info received from OAuth provider")
                .prefix(PREFIX)
                .identifier(106)
                .build();

        public static final LogRecord REQUEST_TOKEN_FAILED = LogRecordModel.builder()
                .template("Failed to request token from OAuth provider")
                .prefix(PREFIX)
                .identifier(107)
                .build();

        public static final LogRecord CLIENT_TOKEN_FAILED = LogRecordModel.builder()
                .template("Failed to obtain client token")
                .prefix(PREFIX)
                .identifier(108)
                .build();

        public static final LogRecord GET_USERINFO_FAILED = LogRecordModel.builder()
                .template("Failed to get user info from OAuth provider")
                .prefix(PREFIX)
                .identifier(109)
                .build();

        public static final LogRecord REDIRECT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect")
                .prefix(PREFIX)
                .identifier(110)
                .build();

        public static final LogRecord INVALID_SCOPE = LogRecordModel.builder()
                .template("Invalid scope: %s")
                .prefix(PREFIX)
                .identifier(111)
                .build();

        public static final LogRecord LOGIN_ERROR = LogRecordModel.builder()
                .template("OAuth login error: %s")
                .prefix(PREFIX)
                .identifier(112)
                .build();

        public static final LogRecord UNKNOWN_STATE = LogRecordModel.builder()
                .template("Unknown state: %s")
                .prefix(PREFIX)
                .identifier(113)
                .build();
    }

    @UtilityClass
    public static final class ERROR {
        public static final LogRecord DISCOVERY_FAILED = LogRecordModel.builder()
                .template("Auto discovery of oauth config failed, using URI: %s")
                .prefix(PREFIX)
                .identifier(200)
                .build();

        public static final LogRecord REDIRECT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect to %s")
                .prefix(PREFIX)
                .identifier(201)
                .build();

        public static final LogRecord LOGOUT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect to logout URL %s")
                .prefix(PREFIX)
                .identifier(202)
                .build();

        public static final LogRecord INVALID_SCOPE = LogRecordModel.builder()
                .template("Invalid scope error: %s")
                .prefix(PREFIX)
                .identifier(203)
                .build();

        public static final LogRecord LOGIN_ERROR = LogRecordModel.builder()
                .template("OAuth login error: %s")
                .prefix(PREFIX)
                .identifier(204)
                .build();

        public static final LogRecord UNEXPECTED_LOGIN_CALL = LogRecordModel.builder()
                .template("Unexpected login call with unknown state %s, redirecting to login")
                .prefix(PREFIX)
                .identifier(205)
                .build();

        public static final LogRecord CANNOT_GENERATE_CODE_CHALLENGE = LogRecordModel.builder()
                .template("Cannot generate code_challenge")
                .prefix(PREFIX)
                .identifier(206)
                .build();

        public static final LogRecord TOKEN_EXPIRES_IN_NOT_A_VALID_NUMBER = LogRecordModel.builder()
                .template("Token expires_in is not a valid number")
                .prefix(PREFIX)
                .identifier(207)
                .build();

        public static final LogRecord IO_EXCEPTION = LogRecordModel.builder()
                .template("IO Exception occurred during OAuth operation")
                .prefix(PREFIX)
                .identifier(208)
                .build();

        public static final LogRecord ERROR_DURING_AUTHENTICATION = LogRecordModel.builder()
                .template("Error during authentication: %s - %s")
                .prefix(PREFIX)
                .identifier(209)
                .build();
    }

    @UtilityClass
    public static final class DEBUG {
        public static final LogRecord USING_DISCOVERY_URI = LogRecordModel.builder()
                .template("Using discoveryURI %s")
                .prefix(PREFIX)
                .identifier(500)
                .build();

        public static final LogRecord CALLING_REDIRECT = LogRecordModel.builder()
                .template("Calling redirect to %s")
                .prefix(PREFIX)
                .identifier(501)
                .build();

        public static final LogRecord CALLING_LOGOUT = LogRecordModel.builder()
                .template("Calling logout with URL %s")
                .prefix(PREFIX)
                .identifier(502)
                .build();

        public static final LogRecord USER_AUTHENTICATED = LogRecordModel.builder()
                .template("User successfully authenticated")
                .prefix(PREFIX)
                .identifier(503)
                .build();

        public static final LogRecord AUTHENTICATION_RESULT = LogRecordModel.builder()
                .template("Authentication result: %s")
                .prefix(PREFIX)
                .identifier(504)
                .build();

        public static final LogRecord USERINFO_RETRIEVED = LogRecordModel.builder()
                .template("User info retrieved from OAuth provider")
                .prefix(PREFIX)
                .identifier(505)
                .build();

        public static final LogRecord ERROR_PARAMETER = LogRecordModel.builder()
                .template("Error parameter present: %s")
                .prefix(PREFIX)
                .identifier(506)
                .build();

        public static final LogRecord NEW_TOKEN_RETRIEVED = LogRecordModel.builder()
                .template("New token retrieved from OAuth provider")
                .prefix(PREFIX)
                .identifier(507)
                .build();

        public static final LogRecord CODE_AND_STATE_PARAMETER = LogRecordModel.builder()
                .template("Code and state parameters are present")
                .prefix(PREFIX)
                .identifier(508)
                .build();

        public static final LogRecord OVERWRITE_TOKEN_URL = LogRecordModel.builder()
                .template("Overwriting token URL with: %s")
                .prefix(PREFIX)
                .identifier(509)
                .build();

        public static final LogRecord STATE_PARAMETER_MATCHES = LogRecordModel.builder()
                .template("State parameter matches stored value")
                .prefix(PREFIX)
                .identifier(510)
                .build();

        public static final LogRecord OVERWRITE_USERINFO_URL = LogRecordModel.builder()
                .template("Overwriting userinfo URL with: %s")
                .prefix(PREFIX)
                .identifier(511)
                .build();

        public static final LogRecord STATE_PARAMETER_DIFFERS = LogRecordModel.builder()
                .template("State parameter %s differs from stored value %s")
                .prefix(PREFIX)
                .identifier(512)
                .build();

        public static final LogRecord CODE_VERIFIER = LogRecordModel.builder()
                .template("Code verifier: %s")
                .prefix(PREFIX)
                .identifier(513)
                .build();

        public static final LogRecord AUTHENTICATED_OAUTH_USER_INFO = LogRecordModel.builder()
                .template("Authenticated oauth user info was retrieved: %s")
                .prefix(PREFIX)
                .identifier(514)
                .build();

        public static final LogRecord INVALIDATE_SESSION = LogRecordModel.builder()
                .template("Session user missing or not authenticated, invalidating session")
                .prefix(PREFIX)
                .identifier(515)
                .build();

        public static final LogRecord ADD_OAUTH_USER_TO_SESSION = LogRecordModel.builder()
                .template("Adding oauth user to session")
                .prefix(PREFIX)
                .identifier(516)
                .build();

        public static final LogRecord UNABLE_TO_RETRIEVE_AUTHENTICATED_USER_INFO = LogRecordModel.builder()
                .template("Unable to retrieve authenticated user info")
                .prefix(PREFIX)
                .identifier(517)
                .build();

        public static final LogRecord GET_ATTRIBUTE_FAILED = LogRecordModel.builder()
                .template("Failed to get attribute from session")
                .prefix(PREFIX)
                .identifier(518)
                .build();

        public static final LogRecord NEW_CODE = LogRecordModel.builder()
                .template("Generated new code")
                .prefix(PREFIX)
                .identifier(519)
                .build();

        public static final LogRecord CODE = LogRecordModel.builder()
                .template("Code: %s")
                .prefix(PREFIX)
                .identifier(520)
                .build();

        public static final LogRecord CODE_CHALLENGE = LogRecordModel.builder()
                .template("Code challenge: %s")
                .prefix(PREFIX)
                .identifier(521)
                .build();

        public static final LogRecord REDIRECT_URL = LogRecordModel.builder()
                .template("Redirect URL: %s")
                .prefix(PREFIX)
                .identifier(522)
                .build();

        public static final LogRecord RETRIEVE_TOKEN_FOR_SCOPES = LogRecordModel.builder()
                .template("Retrieving token for scopes: %s")
                .prefix(PREFIX)
                .identifier(523)
                .build();

        public static final LogRecord USER_PRESENT = LogRecordModel.builder()
                .template("User is present")
                .prefix(PREFIX)
                .identifier(524)
                .build();

        public static final LogRecord ACCESS_TOKEN_PRESENT = LogRecordModel.builder()
                .template("Access token present: %s")
                .prefix(PREFIX)
                .identifier(525)
                .build();

        public static final LogRecord CUI_TOKEN_PRESENT = LogRecordModel.builder()
                .template("CUI Token present")
                .prefix(PREFIX)
                .identifier(526)
                .build();

        public static final LogRecord NO_CUI_TOKEN_AVAILABLE = LogRecordModel.builder()
                .template("No CUI Token available")
                .prefix(PREFIX)
                .identifier(527)
                .build();

        public static final LogRecord ACCESS_TOKEN_NOT_PRESENT = LogRecordModel.builder()
                .template("Access token not present, redirecting to oauth server using idToken=%s")
                .prefix(PREFIX)
                .identifier(528)
                .build();

        public static final LogRecord USING_ID_TOKEN = LogRecordModel.builder()
                .template("Using ID token: %s")
                .prefix(PREFIX)
                .identifier(529)
                .build();

        public static final LogRecord ID_TOKEN_CANNOT_BE_SPLIT = LogRecordModel.builder()
                .template("ID token cannot be split: %s")
                .prefix(PREFIX)
                .identifier(530)
                .build();

        public static final LogRecord ID_TOKEN_CANNOT_BE_PARSED = LogRecordModel.builder()
                .template("ID token cannot be parsed: %s")
                .prefix(PREFIX)
                .identifier(531)
                .build();

        public static final LogRecord TOKEN_IS_VALID = LogRecordModel.builder()
                .template("Token is valid")
                .prefix(PREFIX)
                .identifier(532)
                .build();

        public static final LogRecord MISSING_SCOPE = LogRecordModel.builder()
                .template("Missing scope: %s")
                .prefix(PREFIX)
                .identifier(533)
                .build();

        public static final LogRecord ACCESS_TOKEN_EXPIRED = LogRecordModel.builder()
                .template("Access token expired, but refresh token present; trying to use it to get a new access token")
                .prefix(PREFIX)
                .identifier(534)
                .build();

        public static final LogRecord TOKEN_HAS_NO_EXPIRATION = LogRecordModel.builder()
                .template("Token has no expiration, token is valid")
                .prefix(PREFIX)
                .identifier(535)
                .build();

        public static final LogRecord CHECKED_EXPIRE_TIME = LogRecordModel.builder()
                .template("Checked expire time, token valid: %s")
                .prefix(PREFIX)
                .identifier(536)
                .build();

        public static final LogRecord CREATING_AUTH_USER_INFO = LogRecordModel.builder()
                .template("Creating authenticated user info with scopes: %s, tokenUri: %s, redirectUri: %s")
                .prefix(PREFIX)
                .identifier(537)
                .build();

        public static final LogRecord NO_TOKEN_RECEIVED = LogRecordModel.builder()
                .template("No token received")
                .prefix(PREFIX)
                .identifier(538)
                .build();
    }

    @UtilityClass
    public static final class TRACE {
        public static final LogRecord TOKEN_DETAILS = LogRecordModel.builder()
                .template("Token details: %s")
                .prefix(PREFIX)
                .identifier(600)
                .build();

        public static final LogRecord USERINFO_DETAILS = LogRecordModel.builder()
                .template("User info details: %s")
                .prefix(PREFIX)
                .identifier(601)
                .build();

        public static final LogRecord TOKEN_RECEIVED = LogRecordModel.builder()
                .template("Token received from OAuth provider")
                .prefix(PREFIX)
                .identifier(602)
                .build();

        public static final LogRecord RETRIEVING_USERINFO = LogRecordModel.builder()
                .template("Retrieving user info from OAuth provider")
                .prefix(PREFIX)
                .identifier(603)
                .build();

        public static final LogRecord USERINFO_CONTENT = LogRecordModel.builder()
                .template("User info content: %s")
                .prefix(PREFIX)
                .identifier(604)
                .build();

        public static final LogRecord NEW_TOKEN_CONTENT = LogRecordModel.builder()
                .template("New token content: %s")
                .prefix(PREFIX)
                .identifier(605)
                .build();

        public static final LogRecord CODE_VERIFIER = LogRecordModel.builder()
                .template("Code verifier: %s")
                .prefix(PREFIX)
                .identifier(606)
                .build();

        public static final LogRecord NEW_CODE = LogRecordModel.builder()
                .template("New code generated")
                .prefix(PREFIX)
                .identifier(607)
                .build();

        public static final LogRecord CODE = LogRecordModel.builder()
                .template("Code: %s")
                .prefix(PREFIX)
                .identifier(608)
                .build();

        public static final LogRecord CODE_CHALLENGE = LogRecordModel.builder()
                .template("Code challenge: %s")
                .prefix(PREFIX)
                .identifier(609)
                .build();

        public static final LogRecord RETRIEVE_TOKEN_FOR_SCOPES = LogRecordModel.builder()
                .template("Retrieving token for scopes: %s")
                .prefix(PREFIX)
                .identifier(610)
                .build();

        public static final LogRecord USING_ID_TOKEN = LogRecordModel.builder()
                .template("Using ID token: %s")
                .prefix(PREFIX)
                .identifier(611)
                .build();
    }
}
