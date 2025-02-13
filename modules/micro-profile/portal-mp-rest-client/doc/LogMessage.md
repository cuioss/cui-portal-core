# Log Messages for Portal MP Rest Client

All messages follow the format: RestClient-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-001 | LogClientRequestFilter | -- Client request info -- Request URI: %s Method: %s Headers: %s Body: %s | Logged when a client request is made, showing the request details |
| RestClient-002 | LogClientResponseFilter | -- Client response filter %s-- Status: %s StatusInfo: %s Allowed Methods: %s EntityTag: %s Cookies: %s Date: %s Headers: %s Language: %s LastModified: %s Links: %s Location: %s MediaType: %s | Logged when a client response is received, showing the response details |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-200 | LogClientRequestFilter/LogClientResponseFilter | Could not trace-log %s data | Logged when an error occurs during request/response logging |

## TRACE Level (600-699)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-600 | LogReaderInterceptor | -- Client response info -- MediaType: %s GenericType: %s Properties: %s Headers: %s Body: %s | Detailed trace logging of response content |
