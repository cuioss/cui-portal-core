# Log Messages for Portal Common CDI Module

All messages follow the format: PortalCommonCDI-[identifier]: [message]

## WARN Level (100-199)

| ID                   | Component | Message | Description |
|---------------------|-----------|---------|-------------|
| PortalCommonCDI-100 | BUNDLE | No key '%s' defined within any of the configured bundles: %s | A requested resource key could not be found in any of the configured bundles |
| PortalCommonCDI-101 | BUNDLE | Unable to load bundle '%s' for locale '%s': %s | A resource bundle could not be loaded for a specific locale |
| PortalCommonCDI-102 | BUNDLE | Duplicate resource path detected for %s | A duplicate resource bundle path was detected during initialization |
| PortalCommonCDI-103 | BUNDLE | Ignoring bundle %s due to missing path | A bundle locator was ignored because it did not provide a valid path |
| PortalCommonCDI-104 | BUNDLE | No valid resource bundles found | No valid resource bundles were found during initialization |

## ERROR Level (200-299)

| ID                   | Component | Message | Description |
|---------------------|-----------|---------|-------------|
| PortalCommonCDI-200 | STAGE | Invalid stage name '%s', falling back to %s | An invalid project stage name was provided, system will fall back to default |
