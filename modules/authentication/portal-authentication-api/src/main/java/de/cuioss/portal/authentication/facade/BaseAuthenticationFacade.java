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

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.PortalUserEnricher;
import de.cuioss.portal.configuration.common.PortalPriorities;

/**
 * Abstract implementation to handle {@link PortalUserEnricher}s.
 */
public abstract class BaseAuthenticationFacade implements AuthenticationFacade {

    @Inject
    private Instance<PortalUserEnricher> portalUserEnrichers;

    /**
     * Enriches the given {@link AuthenticatedUserInfo} using the available
     * {@link PortalUserEnricher} implemenentations that are part of the
     * cdi-context.
     * <p>
     * The {@linkplain PortalUserEnricher} are sorted via {@link PortalPriorities}
     * if multiple instances are found.
     * <p>
     * The enriched {@linkplain AuthenticatedUserInfo} is returned as result.
     *
     * @param authenticatedUserInfo the instance to enrich
     * @return the enriched instance
     */
    protected AuthenticatedUserInfo enrich(AuthenticatedUserInfo authenticatedUserInfo) {
        final List<PortalUserEnricher> sortedPortalUserEnrichers = PortalPriorities
                .sortByPriority(mutableList(portalUserEnrichers));
        var enrichedAuthenticatedUserInfo = authenticatedUserInfo;
        for (PortalUserEnricher portalUserEnricher : sortedPortalUserEnrichers) {
            enrichedAuthenticatedUserInfo = portalUserEnricher.apply(enrichedAuthenticatedUserInfo);
        }
        return enrichedAuthenticatedUserInfo;
    }

}
