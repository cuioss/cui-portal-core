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

## DEBUG Level (500-599)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PORTAL_OAUTH-500 | OAuth | Using discoveryURI %s | Logged when using discovery URI |
| PORTAL_OAUTH-501 | OAuth | Calling redirect to %s | Logged when calling redirect |
| PORTAL_OAUTH-502 | OAuth | Calling logout with URL %s | Logged when calling logout |
| PORTAL_OAUTH-503 | OAuth | User successfully authenticated | Logged when user is authenticated |
| PORTAL_OAUTH-504 | OAuth | Authentication result: %s | Logged with authentication result |
| PORTAL_OAUTH-505 | OAuth | User info retrieved from OAuth provider | Logged when user info is retrieved |
| PORTAL_OAUTH-506 | OAuth | Error parameter present: %s | Logged when error parameter exists |
| PORTAL_OAUTH-507 | OAuth | New token retrieved from OAuth provider | Logged when new token is retrieved |
| PORTAL_OAUTH-508 | OAuth | Code and state parameters are present | Logged when code and state parameters exist |
| PORTAL_OAUTH-509 | OAuth | Overwriting token URL with: %s | Logged when token URL is overwritten |
| PORTAL_OAUTH-510 | OAuth | State parameter matches stored value | Logged when state parameter matches |
| PORTAL_OAUTH-511 | OAuth | Overwriting userinfo URL with: %s | Logged when userinfo URL is overwritten |
| PORTAL_OAUTH-512 | OAuth | State parameter %s differs from stored value %s | Logged when state parameter differs |
| PORTAL_OAUTH-513 | OAuth | Code verifier: %s | Logged with code verifier value |
| PORTAL_OAUTH-514 | OAuth | Authenticated oauth user info was retrieved: %s | Logged when user info is retrieved |
| PORTAL_OAUTH-515 | OAuth | Session user missing or not authenticated | Logged when session needs invalidation |
| PORTAL_OAUTH-516 | OAuth | Adding oauth user to session | Logged when adding user to session |
| PORTAL_OAUTH-517 | OAuth | Unable to retrieve authenticated user info | Logged when user info retrieval fails |
| PORTAL_OAUTH-518 | OAuth | Failed to get attribute from session | Logged when session attribute access fails |
| PORTAL_OAUTH-519 | OAuth | Generated new code | Logged when new code is generated |
| PORTAL_OAUTH-520 | OAuth | Code: %s | Logged with code value |
| PORTAL_OAUTH-521 | OAuth | Code challenge: %s | Logged with code challenge value |
| PORTAL_OAUTH-522 | OAuth | Redirect URL: %s | Logged with redirect URL |
| PORTAL_OAUTH-523 | OAuth | Retrieving token for scopes: %s | Logged when retrieving token |
| PORTAL_OAUTH-524 | OAuth | User is present | Logged when user is present |
| PORTAL_OAUTH-525 | OAuth | Access token present: %s | Logged when access token exists |
| PORTAL_OAUTH-526 | OAuth | CUI Token present | Logged when CUI token exists |
| PORTAL_OAUTH-527 | OAuth | No CUI Token available | Logged when CUI token is missing |
| PORTAL_OAUTH-528 | OAuth | Access token not present, redirecting using idToken=%s | Logged when access token is missing |
| PORTAL_OAUTH-529 | OAuth | Using ID token: %s | Logged when using ID token |
| PORTAL_OAUTH-530 | OAuth | ID token cannot be split: %s | Logged when ID token split fails |
| PORTAL_OAUTH-531 | OAuth | ID token cannot be parsed: %s | Logged when ID token parse fails |
| PORTAL_OAUTH-532 | OAuth | Token is valid | Logged when token validation succeeds |
| PORTAL_OAUTH-533 | OAuth | Missing scope: %s | Logged when scope is missing |
| PORTAL_OAUTH-534 | OAuth | Access token expired, trying refresh token | Logged when refreshing expired token |
| PORTAL_OAUTH-535 | OAuth | Token has no expiration, token is valid | Logged when token has no expiration |
| PORTAL_OAUTH-536 | OAuth | Checked expire time, token valid: %s | Logged when checking token expiration |
| PORTAL_OAUTH-537 | OAuth | Creating auth user info with scopes: %s, tokenUri: %s, redirectUri: %s | Logged when creating auth user info |
| PORTAL_OAUTH-538 | OAuth | No token received | Logged when no token is received |
| PORTAL_OAUTH-539 | OAuth | token.expires_in not a valid number | Logged when token expiration is invalid |
| PORTAL_OAUTH-540 | OAuth | Adding id-token-hint as recommended by spec | Logged when adding ID token hint |

## TRACE Level (600-699)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PORTAL_OAUTH-600 | OAuth | Token details: %s | Logged with token details |
| PORTAL_OAUTH-601 | OAuth | User info details: %s | Logged with user info details |
| PORTAL_OAUTH-602 | OAuth | Token received from OAuth provider | Logged when token is received |
| PORTAL_OAUTH-603 | OAuth | Retrieving user info from OAuth provider | Logged when retrieving user info |
| PORTAL_OAUTH-604 | OAuth | User info content: %s | Logged with user info content |
| PORTAL_OAUTH-605 | OAuth | New token content: %s | Logged with new token content |
| PORTAL_OAUTH-606 | OAuth | Code verifier: %s | Logged with code verifier |
| PORTAL_OAUTH-607 | OAuth | New code generated | Logged when new code is generated |
| PORTAL_OAUTH-608 | OAuth | Code: %s | Logged with code value |
| PORTAL_OAUTH-609 | OAuth | Code challenge: %s | Logged with code challenge value |
| PORTAL_OAUTH-610 | OAuth | Retrieving token for scopes: %s | Logged when retrieving token |
| PORTAL_OAUTH-611 | OAuth | Using ID token: %s | Logged when using ID token |
