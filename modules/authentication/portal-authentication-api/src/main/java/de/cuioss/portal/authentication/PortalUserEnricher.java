package de.cuioss.portal.authentication;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.portal.configuration.common.PortalPriorities;

/**
 * To enrich an {@link AuthenticatedUserInfo} created by the {@link AuthenticationFacade}.
 *
 * Implementations of this interface will be called as part of
 * {@link AuthenticationFacade#retrieveCurrentAuthenticationContext(HttpServletRequest)}.
 *
 * Multiple implementations can be specified, and differentiated by {@link PortalPriorities}.
 *
 * The highest priority is called last.
 *
 * The enriched {@linkplain AuthenticatedUserInfo} should be returned as result.
 *
 * If no enrichment is done, the same object should be returned, but never null.
 */
public interface PortalUserEnricher extends Function<AuthenticatedUserInfo, AuthenticatedUserInfo> {

}
