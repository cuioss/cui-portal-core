# Log Messages for Portal Authentication OAuth

All messages follow the format: PortalAuthOauth-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalAuthOauth-001 | OAuth | OAuth configuration created: %s | Logged when oauth configuration is created successfully |

## WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalAuthOauth-100 | Oauth2Service | Portal-106: Retrieving request token failed | Logged when retrieving request token fails during authentication |
| PortalAuthOauth-101 | Oauth2Service | Portal-107: Get userinfo failed | Logged when retrieving user information fails after successful token acquisition |
| PortalAuthOauth-102 | Oauth2Service | Portal-135: Retrieving client token failed | Logged when retrieving client token fails |
| PortalAuthOauth-103 | OAuth | Oauth config key '%s' and/or '%s' not set, trying fallback | Logged when OAuth config keys are not set |
| PortalAuthOauth-104 | OAuth | Portal-146: Oauth2 sendRedirect failed | Logged when OAuth redirect fails |
| PortalAuthOauth-105 | OAuth | Portal-147: Oauth2 login error: %s | Logged when OAuth login error occurs |
| PortalAuthOauth-106 | OAuth | Portal-148: Oauth2 unexpected login call with unknown state %s, redirecting to login | Logged when unexpected login call with unknown state occurs |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalAuthOauth-200 | Oauth2Service | Portal-540: IO Exception during request | Logged when an IO exception occurs during an OAuth request |
| PortalAuthOauth-201 | OAuth | Auto discovery of oauth config failed, using URI: %s | Logged when auto discovery of OAuth config fails |
| PortalAuthOauth-202 | OAuth | Facade failed to retrieve token | Logged when facade fails to retrieve token |

## DEBUG Level (500-599)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalAuthOauth-500 | Oauth2Service | Creating auth user info with scopes='%s', tokenUri='%s', redirectUri='%s' | Logged when starting to create authenticated user info |
| PortalAuthOauth-501 | Oauth2Service | Successfully retrieved userinfo | Logged when user information is successfully retrieved |
| PortalAuthOauth-502 | Oauth2Service | Successfully retrieved new token | Logged when a new token is successfully retrieved |
| PortalAuthOauth-503 | Oauth2Service | No token received | Logged when no token is received during token refresh |
| PortalAuthOauth-504 | OAuth | Using discoveryURI %s | Logged when using discovery URI |
| PortalAuthOauth-505 | OAuth | overwrite well-known token-url '%s' with: %s | Logged when overwriting well-known token URL |
| PortalAuthOauth-506 | OAuth | overwrite well-known userinfo-url '%s' with: %s | Logged when overwriting well-known userinfo URL |
| PortalAuthOauth-507 | OAuth | Calling redirect to %s | Logged when calling redirect |
| PortalAuthOauth-508 | OAuth | state and error %s parameter are present | Logged when state and error parameters are present |
| PortalAuthOauth-509 | OAuth | code and state parameter are present | Logged when code and state parameters are present |
| PortalAuthOauth-510 | OAuth | state parameter matches stored value | Logged when state parameter matches stored value |
| PortalAuthOauth-511 | OAuth | state parameter %s differs from stored value %s | Logged when state parameter differs from stored value |
| PortalAuthOauth-512 | OAuth | Calling discovery endpoint %s | Logged when discovery endpoint is called |
| PortalAuthOauth-513 | OAuth | Discovery endpoint returned response: %s | Logged when discovery endpoint returns response |

## TRACE Level (600-699)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalAuthOauth-600 | Oauth2Service | Received token='%s' for scopes='%s', requestUri=%s | Logged with detailed token information after successful token acquisition |
| PortalAuthOauth-601 | Oauth2Service | Retrieving userinfo for authenticated user. userInfoUri=%s, access_token=%s | Logged when starting to retrieve user information |
| PortalAuthOauth-602 | Oauth2Service | Userinfo: %s | Logged with detailed user information after successful retrieval |
| PortalAuthOauth-603 | Oauth2Service | New token: %s | Logged with detailed token information after successful token refresh |
