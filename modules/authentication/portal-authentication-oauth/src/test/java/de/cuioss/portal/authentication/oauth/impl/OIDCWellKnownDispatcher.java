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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.oauth.OAuthConfigKeys;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.io.FileTypePrefix;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.cuioss.tools.io.FileLoaderUtility.toStringUnchecked;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OIDCWellKnownDispatcher extends Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OIDCWellKnownDispatcher.class);

    public static final String EXTERNAL_HOSTNAME = "http://path";
    public static final String LOGOUT_URL = "http://localhost:5602/auth/realms/master/protocol/openid-connect/logout";
    public static final String AUTHORIZE_URL = "http://localhost:5602/auth/realms/master/protocol/openid-connect/auth";

    public static final String CLIENT_ID = "my-secret-client-id";
    public static final String CLIENT_SECRET = "my-secret-client-secret";

    public static final FileLoader CONFIGURATION = FileLoaderUtility
            .getLoaderForPath(FileTypePrefix.CLASSPATH + "/openid-configuration.json");

    public static final FileLoader INVALID_CONFIGURATION = FileLoaderUtility
            .getLoaderForPath(FileTypePrefix.CLASSPATH + "/openid-configuration-invalid.json");

    public static final FileLoader TOKEN = FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/token.json");

    public static final FileLoader USER_INFO = FileLoaderUtility
            .getLoaderForPath(FileTypePrefix.CLASSPATH + "/userInfo.json");

    public static final String OIDC_DISCOVERY_PATH = ".well-known/openid-configuration";

    @Getter
    @Setter
    private MockResponse tokenResult;

    @Getter
    @Setter
    private MockResponse userInfoResult;

    @Getter
    private String currentPort;

    @Setter
    private boolean simulateInvalidOidcConfig = false;

    public void reset() {
        tokenResult = new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", MediaType.APPLICATION_JSON), toStringUnchecked(TOKEN));
        userInfoResult = new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", MediaType.APPLICATION_JSON), toStringUnchecked(USER_INFO));
    }

    public void assertAuthorizeURL(String actualUrl, String... parts) {
        var expected = AUTHORIZE_URL.replaceAll("5602", currentPort);
        assertTrue(actualUrl.startsWith(expected), actualUrl);
        for (String string : parts) {
            assertTrue(actualUrl.contains(string), string);
        }
    }

    public void assertLogoutURL(String actualUrl, String... parts) {
        var expected = LOGOUT_URL.replaceAll("5602", currentPort);
        assertTrue(actualUrl.startsWith(expected), actualUrl);
        for (String string : parts) {
            assertTrue(actualUrl.contains(string), string);
        }
    }

    public void configure(PortalTestConfiguration configuration, MockWebServer mockWebServer) {
        currentPort = String.valueOf(mockWebServer.getPort());
        configuration.update(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL, "http://localhost:" + mockWebServer.getPort());
        configuration.update(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, OIDC_DISCOVERY_PATH);
        configuration.update(OAuthConfigKeys.OPEN_ID_CLIENT_ID, CLIENT_ID);
        configuration.update(OAuthConfigKeys.OPEN_ID_CLIENT_SECRET, CLIENT_SECRET);
        configuration.update(OAuthConfigKeys.EXTERNAL_HOSTNAME, EXTERNAL_HOSTNAME);
    }

    public List<RecordedRequest> nonWellKnownRequests(MockWebServer mockWebServer) throws InterruptedException {
        var builder = new CollectionBuilder<RecordedRequest>();

        var request = mockWebServer.takeRequest();
        while (null != request) {
            if (!isOidcDiscoveryPath(request.getUrl().encodedPath())) {
                builder.add(request);
            }
            request = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS);
        }

        return builder.toImmutableList();
    }

    private boolean isOidcDiscoveryPath(String path) {
        return path != null && path.contains(OIDC_DISCOVERY_PATH);
    }

    @Override
    public @NotNull MockResponse dispatch(RecordedRequest request) {
        var path = request.getUrl().encodedPath();
        LOGGER.info(() -> "Serve request " + path);

        return switch (path) {
            case "/" + OIDC_DISCOVERY_PATH ->
                new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", MediaType.APPLICATION_JSON), simulateInvalidOidcConfig
                        ? toStringUnchecked(INVALID_CONFIGURATION).replaceAll("5602", currentPort)
                        : toStringUnchecked(CONFIGURATION).replaceAll("5602", currentPort));
            case "/auth/realms/master/protocol/openid-connect/userinfo" -> userInfoResult;
            case "/auth/realms/master/protocol/openid-connect/token" -> tokenResult;
            default -> {
                LOGGER.warn(() -> "Unable to serve request " + path);
                yield new MockResponse(HttpServletResponse.SC_NOT_FOUND, Headers.of(), "");
            }
        };
    }
}
