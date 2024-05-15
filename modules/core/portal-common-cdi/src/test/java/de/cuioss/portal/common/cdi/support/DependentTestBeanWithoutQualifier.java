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
package de.cuioss.portal.common.cdi.support;

import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import jakarta.annotation.PostConstruct;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Oliver Wolff
 */
@SuppressWarnings("cdi-ambiguous-name")
@Named
@RequestScoped
@EqualsAndHashCode
@ToString
public class DependentTestBeanWithoutQualifier implements Serializable {

    private static final long serialVersionUID = -5664254977845330549L;

    /**
     * Test message, for checking whether initializer was called
     */
    public static final String MESSAGE = "init was called";

    @Getter
    private String initMessage;

    /**
     * Test initializer.
     */
    @PostConstruct
    public void initBean() {
        this.initMessage = MESSAGE;
    }

}
