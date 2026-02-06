/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.listener;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.generator.Generators;
import lombok.Getter;
import lombok.Setter;

@PortalInitializer
class MockInitializer implements ApplicationInitializer {

    @Getter
    boolean initializeCalled;

    @Getter
    boolean destroyCalled;

    @Getter
    @Setter
    boolean explodeOnDestroy = false;

    @Override
    public void initialize() {
        initializeCalled = true;
    }

    @Override
    public void destroy() {
        destroyCalled = true;
        if (explodeOnDestroy) {
            throw Generators.runtimeExceptions().next();
        }
    }
}
