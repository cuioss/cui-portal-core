# Log Messages for Portal Common CDI Module

All messages follow the format: PortalCommon-[identifier]: [message]

## WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalCommon-100 | BUNDLE | No key '%s' defined within any of the configured bundles: %s | Logged when a requested resource bundle key cannot be found in any configured bundle |
| PortalCommon-101 | BUNDLE | Unable to load bundle '%s' for locale '%s': %s | Logged when a bundle cannot be loaded for a specific locale |
| PortalCommon-102 | BUNDLE | Duplicate resource path detected for %s | Logged when a duplicate resource path is detected |
| PortalCommon-103 | BUNDLE | Ignoring bundle %s due to missing path | Logged when a bundle is ignored due to missing path |
| PortalCommon-104 | BUNDLE | No valid resource bundles found | Logged when no valid resource bundles are found |
| PortalCommon-105 | BUNDLE | Ignoring Empty Key | Logged when attempting to access a bundle with an empty or null key |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalCommon-200 | STAGE | Invalid stage name '%s', falling back to %s | Logged when an invalid stage name is provided and system falls back to default |
