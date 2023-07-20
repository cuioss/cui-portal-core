package de.cuioss.portal.core.cdi.servlet.literal;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Annotation literal for {@link DestroyedLiteral}.
 * 
 * @author https://github.com/apache/deltaspike/blob/deltaspike-1.9.6/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/literal/DestroyedLiteral.java
 */
public class DestroyedLiteral extends AnnotationLiteral<Initialized> implements Destroyed {

    private static final long serialVersionUID = 5587631398288144209L;
    public static final Destroyed INSTANCE = new DestroyedLiteral();

}
