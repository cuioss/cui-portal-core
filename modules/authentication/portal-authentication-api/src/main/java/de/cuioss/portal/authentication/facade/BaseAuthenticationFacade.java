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
     * {@link PortalUserEnricher} implemenentations that are part of the cdi-context.
     *
     * The {@linkplain PortalUserEnricher} are sorted via {@link PortalPriorities} if multiple
     * instances are found.
     *
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
