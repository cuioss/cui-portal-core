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

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A {@link UserStore} represents an entry, local, or ldap server. The optional
 * display name can be used for adjusting the technical name. If they do not
 * differ, you can use {@link #UserStore(String)} as simplified constructor.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class UserStore implements Serializable {

    private static final long serialVersionUID = -1854435554671404250L;

    @Getter
    @NonNull
    private final String name;

    @Getter
    @NonNull
    private final String displayName;

    /**
     * Constructor.
     *
     * @param name to be set, must not be null. Will be used for {@link #getName()}
     *             and {@link #getDisplayName()}
     */
    public UserStore(@NonNull final String name) {
        this.name = name;
        displayName = name;
    }
}
