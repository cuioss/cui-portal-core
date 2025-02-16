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
package de.cuioss.portal.authentication.token;

import de.cuioss.tools.base.Preconditions;
import de.cuioss.tools.logging.CuiLogger;
import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;

/**
 * Variant of {@link JWTParser} that will be configured for remote loading of the public-keys.
 * They are necessary to verify the signature or the token.
 *
 * @author Oliver Wolff
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class JwksAwareTokenParser implements JWTParser {

    private static final CuiLogger LOGGER = new CuiLogger(JwksAwareTokenParser.class);

    @Delegate
    private final JWTParser tokenParser;

    @Getter
    private final String jwksIssuer;

    public static class Builder {

        private final JWTAuthContextInfo containedContextInfo;

        Builder() {
            containedContextInfo = new JWTAuthContextInfo();
        }

        /**
         * @param jwksIssuer must not be {@code null}. Represents the allowed issuer for token to be verified.
         * @return the {@link Builder} itself
         */
        public Builder jwksIssuer(@NonNull String jwksIssuer) {
            containedContextInfo.setIssuedBy(jwksIssuer);
            return this;
        }

        /**
         * @param jwksRefreshIntervall If not set, it will be defaulted to '100'
         * @return the {@link Builder} itself
         */
        public Builder jwksRefreshIntervall(Integer jwksRefreshIntervall) {
            containedContextInfo.setJwksRefreshInterval(jwksRefreshIntervall);
            return this;
        }

        /**
         * @param jwksEndpoint must not be {@code null}
         * @return the {@link Builder} itself
         */
        public Builder jwksEndpoint(@NonNull String jwksEndpoint) {
            containedContextInfo.setPublicKeyLocation(jwksEndpoint);
            return this;
        }

        /**
         * Sets the tlsCertificatePath the ssl-connection
         *
         * @param tlsCertificatePath to be set
         * @return the {@link Builder} itself
         */
        public Builder tTlsCertificatePath(String tlsCertificatePath) {
            containedContextInfo.setTlsCertificatePath(tlsCertificatePath);
            return this;
        }

        /**
         * Sets the public-key content for the verification of the token
         *
         * @param jwksKeyContent to be set
         * @return the {@link Builder} itself
         */
        public Builder jwksKeyContent(String jwksKeyContent) {
            containedContextInfo.setPublicKeyContent(jwksKeyContent);
            return this;
        }

        /**
         * Build the {@link JwksAwareTokenParser}
         * return the configured {@link JwksAwareTokenParser}
         */
        public JwksAwareTokenParser build() {
            Preconditions.checkArgument(null != containedContextInfo.getIssuedBy(), "jwksIssuer must be set");
            Preconditions.checkArgument(null != containedContextInfo.getPublicKeyLocation() || null != containedContextInfo.getPublicKeyContent(), "either jwksEndpoint or getPublicKeyContent must be set");
            if (null != containedContextInfo.getJwksRefreshInterval()) {
                LOGGER.debug("Defaulting jwksRefreshIntervall to %s", 180);
                containedContextInfo.setJwksRefreshInterval(180);
            }
            LOGGER.info(PortalTokenLogMessages.CONFIGURED_JWKS.format(containedContextInfo.getPublicKeyLocation(), containedContextInfo.getJwksRefreshInterval(), containedContextInfo.getIssuedBy()));
            return new JwksAwareTokenParser(new DefaultJWTParser(containedContextInfo), containedContextInfo.getIssuedBy());
        }
    }

    /**
     * Get a newly created builder
     */
    public static Builder builder() {
        return new Builder();
    }

}
