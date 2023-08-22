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
package de.cuioss.portal.authentication;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.configuration.common.PortalPriorities;

/**
 * To enrich an {@link AuthenticatedUserInfo} created by the
 * {@link AuthenticationFacade}.
 * <p>
 * Implementations of this interface will be called as part of
 * {@link AuthenticationFacade#retrieveCurrentAuthenticationContext(HttpServletRequest)}.
 * <p>
 * Multiple implementations can be specified, and differentiated by
 * {@link PortalPriorities}.
 * <p>
 * The highest priority is called last.
 * <p>
 * The enriched {@linkplain AuthenticatedUserInfo} should be returned as result.
 * <p>
 * If no enrichment is done, the same object should be returned, but never null.
 */
public interface PortalUserEnricher extends Function<AuthenticatedUserInfo, AuthenticatedUserInfo> {

}
