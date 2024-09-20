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
package de.cuioss.portal.core.test.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jboss.weld.exceptions.DeploymentException;
import org.junit.jupiter.api.Test;

class BaseModuleConsistencyTestTest extends BaseModuleConsistencyTest {

    @Override
    @Test
    protected void shouldStartUpContainer() {
        // This is not CDI module, therefore this test must fail
        assertThrows(DeploymentException.class, super::shouldStartUpContainer);
    }

    @Override
    @Test
    protected void moduleShouldProvideBeansXml() {
        // This is not CDI module, therefore this test must fail
        assertThrows(AssertionError.class, super::moduleShouldProvideBeansXml);
    }
}
