# Log Messages for Portal Configuration Module

All messages follow the format: PortalConfig-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-001 | CONFIG | Running in Production-Mode | Logged when the application is running in production mode |
| PortalConfig-010 | CONFIG | Watching for file changes at path: %s | Logged when file watching is started for configuration changes |

## WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-100 | CONFIG | Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns about development mode being active |
| PortalConfig-101 | CONFIG | Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns about test mode being active |
| PortalConfig-102 | CONFIG | Invalid configuration found for Locale: %s | Logged when an invalid locale configuration is detected |
| PortalConfig-103 | CONFIG | Invalid element found, watchKey='%s', ignoring | Logged when an invalid watch key is encountered |
| PortalConfig-110 | CONFIG | Path '%s' %s, therefore it can not be watched | Logged when a path cannot be watched for changes |
| PortalConfig-111 | CONFIG | Unable to read metadata for file %s | Logged when file metadata cannot be read |
| PortalConfig-112 | CONFIG | Directory %s could not be read | Logged when a directory cannot be read |
| PortalConfig-115 | CONFIG | Unable to construct ConnectionMetadata, due to %s | Logged when connection metadata construction fails |
| PortalConfig-116 | CONFIG | Configuration setting for baseName is missing. | Logged when required baseName configuration is missing |
| PortalConfig-117 | CONFIG | Missing configuration for %s detected. | Logged when required configuration is missing |
| PortalConfig-118 | CONFIG | Configuration for basic authentication is incomplete. Missing: %s | Logged when basic auth configuration is incomplete |
| PortalConfig-119 | CONFIG | Configuration for token based authentication is incomplete. Missing: %s | Logged when token auth configuration is incomplete |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | Logged when a number configuration value is invalid |
| PortalConfig-201 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | Logged when a time unit configuration value is invalid |
| PortalConfig-202 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | Logged when a boolean configuration value is invalid |
| PortalConfig-203 | CONFIG | Invalid content for '%s', expected a number but was '%s' | Logged when a numeric value is invalid |
| PortalConfig-204 | CONFIG | Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature | Logged when file system access fails |
| PortalConfig-205 | CONFIG | Error while polling / accessing the file-system | Logged when file system polling fails |
| PortalConfig-206 | CONFIG | Handling fileChangedEvent failed for file %s | Logged when file change event handling fails |
| PortalConfig-207 | CONFIG | Connection error for %s: %s | Logged when a connection error occurs |
| PortalConfig-208 | CONFIG | Connection timeout for %s: %s | Logged when a connection timeout occurs |
| PortalConfig-209 | CONFIG | Connection refused for %s: %s | Logged when a connection is refused |
| PortalConfig-210 | CONFIG | Missing connection metadata for %s | Logged when connection metadata is missing |
| PortalConfig-211 | CONFIG | Invalid connection metadata for %s | Logged when connection metadata is invalid |
| PortalConfig-212 | CONFIG | Unable to schedule given Path for tracking for changes, due to '%s' | Logged when path scheduling fails |
| PortalConfig-220 | CONFIG | Unable to create SSLContext for connection '%s', due to '%s', defaulting to default ssl configuration | Logged when SSL context creation fails |
