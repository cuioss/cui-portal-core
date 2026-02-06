/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
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
 * Produces the external hostname for the portal, handling both direct server access and
 * proxy-forwarded scenarios. This is particularly useful in environments with load balancers,
 * reverse proxies, or SSL terminators.
 *
 * <p>The hostname resolution follows this priority order:</p>
 * <ol>
 * <li>X-Forwarded-Host header (for proxy-forwarded host)</li>
 * <li>X-Forwarded-Port header (for proxy-forwarded port)</li>
 * <li>Server Name from request (direct server name)</li>
 * <li>Server Port from request (direct server port)</li>
 * </ol>
 *
 * <p>The produced hostname will be in the format: {@code hostname:port}</p>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>
 * &#64;Inject
 * &#64;CuiExternalHostname
 * Provider&lt;String&gt; hostname;
 *
 * String serverAddress = hostname.get();
 * </pre>
 *
 * @author Matthias Walliczek
 * @see CuiExternalHostname
 * @since 1.0
 */
@ApplicationScoped
public class ExternalHostnameProducer {

    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    private static final CuiLogger LOGGER = new CuiLogger(ExternalHostnameProducer.class);

    private final Provider<HttpServletRequest> httpServletRequest;

    @Inject
    ExternalHostnameProducer(Provider<HttpServletRequest> httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

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
