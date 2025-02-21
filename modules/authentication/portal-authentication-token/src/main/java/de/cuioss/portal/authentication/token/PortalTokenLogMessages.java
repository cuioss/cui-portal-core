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
package de.cuioss.portal.authentication.token;

import de.cuioss.tools.logging.LogRecord;
import de.cuioss.tools.logging.LogRecordModel;
import lombok.experimental.UtilityClass;

/**
 * Defines the standardized log messages for the Portal Token module.
 * Follows the CUI logging standards for consistent message formatting and categorization.
 * <p>
 * Message categories:
 * <ul>
 *   <li>INFO (001-099): Configuration and successful operations</li>
 *   <li>WARN (100-199): Non-critical issues and potential problems</li>
 * </ul>
 * <p>
 * Available messages:
 * <ul>
 *   <li>INFO:
 *     <ul>
 *       <li>{@link #CONFIGURED_JWKS}: JWKS configuration details</li>
 *     </ul>
 *   </li>
 *   <li>WARN:
 *     <ul>
 *       <li>{@link #TOKEN_SIZE_EXCEEDED}: Token size limit violation</li>
 *       <li>{@link #TOKEN_IS_EMPTY}: Empty token handling</li>
 *       <li>{@link #COULD_NOT_PARSE_TOKEN}: Token parsing failures</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * All messages use the prefix "PortalToken" for module identification.
 */
@UtilityClass
public class PortalTokenLogMessages {

    private static final String PREFIX = "PortalToken";

    // Info-Level (001-099)
    public static final LogRecord CONFIGURED_JWKS = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(1)
            .template("Initializing JWKS lookup, jwks-endpoint='%s', refresh-interval='%s', issuer = '%s'")
            .build();

    // Warn-Level (100-199)
    public static final LogRecord TOKEN_SIZE_EXCEEDED = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(100)
            .template("Token exceeds maximum size limit of %s bytes")
            .build();

    public static final LogRecord TOKEN_IS_EMPTY = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(101)
            .template("The given token was empty")
            .build();

    public static final LogRecord COULD_NOT_PARSE_TOKEN = LogRecordModel.builder()
            .prefix(PREFIX)
            .identifier(102)
            .template("Unable to parse token due to ParseException")
            .build();
}
