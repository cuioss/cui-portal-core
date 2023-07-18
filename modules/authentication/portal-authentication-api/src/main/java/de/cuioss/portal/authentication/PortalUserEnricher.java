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
