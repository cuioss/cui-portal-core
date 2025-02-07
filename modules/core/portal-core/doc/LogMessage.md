# Log Messages for Portal Core Module

All messages follow the format: PortalCore-[identifier]: [message]

## ERROR Level (200-299)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-200 | SERVLET | Could not process Request, due to %s | Indicates a request processing error with details about the cause |

## WARN Level (100-199)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-100 | SERVLET | Could not process Request, because the user must be logged in for this request | Request failed due to missing user authentication |
| PortalCore-101 | SERVLET | Could not process Request, because of the condition '%s' is not met for user '%s' | Request failed due to insufficient user permissions |

## DEBUG Level (500-599)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-500 | SERVLET | Could not process Request, disabled by configuration | Request processing skipped due to configuration settings |

## TRACE Level (600-699)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-600 | SERVLET | Checking call preconditions | Starting precondition checks for request processing |
| PortalCore-601 | SERVLET | All preconditions are ok, generating payload | Preconditions passed, proceeding with payload generation |
