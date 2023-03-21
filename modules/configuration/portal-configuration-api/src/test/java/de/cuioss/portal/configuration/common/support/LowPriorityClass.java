package de.cuioss.portal.configuration.common.support;

import javax.annotation.Priority;

import de.cuioss.portal.configuration.common.PortalPriorities;

/**
 * @author Oliver Wolff
 *
 */
@Priority(PortalPriorities.PORTAL_CORE_LEVEL)
public class LowPriorityClass implements SomeInterface {

}
