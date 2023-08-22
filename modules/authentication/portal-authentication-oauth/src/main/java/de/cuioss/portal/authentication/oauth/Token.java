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
package de.cuioss.portal.authentication.oauth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Oauth2 token.
 *
 * @author Matthias Walliczek
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token implements Serializable {

    private static final long serialVersionUID = 1814898874197817661L;

    private String id_token;
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String state;
}
