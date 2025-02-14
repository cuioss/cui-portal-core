# Log Messages for Portal Configuration Module

All messages follow the format: PortalConfig-[identifier]: [message]

## INFO Level (001-099)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-001 | CORE | Running in Production-Mode | Indicates that the application is running in production mode |
| PortalConfig-010 | FILE | Watching for file changes at path: %s | Indicates that file watching has started for a specific path |

## WARN Level (100-199)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-100 | CORE | Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns when development stage is detected |
| PortalConfig-101 | CORE | Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns when test stage is detected |
| PortalConfig-102 | CORE | Invalid configuration found for Locale: %s | Indicates that an invalid locale configuration was found |
| PortalConfig-103 | FILE | Invalid element found, watchKey='%s', ignoring | Indicates an invalid watch key was encountered |
| PortalConfig-142 | FILE | Path '%s' %s, therefore it can not be watched | Indicates a path cannot be watched due to a specific reason |
| PortalConfig-143 | FILE | Unable to read metadata for file %s | Indicates failure to read file metadata |
| PortalConfig-144 | FILE | Directory %s could not be read | Indicates failure to read directory contents |

## ERROR Level (200-299)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | Indicates invalid numeric configuration value |
| PortalConfig-201 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | Indicates invalid TimeUnit configuration value |
| PortalConfig-202 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | Indicates invalid boolean configuration value |
| PortalConfig-206 | CONFIG | Invalid content for '%s', expected a number but was '%s' | Indicates invalid numeric configuration value |
| PortalConfig-207 | CONN | Error while connecting to %s | Indicates a general connection error |
| PortalConfig-208 | CONN | Timeout while connecting to %s | Indicates a connection timeout error |
| PortalConfig-209 | CONN | Connection to %s refused | Indicates a connection was refused |
| PortalConfig-517 | FILE | Unable to schedule given Path for tracking for changes, due to '%s' | Indicates failure to schedule path for file watching |
