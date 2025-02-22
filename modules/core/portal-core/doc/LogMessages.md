# Log Messages for Portal Core Module

All messages follow the format: PortalCore-[identifier]: [message]

## INFO Level (001-009)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalCore-001 | LIFECYCLE | Initializing Context for %s | 1. Context path | Context initialization started |
| PortalCore-002 | LIFECYCLE | Shutting down '%s' | 1. Context name | Context shutdown initiated |

## WARN Level (100-119)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalCore-100 | SERVLET | Could not process Request, because the user must be logged in for this request | None | Authentication required |
| PortalCore-101 | SERVLET | Could not process Request, because of the condition '%s' is not met for user '%s' | 1. Condition<br>2. User | User lacks required condition |
| PortalCore-102 | SERVLET | Could not process Request, because the user must be logged in for this request | None | Authentication required |
| PortalCore-103 | SERVLET | Could not process Request, because of the condition '[%s]' is not met for user '%s' | 1. Roles<br>2. User | User lacks required roles |
| PortalCore-110 | LIFECYCLE | Error during servlet context destroy | None | Context destruction failed |

## ERROR Level (200-209)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalCore-200 | SERVLET | Could not process Request, due to %s | 1. Error details | Request processing failed |
