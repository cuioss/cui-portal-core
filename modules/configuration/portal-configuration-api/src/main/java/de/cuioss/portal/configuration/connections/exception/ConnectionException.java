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
 * General exception to be thrown if anything goes wrong while trying to
 * <em>establish</em> the connection. Other exceptions that are specific to the
 * concrete connections are not covered.
 *
 * @author Oliver Wolff
 */
public class ConnectionException extends Exception {

    @Serial
    private static final long serialVersionUID = 3441459660135305431L;

    @Getter
    private final ErrorReason errorReason;

    /**
     * @param root
     * @param errorReason
     */
    public ConnectionException(final Throwable root, final ErrorReason errorReason) {
        super(root);
        this.errorReason = errorReason;
    }

    /**
     * @param errorReason
     */
    public ConnectionException(final ErrorReason errorReason) {
        this.errorReason = errorReason;
    }

}
