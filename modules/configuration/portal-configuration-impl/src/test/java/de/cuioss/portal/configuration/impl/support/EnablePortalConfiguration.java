package de.cuioss.portal.configuration.impl.support;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.weld.junit5.auto.AddBeanClasses;

import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.configuration.impl.schedule.ConfigChangeObserver;
import de.cuioss.portal.configuration.impl.source.InstallationConfigSourcePathInitializer;
import de.cuioss.portal.configuration.impl.source.InstallationFileConfigSource;
import de.cuioss.portal.configuration.impl.source.InstallationProductionFileConfigSource;
import io.smallrye.config.inject.ConfigProducer;

/**
 * Using this annotations at type-level of a junit 5 test provides the basic types for handling
 * configuration in unit-tests. It includes the types:
 *
 * <ul>
 * <li>{@link ConfigProducer}</li>
 * <li>{@link PortalConfigProducer}</li>
 * <li>{@link PortalConfigurationMock}</li>
 * <li>{@link InstallationFileConfigSource}</li>
 * <li>{@link InstallationProductionFileConfigSource}</li>
 * </ul>
 *
 * @author Sven Haag
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@AddBeanClasses({
    ConfigProducer.class,
    PortalConfigProducer.class,
    PortalConfigurationMock.class,
    InstallationConfigSourcePathInitializer.class,
    ConfigChangeObserver.class})
public @interface EnablePortalConfiguration {
}
