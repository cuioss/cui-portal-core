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

/**
 * Helper class for redirecting to another (external) URL. To be used in non jsf
 * modules.
 *
 */
public interface OauthRedirector {

    /**
     * Redirect to another url. The url is expected to be complete and independent
     * from the current context.
     *
     * @param url the url as string
     * @throws IllegalStateException if an exception occurred during redirect.
     */
    void sendRedirect(String url) throws IllegalStateException;

}
