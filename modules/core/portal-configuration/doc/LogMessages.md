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

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | Logged when a number configuration value is invalid |
| PortalConfig-201 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | Logged when a time unit configuration value is invalid |
| PortalConfig-202 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | Logged when a boolean configuration value is invalid |
| PortalConfig-206 | CONFIG | Invalid content for '%s', expected a number but was '%s' | Logged when a numeric configuration value is invalid |
