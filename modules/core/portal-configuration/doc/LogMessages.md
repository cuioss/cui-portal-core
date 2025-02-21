# Portal Configuration Module Log Messages

## Message Categories

- INFO (001-099): Informational messages about normal operation
- WARN (100-199): Warning messages about potential issues
- ERROR (200-299): Error messages about operational failures
- FATAL (300-399): Critical system failures

## INFO Messages (001-099)

### Project Stage Messages
- **001**: Running in Production-Mode
  - Logged when the system is running in production mode
  - Level: INFO

### System Configuration Messages
- **003**: JVM Configuration:\n%s
  - Logged when displaying system JVM configuration
  - Parameters: Configuration properties as string
  - Level: INFO

- **004**: Environment Configuration:\n%s
  - Logged when displaying environment configuration
  - Parameters: Environment variables as string
  - Level: INFO

- **005**: Portal Configuration:\n%s
  - Logged when displaying portal configuration
  - Parameters: Portal properties as string
  - Level: INFO

### File System Messages
- **020**: Watching for file changes at path: %s
  - Logged when file watching is initialized for a path
  - Parameters: Absolute path being watched
  - Level: INFO

## WARN Messages (100-199)

### Authentication Messages
- **100**: Unable to determine AuthenticationType for connection='%s' and properties, returning AuthenticationType.NONE
  - Logged when authentication type cannot be determined
  - Parameters: Connection name
  - Level: WARN

### Project Stage Messages
- **101**: Project stage 'development' detected. Set the property 'portal.stage' to 'production' for productive usage.
  - Logged when development mode is detected
  - Level: WARN

- **110**: Project stage 'test' detected. Set the property 'portal.stage' to 'production' for productive usage.
  - Logged when test mode is detected
  - Level: WARN

### Configuration Messages
- **102**: Invalid configuration found for .type, actual '%s', expected one of '%s', defaulting to 'ConnectionType.UNDEFINED'
  - Logged when invalid connection type is configured
  - Parameters: Actual type, Expected types
  - Level: WARN

- **120**: Invalid configuration found for Locale: %s
  - Logged when invalid locale configuration is found
  - Parameters: Invalid locale string
  - Level: WARN

### File System Messages
- **130**: Invalid element found, watchKey='%s', ignoring
  - Logged when invalid watch key is encountered
  - Parameters: Watch key
  - Level: WARN

- **140**: Path '%s' %s, therefore it can not be watched
  - Logged when path cannot be watched
  - Parameters: Path, Reason
  - Level: WARN

- **141**: Unable to read metadata for file %s
  - Logged when file metadata cannot be read
  - Parameters: File path
  - Level: WARN

- **142**: Directory %s could not be read
  - Logged when directory cannot be read
  - Parameters: Directory path
  - Level: WARN

### Connection Messages
- **150**: Unable to construct ConnectionMetadata, due to %s
  - Logged when connection metadata construction fails
  - Parameters: Error message
  - Level: WARN

### Missing Configuration Messages
- **160**: Configuration setting for baseName is missing.
  - Logged when base name configuration is missing
  - Level: WARN

- **161**: Missing configuration for %s detected.
  - Logged when required configuration is missing
  - Parameters: Configuration key
  - Level: WARN

- **170**: Configuration for basic authentication is incomplete. Missing: %s
  - Logged when basic auth configuration is incomplete
  - Parameters: Missing fields
  - Level: WARN

- **171**: Configuration for token based authentication is incomplete. Missing: %s
  - Logged when token auth configuration is incomplete
  - Parameters: Missing fields
  - Level: WARN

## ERROR Messages (200-299)

### Configuration Errors
- **200**: Invalid content for '%s%s', expected a number but was '%s'
  - Logged when number configuration is invalid
  - Parameters: Config prefix, Key, Invalid value
  - Level: ERROR

- **210**: Invalid content for '%s%s', expected one of %s but was '%s'
  - Logged when time unit configuration is invalid
  - Parameters: Config prefix, Key, Valid values, Invalid value
  - Level: ERROR

- **212**: Could not convert input value '%s' to enum of type: %s. Reason: %s
  - Logged when enum conversion fails
  - Parameters: Input value, Enum class, Error reason
  - Level: ERROR

- **220**: Invalid content for '%s%s', expected a boolean but was '%s'
  - Logged when boolean configuration is invalid
  - Parameters: Config prefix, Key, Invalid value
  - Level: ERROR

- **230**: Invalid content for '%s', expected a number but was '%s'
  - Logged when number value is invalid
  - Parameters: Key, Invalid value
  - Level: ERROR

### File System Errors
- **240**: Unable to access File-system for detecting changes, due to '%s', use the configuration property '%s' to disable this feature
  - Logged when file system access fails
  - Parameters: Error message, Configuration property
  - Level: ERROR

- **241**: Error while polling / accessing the file-system
  - Logged when file system polling fails
  - Level: ERROR

- **242**: Handling fileChangedEvent failed for file %s
  - Logged when file change event handling fails
  - Parameters: File path
  - Level: ERROR

### Connection Errors
- **250**: Connection error for %s: %s
  - Logged when general connection error occurs
  - Parameters: Connection name, Error message
  - Level: ERROR

- **251**: Connection timeout for %s: %s
  - Logged when connection timeout occurs
  - Parameters: Connection name, Error message
  - Level: ERROR

- **252**: Connection refused for %s: %s
  - Logged when connection is refused
  - Parameters: Connection name, Error message
  - Level: ERROR

- **260**: Missing connection metadata for %s
  - Logged when connection metadata is missing
  - Parameters: Connection name
  - Level: ERROR

- **261**: Invalid connection metadata for %s
  - Logged when connection metadata is invalid
  - Parameters: Connection name
  - Level: ERROR

- **270**: Unable to schedule given Path for tracking for changes, due to '%s'
  - Logged when path scheduling fails
  - Parameters: Error message
  - Level: ERROR

- **280**: Unable to create SSLContext for connection '%s', due to '%s', defaulting to default ssl configuration
  - Logged when SSL context creation fails
  - Parameters: Connection ID, Error message
  - Level: ERROR
