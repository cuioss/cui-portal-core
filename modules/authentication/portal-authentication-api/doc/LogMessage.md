# Log Messages for Portal Authentication API

All messages follow the format: PortalAuth-[identifier]: [message]

## INFO Level (001-099)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalAuth-001 | AUTH | User '%s' successfully logged in | Logged when a user successfully authenticates |
| PortalAuth-002 | AUTH | User '%s' logged out | Logged when a user logs out of the system |

## WARN Level (100-199)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalAuth-100 | AUTH | Login failed for user '%s' | Logged when a login attempt fails |
| PortalAuth-101 | AUTH | Invalid credentials provided for user '%s' | Logged when invalid credentials are provided |

## ERROR Level (200-299)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalAuth-200 | AUTH | Authentication error occurred: %s | Logged when a system-level authentication error occurs |

## DEBUG Level (500-599)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalAuth-500 | AUTH | User info updated for '%s' | Logged when user information is updated |
| PortalAuth-501 | AUTH | User info enriched with '%s' with '%s' | Logged when user information is being enriched with additional data |
