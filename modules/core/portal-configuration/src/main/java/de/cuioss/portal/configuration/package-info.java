/**
 * <h2>Summary</h2>
 * <p>
 * Provides configuration specific classes and structures.
 * </p>
 * <h2>Usage</h2>
 * <p>
 * The configuration system can be consumed in a simple way.
 *
 * <pre>
 * <code>
 * &#64;Inject
 * &#64;ConfigProperty(name="some.property")
 * private String someProperty;</code>
 * </pre>
 * <p>
 * The {@link org.eclipse.microprofile.config.inject.ConfigProperty} is capable
 * of automatically casting in standard types, like {@link java.lang.Integer}
 * {@link java.lang.Double} or boolean. More complex types are provided by own
 * annotations / producers like:
 * {@link de.cuioss.portal.configuration.types.ConfigAsFileLoader},
 * {@link de.cuioss.portal.configuration.types.ConfigAsFileLoaderList},
 * {@link de.cuioss.portal.configuration.types.ConfigAsList},
 * {@link de.cuioss.portal.configuration.types.ConfigAsSet}
 * </p>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration;
