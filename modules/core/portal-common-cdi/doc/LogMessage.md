# Log Messages for Portal Common CDI Module

All messages follow the format: PortalCommonCDI-[identifier]: [message]

## DEBUG Level (500-599)

| ID                   | Component | Message | Description |
|---------------------|-----------|---------|-------------|
| PortalCommonCDI-500 | BUNDLE | ResourceBundle path not defined for class: %s | Indicates that a ResourceBundleLocator implementation has not defined a bundle path |
| PortalCommonCDI-501 | BUNDLE | Successfully loaded %s '%s' for locale '%s' | Confirms successful loading of a resource bundle for a specific locale |
| PortalCommonCDI-502 | BUNDLE | Adding bundle %s | Indicates that a ResourceBundleLocator is being added to the registry |
| PortalCommonCDI-503 | BUNDLE | Resulting bundles: %s | Shows the final list of resolved resource bundles in the registry |
| PortalCommonCDI-504 | BUNDLE | Locale changed to '%s', clearing bundle cache | Indicates that the locale has changed and the bundle cache is being cleared |
| PortalCommonCDI-505 | BUNDLE | Resolved %d resource bundles for locale '%s' | Shows the number of resource bundles resolved for a specific locale |

## WARN Level (100-199)

| ID                   | Component | Message | Description |
|---------------------|-----------|---------|-------------|
| PortalCommonCDI-100 | BUNDLE | No key '%s' defined within any of the configured bundles: %s | A requested resource key could not be found in any of the configured bundles |
| PortalCommonCDI-101 | BUNDLE | Failed to load bundle '%s' for locale '%s': %s | A resource bundle could not be loaded for a specific locale |
