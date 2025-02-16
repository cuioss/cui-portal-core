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
package de.cuioss.portal.core.servlet;

import de.cuioss.tools.logging.CuiLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Produces the external hostname for the portal. The hostname is resolved by
 * checking the following request headers in order:
 * <ol>
 * <li>X-Forwarded-Host</li>
 * <li>X-Forwarded-Port</li>
 * <li>Server Name</li>
 * <li>Server Port</li>
 * </ol>
 *
 * @author Matthias Walliczek
 */
@ApplicationScoped
public class ExternalHostnameProducer {

    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    private static final CuiLogger LOGGER = new CuiLogger(ExternalHostnameProducer.class);

    @Inject
    Provider<HttpServletRequest> httpServletRequest;

    @Produces
    @CuiExternalHostname
    String getExternalHostname() {
        var request = httpServletRequest.get();
        String serverName = Optional.ofNullable(request.getHeader(X_FORWARDED_HOST)).orElse(request.getServerName());
        String serverPort = Optional.ofNullable(request.getHeader(X_FORWARDED_PORT)).orElse(String.valueOf(request.getServerPort()));

        String hostWithPort = serverName + ":" + serverPort;
        LOGGER.debug("Resolved hostname: %s", hostWithPort);
        return hostWithPort;
    }
}
