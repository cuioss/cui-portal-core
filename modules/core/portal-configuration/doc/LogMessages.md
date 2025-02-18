# Log Messages for Portal Configuration Module

All messages follow the format: PortalConfig-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-001 | CONFIG | Running in Production-Mode | Logged when the application is running in production mode |
| PortalConfig-020 | CONFIG | Watching for file changes at path: %s | Logged when file watching is started for configuration changes |

## WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-100 | AUTH | Unable to determine AuthenticationType for connection='%s' and properties, returning AuthenticationType.NONE | Logged when authentication type cannot be determined from configuration |
| PortalConfig-101 | CONFIG | Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns about development mode being active |
| PortalConfig-102 | CONN | Invalid configuration found for .type, actual '%s', expected one of '%s', defaulting to 'ConnectionType.UNDEFINED' | Logged when an invalid connection type is specified in configuration |
| PortalConfig-110 | CONFIG | Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns about test mode being active |
| PortalConfig-120 | CONFIG | Invalid configuration found for Locale: %s | Logged when an invalid locale configuration is detected |
| PortalConfig-130 | CONFIG | Invalid element found, watchKey='%s', ignoring | Logged when an invalid watch key is encountered |
| PortalConfig-140 | CONFIG | Path '%s' %s, therefore it can not be watched | Logged when a path cannot be watched for changes |
| PortalConfig-141 | CONFIG | Unable to read metadata for file %s | Logged when file metadata cannot be read |
| PortalConfig-142 | CONFIG | Directory %s could not be read | Logged when a directory cannot be read |
| PortalConfig-150 | CONFIG | Unable to construct ConnectionMetadata, due to %s | Logged when connection metadata construction fails |
| PortalConfig-160 | CONFIG | Configuration setting for baseName is missing. | Logged when required baseName configuration is missing |
| PortalConfig-161 | CONFIG | Missing configuration for %s detected. | Logged when required configuration is missing |
| PortalConfig-170 | CONFIG | Configuration for basic authentication is incomplete. Missing: %s | Logged when basic auth configuration is incomplete |
| PortalConfig-171 | CONFIG | Configuration for token based authentication is incomplete. Missing: %s | Logged when token auth configuration is incomplete |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | Logged when a number configuration value is invalid |
| PortalConfig-210 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | Logged when a time unit configuration value is invalid |
| PortalConfig-220 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | Logged when a boolean configuration value is invalid |
| PortalConfig-230 | CONFIG | Invalid content for '%s', expected a number but was '%s' | Logged when a numeric value is invalid |
| PortalConfig-240 | CONFIG | Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature | Logged when file system access fails |
| PortalConfig-241 | CONFIG | Error while polling / accessing the file-system | Logged when file system polling fails |
| PortalConfig-242 | CONFIG | Handling fileChangedEvent failed for file %s | Logged when file change event handling fails |
| PortalConfig-250 | CONFIG | Connection error for %s: %s | Logged when a connection error occurs |
| PortalConfig-251 | CONFIG | Connection timeout for %s: %s | Logged when a connection timeout occurs |
| PortalConfig-252 | CONFIG | Connection refused for %s: %s | Logged when a connection is refused |
| PortalConfig-260 | CONFIG | Missing connection metadata for %s | Logged when connection metadata is missing |
| PortalConfig-261 | CONFIG | Invalid connection metadata for %s | Logged when connection metadata is invalid |
| PortalConfig-270 | CONFIG | Unable to schedule given Path for tracking for changes, due to '%s' | Logged when path scheduling fails |
| PortalConfig-280 | CONFIG | Unable to create SSLContext for connection '%s', due to '%s', defaulting to default ssl configuration | Logged when SSL context creation fails |
