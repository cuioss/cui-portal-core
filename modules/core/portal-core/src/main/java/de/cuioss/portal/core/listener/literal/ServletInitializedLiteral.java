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
package de.cuioss.portal.core.listener.literal;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Annotation literal for {@link ServletInitialized}.
 *
 * @author https://github.com/apache/deltaspike/blob/deltaspike-1.9.6/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/literal/InitializedLiteral.java
 */
public class ServletInitializedLiteral extends AnnotationLiteral<ServletInitialized> implements ServletInitialized {

    private static final long serialVersionUID = 1268993406072023790L;

    public static final ServletInitialized INSTANCE = new ServletInitializedLiteral();

}
