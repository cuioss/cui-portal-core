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

import lombok.Getter;

import java.io.Serial;

/**
 * Exception thrown during connection establishment failures in the portal's connection system.
 * This exception specifically handles errors that occur while setting up or initializing
 * a connection, rather than runtime communication errors.
 * <p>
 * The exception includes an {@link ErrorReason} to categorize the type of failure,
 * enabling consistent error handling and appropriate user feedback. Common scenarios include:
 * <ul>
 *   <li>Authentication failures during connection setup</li>
 *   <li>Invalid or missing configuration</li>
 *   <li>Network connectivity issues during initialization</li>
 *   <li>SSL/TLS configuration problems</li>
 * </ul>
 * <p>
 * Note: This exception is distinct from protocol-specific exceptions that may occur
 * during normal operation after the connection is established.
 *
 * @author Oliver Wolff
 */
public class ConnectionException extends Exception {

    @Serial
    private static final long serialVersionUID = 3441459660135305431L;

    /**
     * The categorized reason for the connection failure.
     * This helps in providing structured error handling and appropriate
     * error messages to users.
     */
    @Getter
    private final ErrorReason errorReason;

    /**
     * Constructs a new connection exception with a root cause and error reason.
     * Use this constructor when wrapping a lower-level exception that caused
     * the connection failure.
     *
     * @param root the underlying cause of the connection failure, must not be null
     * @param errorReason the categorized reason for the failure, must not be null
     */
    public ConnectionException(final Throwable root, final ErrorReason errorReason) {
        super(root);
        this.errorReason = errorReason;
    }

    /**
     * Constructs a new connection exception with just an error reason.
     * Use this constructor when there is no underlying exception but the
     * connection failed due to a known reason (e.g., invalid configuration).
     *
     * @param errorReason the categorized reason for the failure, must not be null
     */
    public ConnectionException(final ErrorReason errorReason) {
        this.errorReason = errorReason;
    }

}
