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

        public static final LogRecord ID_TOKEN_SPLIT_FAILED = LogRecordModel.builder()
                .template("idToken can not be split: %s")
                .prefix(PREFIX)
                .identifier(10)
                .build();

        public static final LogRecord ID_TOKEN_PARSE_FAILED = LogRecordModel.builder()
                .template("idToken %s can not be parsed")
                .prefix(PREFIX)
                .identifier(11)
                .build();
    }

    @UtilityClass
    public static final class WARN {
        public static final LogRecord NO_LOGOUT_URI = LogRecordModel.builder()
                .template("Missing config for logout URI. Check the end_session_endpoint property from userinfo endpoint.")
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

        public static final LogRecord NO_ID_TOKEN = LogRecordModel.builder()
                .template("could not get id-token. no user context available.")
                .prefix(PREFIX)
                .identifier(114)
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

        public static final LogRecord CODE_CHALLENGE_GENERATION_FAILED = LogRecordModel.builder()
                .template("Cannot generate code_challenge")
                .prefix(PREFIX)
                .identifier(201)
                .build();

        public static final LogRecord LOGOUT_FAILED = LogRecordModel.builder()
                .template("Failed to redirect to logout URL %s")
                .prefix(PREFIX)
                .identifier(202)
                .build();

        public static final LogRecord TOKEN_EXPIRES_IN_INVALID = LogRecordModel.builder()
                .template("Oauth2 token.expires_in not a valid number")
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
}
