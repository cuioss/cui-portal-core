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
 * @author Oliver Wolff
 */
public class ConnectionConfigurationException extends ConnectionException {

    @Serial
    private static final long serialVersionUID = -9083478212303783709L;

    private final String message;

    /**
     * @param root
     * @param errorReason
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason) {
        super(root, errorReason);
        message = null;
    }

    /**
     * @param root
     * @param errorReason
     * @param message
     */
    public ConnectionConfigurationException(final Throwable root, final ErrorReason errorReason, final String message) {
        super(root, errorReason);
        this.message = message;
    }

    /**
     * @param errorReason
     */
    public ConnectionConfigurationException(final ErrorReason errorReason) {
        super(errorReason);
        message = null;
    }

    /**
     * @param errorReason
     * @param message
     */
    public ConnectionConfigurationException(final ErrorReason errorReason, final String message) {
        super(errorReason);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (null != message) {
            return message;
        }
        return getErrorReason().getErrorText();
    }

}
