package de.cuioss.portal.authentication.oauth.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.tools.collect.CollectionBuilder;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.io.FileTypePrefix;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SuppressWarnings("javadoc")
public class OIDCWellKnownDispatcher extends Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OIDCWellKnownDispatcher.class);

    public static final String EXTERNAL_HOSTNAME = "http://path";
    public static final String LOGOUT_URL = "http://localhost:5602/auth/realms/master/protocol/openid-connect/logout";
    public static final String AUTHORIZE_URL = "http://localhost:5602/auth/realms/master/protocol/openid-connect/auth";

    public static final String CLIENT_ID = "my-secret-client-id";
    public static final String CLIENT_SECRET = "my-secret-client-secret";

    public static final FileLoader CONFIGURATION = FileLoaderUtility
            .getLoaderForPath(FileTypePrefix.CLASSPATH + "/openid-configuration.json");

    public static final FileLoader TOKEN = FileLoaderUtility.getLoaderForPath(FileTypePrefix.CLASSPATH + "/token.json");

    public static final FileLoader CLIENT_TOKEN = FileLoaderUtility
            .getLoaderForPath(FileTypePrefix.CLASSPATH + "/clientToken.json");

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

    public void reset() {
        tokenResult = new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(FileLoaderUtility.toStringUnchecked(TOKEN));
        userInfoResult = new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(FileLoaderUtility.toStringUnchecked(USER_INFO));
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
        configuration.put(OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL, "http://localhost:" + mockWebServer.getPort());
        configuration.put(OAuthConfigKeys.OPEN_ID_DISCOVER_PATH, OIDC_DISCOVERY_PATH);
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_ID, CLIENT_ID);
        configuration.put(OAuthConfigKeys.OPEN_ID_CLIENT_SECRET, CLIENT_SECRET);
        configuration.put(OAuthConfigKeys.EXTERNAL_HOSTNAME, EXTERNAL_HOSTNAME);
        configuration.fireEvent();
    }

    public List<RecordedRequest> nonWellKnownRequests(MockWebServer mockWebServer) throws InterruptedException {
        var builder = new CollectionBuilder<RecordedRequest>();

        var request = mockWebServer.takeRequest();
        while (null != request) {
            if (!request.getPath().contains(OIDC_DISCOVERY_PATH)) {
                builder.add(request);
            }
            request = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS);
        }

        return builder.toImmutableList();
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        LOGGER.info(() -> "Serve request " + request.getPath());
        return switch (request.getPath()) {
        case "/" + OIDC_DISCOVERY_PATH -> new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                            .addHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .setBody(FileLoaderUtility.toStringUnchecked(CONFIGURATION).replaceAll("5602", currentPort));
        case "/auth/realms/master/protocol/openid-connect/userinfo" -> userInfoResult;
        case "/auth/realms/master/protocol/openid-connect/token" -> tokenResult;
        default -> {
            LOGGER.warn(() -> "Unable to serve request " + request.getPath());
            yield new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
        }
        };
    }

}
