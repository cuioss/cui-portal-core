package de.cuioss.portal.core.cdi.servlet.literal;

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
