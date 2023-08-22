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

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de.cuioss.tools.logging.CuiLogger;

/**
 * Runs before {@link LogReaderInterceptor} because it has a lower
 * {@link Priority}.
 */
@Priority(42)
public class LogClientRequestFilter42 implements ClientRequestFilter {

    private static final CuiLogger LOGGER = new CuiLogger(LogClientRequestFilter42.class);

    @Override
    public void filter(final ClientRequestContext reqContext) {
        LOGGER.trace("-- LogClientRequestFilter42 --");
    }
}
