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
 * Annotation literal for {@link ServletDestroyedLiteral}.
 *
 * @author https://github.com/apache/deltaspike/blob/deltaspike-1.9.6/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/literal/DestroyedLiteral.java
 */
public class ServletDestroyedLiteral extends AnnotationLiteral<ServletInitialized> implements ServletDestroyed {

    private static final long serialVersionUID = 5587631398288144209L;
    public static final ServletDestroyed INSTANCE = new ServletDestroyedLiteral();

}
