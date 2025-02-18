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
package de.cuioss.portal.configuration.connections.exception;

import java.io.Serial;

/**
 * Exception thrown when connection configuration validation fails.
 * This exception extends {@link ConnectionException} to specifically handle
 * configuration-related errors, such as:
 * <ul>
 *   <li>Missing or invalid required configuration parameters</li>
 *   <li>Incompatible configuration combinations</li>
 *   <li>Invalid authentication settings</li>
 *   <li>Malformed URLs or connection properties</li>
 * </ul>
 * <p>
 * The exception can carry both an {@link ErrorReason} and an optional
 * detailed message to provide more context about the configuration error.
 *
 * @author Oliver Wolff
 */
public class ConnectionConfigurationException extends ConnectionException {

    @Serial
    private static final long serialVersionUID = -9083478212303783709L;

    /**
     * Optional detailed message providing additional context about the configuration error.
     * If null, {@link #getMessage()} will return the error text from {@link ErrorReason}.
     */
    private final String message;

    /**
     * Constructs a new configuration exception with a root cause and error reason.
     *
     * @param root the underlying cause of the configuration failure
     * @param errorReason the categorized reason for the configuration error
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason) {
        super(root, errorReason);
        message = null;
    }

    /**
     * Constructs a new configuration exception with a root cause, error reason,
     * and detailed message.
     *
     * @param root the underlying cause of the configuration failure
     * @param errorReason the categorized reason for the configuration error
     * @param message detailed description of the configuration error
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason, final String message) {
        super(root, errorReason);
        this.message = message;
    }

    /**
     * Constructs a new configuration exception with just an error reason.
     *
     * @param errorReason the categorized reason for the configuration error
     */
    public ConnectionConfigurationException(final ErrorReason errorReason) {
        super(errorReason);
        message = null;
    }

    /**
     * Constructs a new configuration exception with an error reason and detailed message.
     *
     * @param errorReason the categorized reason for the configuration error
     * @param message detailed description of the configuration error
     */
    public ConnectionConfigurationException(final ErrorReason errorReason, final String message) {
        super(errorReason);
        this.message = message;
    }

    /**
     * Returns the error message for this exception.
     * If a custom message was provided during construction, it will be returned.
     * Otherwise, returns the error text from the associated {@link ErrorReason}.
     *
     * @return the error message, never null
     */
    @Override
    public String getMessage() {
        if (null != message) {
            return message;
        }
        return getErrorReason().getErrorText();
    }
}
