# Log Messages for Portal Configuration Module

All messages follow the format: PortalConfig-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalConfig-001 | STAGE | Running in Production-Mode | None | Indicates that the system is running in production mode |
| PortalConfig-003 | CONFIG | JVM Configuration:\n%s | 1. JVM configuration string | Displays the current JVM configuration settings |
| PortalConfig-004 | CONFIG | Environment Configuration:\n%s | 1. Environment configuration string | Displays the current environment configuration |
| PortalConfig-005 | CONFIG | Portal Configuration:\n%s | 1. Portal configuration string | Displays the current portal configuration |
| PortalConfig-020 | FILE | Watching for file changes at path: %s | 1. File path | Indicates that file watching has been initialized for the specified path |

## WARN Level (100-199)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalConfig-100 | AUTH | Unable to determine AuthenticationType for connection='%s' and properties, returning AuthenticationType.NONE | 1. Connection name | Authentication type could not be determined for the connection |
| PortalConfig-101 | STAGE | Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage | None | Warns that the system is running in development mode |
| PortalConfig-102 | CONFIG | Invalid configuration found for .type, actual '%s', expected one of '%s', defaulting to 'ConnectionType.UNDEFINED' | 1. Actual type<br>2. Expected types | Invalid connection type configuration detected |
| PortalConfig-110 | STAGE | Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage | None | Warns that the system is running in test mode |
| PortalConfig-120 | CONFIG | Invalid configuration found for Locale: %s | 1. Invalid locale | Invalid locale configuration detected |
| PortalConfig-130 | FILE | Invalid element found, watchKey='%s', ignoring | 1. Watch key | Invalid watch key encountered during file monitoring |
| PortalConfig-140 | FILE | Path '%s' %s, therefore it can not be watched | 1. Path<br>2. Reason | Path cannot be monitored for changes |
| PortalConfig-141 | FILE | Unable to read metadata for file %s | 1. File path | File metadata could not be read |
| PortalConfig-142 | FILE | Directory %s could not be read | 1. Directory path | Directory contents could not be read |
| PortalConfig-150 | CONN | Unable to construct ConnectionMetadata, due to %s | 1. Error reason | Connection metadata construction failed |
| PortalConfig-160 | CONFIG | Configuration setting for baseName is missing | None | Required base name configuration is missing |
| PortalConfig-161 | CONFIG | Missing configuration for %s detected | 1. Configuration key | Required configuration value is missing |
| PortalConfig-170 | AUTH | Configuration for basic authentication is incomplete. Missing: %s | 1. Missing fields | Basic authentication configuration is incomplete |
| PortalConfig-171 | AUTH | Configuration for token based authentication is incomplete. Missing: %s | 1. Missing fields | Token authentication configuration is incomplete |

## ERROR Level (200-299)

| ID | Component | Message | Parameters | Description |
|----|-----------|---------|------------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | 1. Config prefix<br>2. Config key<br>3. Invalid value | Number configuration value is invalid |
| PortalConfig-210 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | 1. Config prefix<br>2. Config key<br>3. Valid options<br>4. Invalid value | Time unit configuration value is invalid |
| PortalConfig-212 | CONFIG | Could not convert input value '%s' to enum of type: %s. Reason: %s | 1. Input value<br>2. Enum type<br>3. Error reason | Enum conversion failed |
| PortalConfig-220 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | 1. Config prefix<br>2. Config key<br>3. Invalid value | Boolean configuration value is invalid |
| PortalConfig-230 | CONFIG | Invalid content for '%s', expected a number but was '%s' | 1. Config key<br>2. Invalid value | Number value is invalid |
| PortalConfig-240 | FILE | Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature | 1. Error reason<br>2. Config property | File system access failed |
| PortalConfig-241 | FILE | Error while polling / accessing the file-system | None | File system polling failed |
| PortalConfig-242 | FILE | Handling fileChangedEvent failed for file %s | 1. File path | File change event handling failed |
| PortalConfig-250 | CONN | Connection error for %s: %s | 1. Connection name<br>2. Error message | General connection error occurred |
| PortalConfig-251 | CONN | Connection timeout for %s: %s | 1. Connection name<br>2. Error message | Connection timeout occurred |
| PortalConfig-252 | CONN | Connection refused for %s: %s | 1. Connection name<br>2. Error message | Connection was refused |
| PortalConfig-260 | CONN | Missing connection metadata for %s | 1. Connection name | Connection metadata is missing |
| PortalConfig-261 | CONN | Invalid connection metadata for %s | 1. Connection name | Connection metadata is invalid |
| PortalConfig-270 | FILE | Unable to schedule given Path for tracking for changes, due to '%s' | 1. Error reason | Path scheduling for change tracking failed |
| PortalConfig-280 | SSL | Unable to create SSLContext for connection '%s', due to '%s', defaulting to default ssl configuration | 1. Connection name<br>2. Error reason | SSL context creation failed |
