package de.cuioss.portal.restclient;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized log messages for the portal-mp-rest-client module using a DSL-style approach.
 * Messages are organized by package, then by log level, making them easily discoverable
 * and maintainable.
 * <p>
 * Message Identifiers:
 * - INFO: 001-099 (Request/Response metadata)
 * - ERROR: 200-299 (Errors and exceptions)
 * - DEBUG: 500-599 (Initialization and configuration details)
 * - TRACE: 600-609 (Detailed request/response content)
 */
@UtilityClass
public final class RestClientLogMessages {

    private static final String PREFIX = "RestClient";

    @UtilityClass
    public static final class INFO {
        /**
         * Logged when a client request is made, including URI, method, headers and body.
         */
        public static final LogRecord REQUEST_INFO = LogRecordModel.builder()
                .template("""
                        -- Client request info --
                        Request URI: %s
                        Method: %s
                        Headers: %s
                        Body: %s""")
                .prefix(PREFIX)
                .identifier(1)
                .build();

        /**
         * Logged when a client response is received, including all response metadata.
         */
        public static final LogRecord RESPONSE_INFO = LogRecordModel.builder()
                .template("""
                        -- Client response filter %s--
                        Status: %s
                        StatusInfo: %s
                        Allowed Methods: %s
                        EntityTag: %s
                        Cookies: %s
                        Date: %s
                        Headers: %s
                        Language: %s
                        LastModified: %s
                        Links: %s
                        Location: %s
                        MediaType: %s""")
                .prefix(PREFIX)
                .identifier(2)
                .build();
    }

    @UtilityClass
    public static final class ERROR {
        /**
         * Logged when an error occurs while attempting to log request/response data.
         */
        public static final LogRecord TRACE_LOG_ERROR = LogRecordModel.builder()
                .template("Could not trace-log %s data")
                .prefix(PREFIX)
                .identifier(200)
                .build();

        /**
         * Logged when initialization of RestClientHolder fails.
         */
        public static final LogRecord INITIALIZATION_FAILED = LogRecordModel.builder()
                .template("Initialization of RestClientHolder failed")
                .prefix(PREFIX)
                .identifier(201)
                .build();
    }

    @UtilityClass
    public static final class DEBUG {
        /**
         * Logged when producing a new client instance.
         */
        public static final LogRecord PRODUCING_CLIENT = LogRecordModel.builder()
                .template("Producing DsmlClient for baseName ='%s'")
                .prefix(PREFIX)
                .identifier(500)
                .build();

        /**
         * Logged when skipping a class that appears to be a Weld CDI proxy.
         */
        public static final LogRecord SKIP_PROXY_CLASS = LogRecordModel.builder()
                .template("Not using class %s as it seems to be a Weld CDI proxy")
                .prefix(PREFIX)
                .identifier(501)
                .build();

        /**
         * Logged when resolving the logger class.
         */
        public static final LogRecord USING_LOGGER_CLASS = LogRecordModel.builder()
                .template("Using logger class: %s")
                .prefix(PREFIX)
                .identifier(502)
                .build();
    }

    @UtilityClass
    public static final class TRACE {
        /**
         * Logged when reading response body content, including media type, properties,
         * headers and body.
         */
        public static final LogRecord RESPONSE_BODY = LogRecordModel.builder()
                .template("""
                        -- Client response info --
                        MediaType: %s
                        GenericType: %s
                        Properties:
                        %s
                        Headers:
                        %s
                        Body:
                        %s""")
                .prefix(PREFIX)
                .identifier(600)
                .build();
    }
}
