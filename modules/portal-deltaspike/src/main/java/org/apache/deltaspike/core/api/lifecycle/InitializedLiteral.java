package org.apache.deltaspike.core.api.lifecycle;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Annotation literal for {@link Initialized}.
 * 
 * @author https://github.com/apache/deltaspike/blob/deltaspike-1.9.6/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/literal/InitializedLiteral.java
 */
public class InitializedLiteral extends AnnotationLiteral<Initialized> implements Initialized {

    private static final long serialVersionUID = 1268993406072023790L;

    public static final Initialized INSTANCE = new InitializedLiteral();

}
