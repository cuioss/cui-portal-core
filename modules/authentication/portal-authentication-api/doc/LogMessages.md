# Portal Authentication API Log Messages

All messages follow the format: `PortalAuth-[identifier]: [message]`

## AUTH Category

Authentication events, user login/logout, and authentication failures.

### INFO (001-099)

| ID  | Message Template | Description | Parameters |
|-----|-----------------|-------------|------------|
| 001 | `User '%s' successfully logged in` | Logged when a user successfully authenticates | username |
| 002 | `User '%s' logged out` | Logged when a user logs out | username |

### WARN (100-199)

| ID  | Message Template | Description | Parameters |
|-----|-----------------|-------------|------------|
| 100 | `Login failed for user '%s'` | Logged when a login attempt fails | username |
| 101 | `Invalid credentials provided for user '%s'` | Logged when invalid credentials are provided | username |

### ERROR (200-299)

| ID  | Message Template | Description | Parameters |
|-----|-----------------|-------------|------------|
| 200 | `Authentication error occurred: %s` | Logged when a general authentication error occurs | error message |
