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

@UtilityClass
class LogMessages {

    static final String PREFIX = "Portal";

    // Info-Level
    static final LogRecord CONFIGURED_JWKS = LogRecordModel.builder().prefix(PREFIX).identifier(120).template("Initializing JWKS lookup, jwks-endpoint='%s', refresh-interval='%s', issuer = '%s'").build();

    // WARN-LEVEL
    static final LogRecord TOKEN_IS_EMPTY = LogRecordModel.builder().prefix(PREFIX).identifier(120).template("The given token was empty").build();
    static final LogRecord COULD_NOT_PARSE_TOKEN = LogRecordModel.builder().prefix(PREFIX).identifier(121).template("Unable to parse token due to ParseException").build();
    static final LogRecord COULD_NOT_PARSE_TOKEN_TRACE = LogRecordModel.builder().prefix(PREFIX).identifier(121).template("Offending token '{}'").build();
}
