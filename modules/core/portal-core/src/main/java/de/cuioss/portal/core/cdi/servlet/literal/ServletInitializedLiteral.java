package de.cuioss.portal.core.cdi.servlet.literal;

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
