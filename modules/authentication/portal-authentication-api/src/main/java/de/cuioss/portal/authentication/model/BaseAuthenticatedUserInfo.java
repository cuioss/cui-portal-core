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
package de.cuioss.portal.authentication.model;

import static java.util.Objects.requireNonNull;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Implementation of {@link AuthenticatedUserInfo} suitable for most
 * uses-cases. It can be overwritten but the extension point for custom
 * attributes is {@link #getContextMap()}
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class BaseAuthenticatedUserInfo implements AuthenticatedUserInfo {

    @Serial
    private static final long serialVersionUID = 5675055136230020827L;

    @Getter
    private final boolean authenticated;

    @Getter
    private final String displayName;

    @Getter
    private final String identifier;

    @Getter
    private final String qualifiedIdentifier;

    @Getter
    private final String system;

    @Getter
    private final List<String> groups = new ArrayList<>();

    @Getter
    private final List<String> roles = new ArrayList<>();

    @Getter
    private final Map<Serializable, Serializable> contextMap = new HashMap<>();

    /**
     * Builder for instances of {@link BaseAuthenticatedUserInfo}
     *
     * @author Oliver Wolff
     */
    public static class BaseAuthenticatedUserInfoBuilder {

        private boolean authenticated;

        private String displayName;

        private String identifier;

        private String qualifiedIdentifier;

        private String system;

        private List<String> groups = new ArrayList<>();

        private List<String> roles = new ArrayList<>();

        private Map<Serializable, Serializable> contextMap = new HashMap<>();

        /**
         * @param authenticated
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder authenticated(final boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        /**
         * @param displayName
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder displayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * @param identifier
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder identifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * @param qualifiedIdentifier
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder qualifiedIdentifier(final String qualifiedIdentifier) {
            this.qualifiedIdentifier = qualifiedIdentifier;
            return this;
        }

        /**
         * @param system
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder system(final String system) {
            this.system = system;
            return this;
        }

        /**
         * @param groups
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder groups(final List<String> groups) {
            requireNonNull(groups);
            this.groups = groups;
            return this;
        }

        /**
         * @param roles
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder roles(final List<String> roles) {
            requireNonNull(roles);
            this.roles = roles;
            return this;
        }

        /**
         * @param contextMap
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder contextMap(final Map<Serializable, Serializable> contextMap) {
            this.contextMap = contextMap;
            return this;
        }

        /**
         * @param key
         * @param value
         * @return the builder for {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfoBuilder contextMapElement(final Serializable key, final Serializable value) {
            contextMap.put(key, value);
            return this;
        }

        /**
         * @return the built {@link BaseAuthenticatedUserInfo}
         */
        public BaseAuthenticatedUserInfo build() {
            final var baseAuthenticatedUserInfo = new BaseAuthenticatedUserInfo(authenticated, displayName, identifier,
                    qualifiedIdentifier, system);
            if (!contextMap.isEmpty()) {
                baseAuthenticatedUserInfo.contextMap.putAll(contextMap);
            }
            if (!groups.isEmpty()) {
                baseAuthenticatedUserInfo.groups.addAll(groups);
            }
            if (!roles.isEmpty()) {
                baseAuthenticatedUserInfo.roles.addAll(roles);
            }
            return baseAuthenticatedUserInfo;
        }
    }

    /**
     * @return the builder for {@link BaseAuthenticatedUserInfo}
     */
    public static BaseAuthenticatedUserInfoBuilder builder() {
        return new BaseAuthenticatedUserInfoBuilder();
    }

}
