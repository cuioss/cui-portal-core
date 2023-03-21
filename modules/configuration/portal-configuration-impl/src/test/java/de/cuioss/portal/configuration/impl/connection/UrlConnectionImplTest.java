package de.cuioss.portal.configuration.impl.connection;

import static de.cuioss.test.generator.Generators.strings;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.connections.exception.ConnectionException;
import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.ConnectionType;
import de.cuioss.test.generator.TypedGenerator;

@Disabled("not working in CI anymore. reason unknown.")
class UrlConnectionImplTest {

    private static final String ICW_HOME_PAGE = "http://x-tention.com/";

    private static final String NON_EXISTING_PAGE = "http://not.there.com/";

    private static final TypedGenerator<String> STRINGS = strings(1, 10);

    private UrlConnectionImpl underTest;

    @BeforeEach
    void before() {
        underTest = new UrlConnectionImpl();
        underTest.setConnectionMetadata(ConnectionMetadata.builder()
            .authenticationType(AuthenticationType.NONE)
            .connectionId(STRINGS.next())
            .connectionType(ConnectionType.REST)
            .description(STRINGS.next())
            .serviceUrl(ICW_HOME_PAGE)
            .build());
    }

    @AfterEach
    void after() {
        if (null != underTest) {
            underTest.close();
        }
    }

    @Test
    void shouldAccessExistingPage() throws Exception {
        underTest.checkConnection();
        final var connection = underTest.openConnection();
        assertNotNull(connection);
        assertNotNull(connection.getContentType());
        assertTrue(connection.getContentType().startsWith("text/html"));
    }

    @Test
    void shouldNotCheckConnection() {
        configureNotExistingUrl();
        assertThrows(ConnectionException.class, () -> underTest.checkConnection());
    }

    @Test
    void shouldFailToAccessNotExistingPage() {
        configureNotExistingUrl();
        assertThrows(ConnectionException.class, () -> underTest.openConnection());
    }

    private void configureNotExistingUrl() {
        underTest.setConnectionMetadata(ConnectionMetadata.builder()
            .authenticationType(AuthenticationType.NONE)
            .connectionId(STRINGS.next())
            .connectionType(ConnectionType.REST)
            .description(STRINGS.next())
            .serviceUrl(NON_EXISTING_PAGE).build());
    }
}
