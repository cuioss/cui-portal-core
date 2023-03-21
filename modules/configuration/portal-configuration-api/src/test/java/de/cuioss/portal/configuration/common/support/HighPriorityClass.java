package de.cuioss.portal.configuration.common.support;

import javax.annotation.Priority;

import de.cuioss.portal.configuration.common.PortalPriorities;

/**
 * @author Oliver Wolff
 *
 */
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL)
public class HighPriorityClass implements SomeInterface {

}
