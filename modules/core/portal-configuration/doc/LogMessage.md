# Log Messages for Portal Configuration Module

All messages follow the format: PortalConfig-[identifier]: [message]

## INFO Level (001-099)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-001 | CORE | Running in Production-Mode | Indicates that the application is running in production mode |

## WARN Level (100-199)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-100 | CORE | Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns when development stage is detected |
| PortalConfig-101 | CORE | Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage. | Warns when test stage is detected |
| PortalConfig-102 | CORE | Invalid configuration found for Locale: %s | Indicates that an invalid locale configuration was found |

## ERROR Level (200-299)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-200 | CONFIG | Invalid content for '%s%s', expected a number but was '%s' | Indicates invalid numeric configuration value |
| PortalConfig-201 | CONFIG | Invalid content for '%s%s', expected one of %s but was '%s' | Indicates invalid TimeUnit configuration value |
| PortalConfig-202 | CONFIG | Invalid content for '%s%s', expected a boolean but was '%s' | Indicates invalid boolean configuration value |

## DEBUG Level (500-599)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-500 | CONFIG | Could not resolve config value for key %s | Indicates a failure to resolve a configuration key |

## TRACE Level (600-699)

| ID               | Component | Message | Description |
|-----------------|-----------|---------|-------------|
| PortalConfig-600 | CONFIG | configProperties (%s): %s | Shows configuration properties for debugging |
| PortalConfig-601 | CONFIG | recordStats: %s | Shows cache statistics recording configuration |
| PortalConfig-602 | CONFIG | CacheConfig: %s | Shows complete cache configuration |
