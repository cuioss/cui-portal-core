# Portal Metrics Module Log Messages

## Log Messages

### INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| Portal-Metrics-001 | METRICS | Metrics App-Name: %s | Logged when the metrics application name is resolved |
| Portal-Metrics-002 | METRICS | Registered %s metrics for cache '%s' | Logged when cache metrics are registered with the registry |
| Portal-Metrics-003 | METRICS | Removed metrics for cache '%s' | Logged when cache metrics are removed from the registry |

### WARN Level (100-199)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| Portal-Metrics-100 | CONFIG | Invalid config. Missing '%s' or '%s' | Logged when required configuration properties are missing |
| Portal-Metrics-101 | CONFIG | Invalid metrics configuration: %s | Logged when metrics configuration is invalid |

### ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| Portal-Metrics-200 | REGISTRY | Failed to register metric '%s': %s | Logged when a metric cannot be registered with the registry |
| Portal-Metrics-201 | REGISTRY | Failed to update metric '%s': %s | Logged when a metric value cannot be updated |
