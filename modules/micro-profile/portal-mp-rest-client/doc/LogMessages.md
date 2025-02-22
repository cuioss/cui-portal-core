# Log Messages for Portal MP REST Client

All messages follow the format: PortalMPRestClient-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalMPRestClient-001 | REST | -- Client request info --\nURI: %s\nMethod: %s\nHeaders: %s\nBody: %s | Logged when a client request is made |
| PortalMPRestClient-002 | REST | -- Client response info --\nStatus: %s\nStatusInfo: %s\nAllowed Methods: %s\nEntityTag: %s\nCookies: %s\nDate: %s\nHeaders: %s\nLanguage: %s\nLastModified: %s\nLinks: %s\nLocation: %s\nMediaType: %s | Logged when a client response is received |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| PortalMPRestClient-200 | REST | Could not trace-log %s data | Logged when an error occurs while attempting to log request/response data |
| PortalMPRestClient-201 | REST | Initialization of RestClientHolder failed | Logged when initialization of RestClientHolder fails |
| PortalMPRestClient-202 | REST | Could not load Default exception Handler, tried: %s | Logged when the default exception handler could not be loaded |
