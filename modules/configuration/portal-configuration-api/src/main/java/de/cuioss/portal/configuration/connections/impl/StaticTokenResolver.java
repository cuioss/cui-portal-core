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

import de.cuioss.portal.configuration.connections.TokenResolver;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;

/**
 * Basic Implementation of {@link TokenResolver} used for static tokens that do
 * not change for a specific user.
 *
 * @author Oliver Wolff
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(of = "key") // Only key, because Token may be considered as sensitive data
public class StaticTokenResolver implements TokenResolver {

    @Serial
    private static final long serialVersionUID = 7523596484663692845L;

    @Getter
    private final String key;

    private final String token;

    @Override
    public String resolve() {
        return token;
    }

}
