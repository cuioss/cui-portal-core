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
package de.cuioss.portal.configuration.connections.impl;

import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static de.cuioss.test.generator.Generators.strings;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.connections.TokenResolver;
import de.cuioss.portal.configuration.connections.exception.ConnectionConfigurationException;
import de.cuioss.portal.configuration.connections.impl.generator.ContextMapGenerator;
import de.cuioss.portal.configuration.connections.impl.generator.KeyStoreInfoGenerator;
import de.cuioss.portal.configuration.connections.impl.generator.TrustStoreInfoGenerator;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBuilder;
import de.cuioss.test.valueobjects.api.generator.PropertyGenerator;
import de.cuioss.test.valueobjects.api.generator.PropertyGeneratorHint;
import de.cuioss.test.valueobjects.api.object.ObjectTestConfig;
import de.cuioss.test.valueobjects.api.property.PropertyReflectionConfig;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;
import de.cuioss.uimodel.application.LoginCredentials;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

@PropertyReflectionConfig(exclude = {"loginCredentialsNecessary"})
@ObjectTestConfig(equalsAndHashCodeBasicOnly = true)
@PropertyGenerator({KeyStoreInfoGenerator.class, TrustStoreInfoGenerator.class, ContextMapGenerator.class})
@PropertyGeneratorHint(declaredType = TokenResolver.class, implementationType = StaticTokenResolver.class)
@VerifyBuilder
class ConnectionMetadataTest extends ValueObjectTest<ConnectionMetadata> {

    private static final String URL = "https://cuioss.de";
    private final TypedGenerator<String> stringGenerator = strings(1, 10);
    private final KeyStoreInfoGenerator keystoreInfos = new KeyStoreInfoGenerator();
    private final TrustStoreInfoGenerator truststoreInfos = new TrustStoreInfoGenerator();

    @Test
    void shouldCreateGoodCase() throws Exception {
        assertNotNull(getAnyValid());
        getAnyValid().validate();
    }

    @Test
    void shouldFailWithMissingCredentials() {
        final var builder = ConnectionMetadata.builder();
        builder.authenticationType(AuthenticationType.BASIC).connectionId(stringGenerator.next())
                .connectionType(ConnectionType.REST).description(stringGenerator.next()).serviceUrl(URL);
        assertThrows(ConnectionConfigurationException.class, () -> builder.build().validate());
    }

    @Test
    void shouldFailWithInvalidCredentials() {
        final var builder = ConnectionMetadata.builder();
        builder.authenticationType(AuthenticationType.BASIC).connectionId(stringGenerator.next())
                .connectionType(ConnectionType.REST).description(stringGenerator.next()).serviceUrl(URL);
        builder.loginCredentials(new LoginCredentials());
        assertThrows(ConnectionConfigurationException.class, () -> builder.build().validate());
    }

    @Test
    void shouldFailWithMissingTokenResolver() {
        final var meta = getAnyValid();
        meta.setAuthenticationType(AuthenticationType.TOKEN_APPLICATION);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldValidateWithTokenResolver() throws Exception {
        final var meta = getAnyValid();
        meta.setAuthenticationType(AuthenticationType.TOKEN_APPLICATION);
        meta.setTokenResolver(new StaticTokenResolver(nonEmptyStrings().next(), nonEmptyStrings().next()));
        meta.validate();
    }

    @Test
    void shouldFailWithMissingServiceUrl() {
        final var meta = getAnyValid();
        meta.setServiceUrl(null);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldFailWithMissingAuthenticationType() {
        final var meta = getAnyValid();
        meta.setAuthenticationType(null);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldFailWithMissingConnectionType() {
        final var meta = getAnyValid();
        meta.setConnectionType(null);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldFailWithInvalidProxyPort() {
        final var meta = getAnyValid();
        meta.setProxyHost("bla");
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldFailWithInvalidProxyHost() {
        final var meta = getAnyValid();
        meta.setProxyPort(123);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldFailWithMissingConnectionId() {
        final var meta = getAnyValid();
        meta.setConnectionId(null);
        assertThrows(ConnectionConfigurationException.class, meta::validate);
    }

    @Test
    void shouldReturnDefaultSSLContext() {
        // Neither key nor truststore
        var meta = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL).build();
        assertNotNull(meta.resolveSSLContext());

        // Truststore only
        meta = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next()).trustStoreInfo(truststoreInfos.next())
                .connectionType(ConnectionType.REST).description(stringGenerator.next()).serviceUrl(URL).build();
        assertNotNull(meta.resolveSSLContext());

        // keystore only
        meta = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next()).keyStoreInfo(keystoreInfos.next())
                .connectionType(ConnectionType.REST).description(stringGenerator.next()).serviceUrl(URL).build();
        assertNotNull(meta.resolveSSLContext());

        // not existing keystore-file
        var metaInvalidFile = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next())
                .keyStoreInfo(KeyStoreProvider.builder().keyStoreType(KeyStoreType.KEY_STORE)
                        .location(new File("not/there")).build())
                .trustStoreInfo(truststoreInfos.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL).build();
        assertThrows(IllegalStateException.class, metaInvalidFile::resolveSSLContext);

        // Invalid keystore-password
        var metaInvalidPassword = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next())
                .keyStoreInfo(KeyStoreProvider.builder().keyStoreType(KeyStoreType.KEY_STORE)
                        .location(keystoreInfos.next().getLocation()).storePassword("wrongpassword").build())
                .trustStoreInfo(truststoreInfos.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL).build();
        assertThrows(IllegalStateException.class, metaInvalidPassword::resolveSSLContext);
    }

    @Test
    void shouldReturnCustomSSLContext() {
        final var meta = ConnectionMetadata.builder().authenticationType(AuthenticationType.BASIC)
                .connectionId(stringGenerator.next()).keyStoreInfo(keystoreInfos.next())
                .trustStoreInfo(truststoreInfos.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL).build();
        assertNotNull(meta.resolveSSLContext());
        // Should be reentrant
        assertNotNull(meta.resolveSSLContext());
    }

    @Test
    void shouldHandleContextData() {
        final var minimum = ConnectionMetadata.builder().authenticationType(AuthenticationType.NONE)
                .connectionId(stringGenerator.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL);
        final var key = stringGenerator.next();
        final var value = stringGenerator.next();
        final var build = minimum.build();
        build.contextMapElement(key, value);
        assertNotNull(build.getContextMap());
        assertTrue(build.getContextMap().containsKey(key));
        assertEquals(value, build.getContextMap().get(key));
    }

    @Test
    void shouldHaveDefaultTimeUnits() {
        final var meta = ConnectionMetadata.builder().build();
        assertEquals(TimeUnit.SECONDS, meta.getConnectionTimeoutUnit());
        assertEquals(TimeUnit.SECONDS, meta.getReadTimeoutUnit());
    }

    private ConnectionMetadata getAnyValid() {
        return ConnectionMetadata.builder().authenticationType(AuthenticationType.NONE)
                .connectionId(stringGenerator.next()).connectionType(ConnectionType.REST)
                .description(stringGenerator.next()).serviceUrl(URL).build();
    }

}
