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
package de.cuioss.portal.authentication.oauth;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier annotation for injecting the OAuth2 login page path.
 * This qualifier is used to identify the string value representing the path
 * to the application's login page, which is used as the redirect target
 * after OAuth2 authentication.
 * 
 * <p>Usage:
 * <pre>
 * &#64;Inject
 * &#64;LoginPagePath
 * private String loginPath;
 * </pre>
 * 
 * <p>The injected path should be relative to the application context and
 * will be used to construct the complete OAuth2 redirect URI.
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface LoginPagePath {

}
