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
