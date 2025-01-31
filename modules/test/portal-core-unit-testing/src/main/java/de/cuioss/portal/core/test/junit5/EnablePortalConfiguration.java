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
package de.cuioss.portal.core.test.junit5;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import de.cuioss.portal.configuration.impl.producer.PortalConfigProducer;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import io.smallrye.config.inject.ConfigProducer;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Using this annotation at type-level of a junit 5 test provides the basic
 * types for handling configuration in unit-tests. It includes the types:
 *
 * <ul>
 * <li>{@link ConfigProducer}</li>
 * <li>{@link PortalTestConfiguration}</li>
 * <li>{@link PortalConfigProducer}</li>
 * </ul>
 * <p>
 * Additional configuration can be added by using {@link #configuration()}
 * </p>
 * <p>
 * For details on usage, see {@link PortalTestConfiguration}
 * </p>
 *
 * @author Oliver Wolff
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@AddBeanClasses({ConfigProducer.class, PortalTestConfiguration.class, PortalConfigProducer.class})
@ExtendWith(PortalTestConfigurationExtension.class)
public @interface EnablePortalConfiguration {

    /**
     * @return an array of Strings representing additional configuration elements to
     * be applied for each test. The individual Strings are expected in the
     * form "key:value", e.g. "portal.locale.default:de"
     */
    String[] configuration() default {};
}
