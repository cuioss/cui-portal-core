/**
 * Members of this package help to initialize
 * {@link javax.enterprise.context.ApplicationScoped} beans eagerly in a
 * specified order. The actual initialization will be done by a ServletContext
 * initializer. This replaces the {@link javax.annotation.PostConstruct} based
 * approach, because with that the initialization order will be determined by
 * the access order and will therefore not be deterministic.
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.initializer;
