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
package de.cuioss.portal.authentication.facade;

import static de.cuioss.tools.collect.CollectionLiterals.mutableList;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUserEnricher;
import de.cuioss.portal.common.priority.PortalPriorities;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Base implementation of {@link AuthenticationFacade} that provides common functionality
 * for authentication handling.
 * 
 * <p>This class is thread-safe as it relies on CDI-managed components and immutable state.
 * Implementations should ensure thread-safety by following the same principles.</p>
 * 
 * @author Oliver Wolff
 */
public abstract class BaseAuthenticationFacade implements AuthenticationFacade {

    @Inject
    private Instance<PortalUserEnricher> portalUserEnrichers;

    /**
     * Enriches the given {@link AuthenticatedUserInfo} using the available
     * {@link PortalUserEnricher} implementations that are part of the
     * cdi-context.
     * <p>
     * The {@linkplain PortalUserEnricher} are sorted via {@link PortalPriorities}
     * if multiple instances are found.
     * <p>
     * The enriched {@linkplain AuthenticatedUserInfo} is returned as a result.
     *
     * @param authenticatedUserInfo the instance to enrich, may be null
     * @return the enriched instance if authenticatedUserInfo is not null, otherwise null
     */
    protected AuthenticatedUserInfo enrich(AuthenticatedUserInfo authenticatedUserInfo) {
        if (null == authenticatedUserInfo) {
            return null;
        }
        
        final List<PortalUserEnricher> sortedPortalUserEnrichers = PortalPriorities
                .sortByPriority(mutableList(portalUserEnrichers));
                
        var enrichedAuthenticatedUserInfo = authenticatedUserInfo;
        for (PortalUserEnricher portalUserEnricher : sortedPortalUserEnrichers) {
            var enriched = portalUserEnricher.apply(enrichedAuthenticatedUserInfo);
            if (enriched != null) {
                enrichedAuthenticatedUserInfo = enriched;
            }
        }
        return enrichedAuthenticatedUserInfo;
    }

}
