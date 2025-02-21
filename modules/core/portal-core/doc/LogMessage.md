# Log Messages for Portal Core Module

All messages follow the format: PortalCore-[identifier]: [message]

## INFO Level (001-099)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-001 | LIFECYCLE | Initializing Context for %s | Indicates the start of context initialization for a given path |
| PortalCore-002 | LIFECYCLE | Shutting down '%s' | Indicates the start of context shutdown for a given path |

## WARN Level (100-199)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-100 | SERVLET | User not logged in for protected resource | Request failed due to missing user authentication |
| PortalCore-101 | SERVLET | User should provide the roles %s | Request failed due to insufficient user permissions |
| PortalCore-110 | LIFECYCLE | Error while destroying '%s' for '%s': %s | Error occurred during component destruction |

## ERROR Level (200-299)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-201 | SERVLET | Could not process Request, due to %s | Indicates a request processing error with details about the cause |

## DEBUG Level (500-599)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-500 | SERVLET | Disabled by configuration | Request processing skipped due to configuration settings |
| PortalCore-510 | LIFECYCLE | ServletLifecycleListener called for '%s', initializing with order: %s | Details about initialization order for ServletLifecycleListener |
| PortalCore-511 | LIFECYCLE | Initializing '%s' for '%s' | Component initialization in progress |
| PortalCore-512 | LIFECYCLE | Initialize successfully called for all elements for '%s' | All components initialized successfully |
| PortalCore-513 | LIFECYCLE | Executing applicationDestroyListener for '%s' | Starting application destroy process |
| PortalCore-514 | LIFECYCLE | ServletLifecycleListener called for '%s', finalizing with order: %s | Details about finalization order for ServletLifecycleListener |
| PortalCore-515 | LIFECYCLE | Destroying '%s' for '%s' | Component destruction in progress |
| PortalCore-516 | LIFECYCLE | Finalize successfully called for all elements for '%s' | All components finalized successfully |
| PortalCore-520 | SERVLET | Resolved hostname: %s | Successfully resolved external hostname |

## TRACE Level (600-699)

| ID             | Component | Message | Description |
|----------------|-----------|---------|-------------|
| PortalCore-600 | SERVLET | Checking call preconditions | Starting precondition checks for request processing |
| PortalCore-601 | SERVLET | All preconditions are ok, generating payload | Preconditions passed, proceeding with payload generation |
