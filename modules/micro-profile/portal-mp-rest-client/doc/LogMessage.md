# Log Messages for Portal MP Rest Client

All messages follow the format: RestClient-[identifier]: [message]

## INFO Level (001-099)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-001 | LogClientRequestFilter | -- Client request info --<br>Request URI: %s<br>Method: %s<br>Headers: %s<br>Body: %s | Logged when a client request is made, including URI, method, headers and body |
| RestClient-002 | LogClientResponseFilter | -- Client response filter %s--<br>Status: %s<br>StatusInfo: %s<br>Allowed Methods: %s<br>EntityTag: %s<br>Cookies: %s<br>Date: %s<br>Headers: %s<br>Language: %s<br>LastModified: %s<br>Links: %s<br>Location: %s<br>MediaType: %s | Logged when a client response is received, including all response metadata |

## ERROR Level (200-299)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-200 | LogClientRequestFilter<br>LogClientResponseFilter<br>LogReaderInterceptor | Could not trace-log %s data | Logged when an error occurs while attempting to log request/response data |
| RestClient-201 | RestClientProducer | Initialization of RestClientHolder failed | Logged when initialization of RestClientHolder fails |
| RestClient-202 | CuiRestClientBuilder | Could not load Default exception Handler, tried: %s | Logged when the default exception handler could not be loaded |

## DEBUG Level (500-599)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-500 | RestClientProducer | Producing DsmlClient for baseName ='%s' | Logged when producing a new client instance |
| RestClient-501 | RestClientProducer | Not using class %s as it seems to be a Weld CDI proxy | Logged when skipping a class that appears to be a Weld CDI proxy |
| RestClient-502 | RestClientProducer | Using logger class: %s | Logged when resolving the logger class |
| RestClient-503 | CuiRestClientBuilder | -- Client response filter --<br>Status: %s<br>StatusInfo: %s<br>Allowed Methods: %s<br>EntityTag: %s<br>Cookies: %s<br>Date: %s<br>Headers: %s<br>Language: %s<br>LastModified: %s<br>Links: %s<br>Location: %s<br>MediaType: %s | Logged when debugging a response |

## TRACE Level (600-699)

| ID | Component | Message | Description |
|----|-----------|---------|-------------|
| RestClient-600 | LogReaderInterceptor | -- Client response info --<br>MediaType: %s<br>GenericType: %s<br>Properties:<br>%s<br>Headers:<br>%s<br>Body:<br>%s | Logged when reading response body content, including media type, properties, headers and body |
