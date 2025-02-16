# Portal Metrics Module Log Messages

## Overview
This document describes the log messages used in the Portal Metrics module. All messages use the prefix `Portal-Metrics`.

## Message Ranges
* 001-099: INFO level messages for general metrics operations
* 100-199: WARN level messages
* 200-299: ERROR level messages

## INFO Messages (001-099)

### METRICS_APP_NAME (001)
* Template: `Metrics App-Name: %s`
* Description: Logged when the metrics application name is resolved from configuration
* Parameters:
  1. The resolved application name

### CACHE_METRICS_REGISTERED (002)
* Template: `Registered %s metrics for cache '%s'`
* Description: Logged when cache metrics are registered with the metrics registry
* Parameters:
  1. Number of metrics registered
  2. Name of the cache

### CACHE_METRICS_REMOVED (003)
* Template: `Removed metrics for cache '%s'`
* Description: Logged when cache metrics are removed from the registry
* Parameters:
  1. Name of the cache
