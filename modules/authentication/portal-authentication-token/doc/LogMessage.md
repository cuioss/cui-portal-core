# Portal Token Module Log Messages

## Overview
This document describes the log messages used in the Portal Token Module.

## Message Categories

### Info Messages (001-099)
| ID | Message Template | Description |
|----|-----------------|-------------|
| 001 | "Initializing JWKS lookup, jwks-endpoint='%s', refresh-interval='%s', issuer = '%s'" | Logged when initializing JWKS lookup with configuration details |

### Warn Messages (100-199)
| ID | Message Template | Description |
|----|-----------------|-------------|
| 100 | "Token exceeds maximum size limit of %s bytes" | Logged when a token's size exceeds the maximum allowed limit |
| 101 | "The given token was empty" | Logged when attempting to process an empty token |
| 102 | "Unable to parse token due to ParseException" | Logged when token parsing fails |

### Error Messages (200-299)
Currently, no error messages are defined.

### Fatal Messages (300-399)
Currently, no fatal messages are defined.

## Usage Examples

### Info Level
```java
LOGGER.info(PortalTokenLogMessages.CONFIGURED_JWKS.format(endpoint, interval, issuer));
```

### Warn Level
```java
LOGGER.warn(PortalTokenLogMessages.TOKEN_SIZE_EXCEEDED.format(maxSize));
LOGGER.warn(PortalTokenLogMessages.TOKEN_IS_EMPTY::format);
LOGGER.warn(PortalTokenLogMessages.COULD_NOT_PARSE_TOKEN::format);
LOGGER.warn(PortalTokenLogMessages.COULD_NOT_PARSE_TOKEN_TRACE.format(token));
