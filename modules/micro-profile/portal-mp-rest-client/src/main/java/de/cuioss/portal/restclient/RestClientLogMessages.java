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
 * - TRACE: 600-609 (Detailed request/response content)
 */
@UtilityClass
public final class RestClientLogMessages {

    private static final String PREFIX = "RestClient";

    @UtilityClass
    public static final class INFO {
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

    @UtilityClass
    public static final class ERROR {
        public static final LogRecord TRACE_LOG_ERROR = LogRecordModel.builder()
                .template("Could not trace-log %s data")
                .prefix(PREFIX)
                .identifier(200)
                .build();
    }

}
