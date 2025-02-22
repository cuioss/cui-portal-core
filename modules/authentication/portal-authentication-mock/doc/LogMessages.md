# Log Messages for Portal Authentication Mock

All messages follow the format: [MOCK-AUTH]-[identifier]: [message]

## INFO Level (001-099)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| MOCK-AUTH-001 | Authentication | User '%s' successfully logged in | Logged when a user successfully authenticates |
| MOCK-AUTH-002 | Authentication | User '%s' logged out | Logged when a user logs out |
| MOCK-AUTH-003 | Authentication | Retrieved authentication context for user '%s' | Logged when retrieving the current authentication context for an authenticated user |

## WARN Level (100-199)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| MOCK-AUTH-101 | Authentication | Invalid login attempt for user '%s' | Logged when a user fails to authenticate |
