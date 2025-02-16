# Log Messages for Portal Authentication OAuth

All messages follow the format: PORTAL_OAUTH-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PORTAL_OAUTH-001 | OAuth | OAuth configuration created: %s | Logged when oauth configuration is created |
| PORTAL_OAUTH-010 | OAuth | idToken can not be split: %s | Logged when ID token cannot be split |
| PORTAL_OAUTH-011 | OAuth | idToken %s can not be parsed | Logged when ID token cannot be parsed |

## WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PORTAL_OAUTH-100 | OAuth | Missing config for logout URI | Logged when logout URI is missing from config |
| PORTAL_OAUTH-101 | OAuth | OAuth config keys '%s' and/or '%s' not set, trying fallback | Logged when OAuth config keys are not set |
| PORTAL_OAUTH-102 | OAuth | Missing required parameters state or code | Logged when required parameters are missing |
| PORTAL_OAUTH-103 | OAuth | Invalid state parameter | Logged when state parameter is invalid |
| PORTAL_OAUTH-104 | OAuth | No scopes found in session | Logged when no scopes are found in session |
| PORTAL_OAUTH-105 | OAuth | No token received from OAuth provider | Logged when no token is received |
| PORTAL_OAUTH-106 | OAuth | No user info received from OAuth provider | Logged when no user info is received |
| PORTAL_OAUTH-107 | OAuth | Failed to request token from OAuth provider | Logged when token request fails |
| PORTAL_OAUTH-108 | OAuth | Failed to obtain client token | Logged when client token request fails |
| PORTAL_OAUTH-109 | OAuth | Failed to get user info from OAuth provider | Logged when user info request fails |
| PORTAL_OAUTH-110 | OAuth | Failed to redirect | Logged when redirect fails |
| PORTAL_OAUTH-111 | OAuth | Invalid scope: %s | Logged when scope is invalid |
| PORTAL_OAUTH-112 | OAuth | OAuth login error: %s | Logged when login error occurs |
| PORTAL_OAUTH-113 | OAuth | Unknown state: %s | Logged when state is unknown |
| PORTAL_OAUTH-114 | OAuth | could not get id-token. no user context available | Logged when ID token is not available |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PORTAL_OAUTH-200 | OAuth | Auto discovery of oauth config failed, using URI: %s | Logged when auto discovery fails |
| PORTAL_OAUTH-201 | OAuth | Failed to redirect to %s | Logged when redirect fails |
| PORTAL_OAUTH-202 | OAuth | Cannot generate code_challenge | Logged when code challenge generation fails |
| PORTAL_OAUTH-203 | OAuth | Failed to redirect to logout URL %s | Logged when logout redirect fails |
| PORTAL_OAUTH-204 | OAuth | Oauth2 token.expires_in not a valid number | Logged when token expiration is invalid |
| PORTAL_OAUTH-205 | OAuth | Invalid scope error: %s | Logged when scope is invalid |
| PORTAL_OAUTH-206 | OAuth | OAuth login error: %s | Logged when login error occurs |
| PORTAL_OAUTH-207 | OAuth | Unexpected login call with unknown state %s | Logged when unexpected login call occurs |
| PORTAL_OAUTH-208 | OAuth | Cannot generate code_challenge | Logged when code challenge generation fails |
| PORTAL_OAUTH-209 | OAuth | Token expires_in is not a valid number | Logged when token expiration is invalid |
| PORTAL_OAUTH-210 | OAuth | IO Exception occurred during OAuth operation | Logged when IO exception occurs |
| PORTAL_OAUTH-211 | OAuth | Error during authentication: %s - %s | Logged when authentication error occurs |