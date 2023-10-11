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
package de.cuioss.portal.common.bundle;

import static de.cuioss.tools.collect.CollectionLiterals.immutableList;

import java.util.List;

import javax.annotation.Priority;

import de.cuioss.portal.common.priority.PortalPriorities;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@PortalResourceBundleLocator
@Priority(PortalPriorities.PORTAL_ASSEMBLY_LEVEL)
@EqualsAndHashCode
@ToString
public class DefectBundle implements ResourceBundleLocator {

    private static final long serialVersionUID = 7756501560722570148L;

    public static final String HIGH_1 = "de.cuioss.portal.l18n.messages.missing";

    @Override
    public List<String> getConfiguredResourceBundles() {
        return immutableList(HIGH_1);
    }
}
