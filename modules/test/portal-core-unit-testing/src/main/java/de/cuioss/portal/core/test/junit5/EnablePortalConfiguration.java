package de.cuioss.portal.core.test.junit5;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.configuration.impl.schedule.ConfigChangeObserver;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import io.smallrye.config.inject.ConfigProducer;

/**
 * Using this annotations at type-level of a junit 5 test provides the basic
 * types for handling configuration in unit-tests. It includes the types:
 *
 * <ul>
 * <li>{@link ConfigProducer}</li>
 * <li>{@link PortalTestConfiguration}</li>
 * <li>{@link PortalConfigProducer}</li>
 * <li>{@link ConfigChangeObserver}</li>
 * </ul>
 * <p>
 * Additional configuration can be added by using {@link #configuration()}
 * </p>
 *
 * @author Oliver Wolff
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@AddBeanClasses({ ConfigProducer.class, PortalTestConfiguration.class, PortalConfigProducer.class,
        InstallationConfigSourcePathInitializer.class, ConfigChangeObserver.class })
@ExtendWith(PortalTestConfigurationExtension.class)
public @interface EnablePortalConfiguration {

    /**
     * @return an array of Strings representing additional configuration elements to
     *         be applied for each test. The individual Strings are expected in the
     *         form "key:value", e.g. "portal.locale.default:de"
     */
    String[] configuration() default {};
}
