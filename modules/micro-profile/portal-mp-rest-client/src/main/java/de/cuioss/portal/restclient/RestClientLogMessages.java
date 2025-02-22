/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.restclient;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Centralized message bundle for Portal MicroProfile REST client logging.
 * Messages are organized into categories based on severity level.
 *
 * <h2>Message Categories</h2>
 * <ul>
 *   <li>INFO - Normal operational events (identifiers 001-099)</li>
 *   <li>ERROR - Error conditions and exceptions (identifiers 200-299)</li>
 * </ul>
 *
 * <p>Debug and trace level logging is handled directly by the respective components
 * without using {@link LogRecord} to allow for more flexible and detailed logging
 * when needed.
 *
 * @see LogClientRequestFilter
 * @see LogClientResponseFilter
 * @see LogReaderInterceptor
 */
@UtilityClass
public final class RestClientLogMessages {

    private static final String PREFIX = "PortalMPRestClient";

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

        /**
         * Logged when the default exception handler could not be loaded.
         */
        public static final LogRecord DEFAULT_HANDLER_LOAD_ERROR = LogRecordModel.builder()
                .template("Could not load Default exception Handler, tried: %s")
                .prefix(PREFIX)
                .identifier(202)
                .build();
    }
}
