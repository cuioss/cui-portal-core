package de.cuioss.portal.core.cdi.support;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Oliver Wolff
 */
@TestAnnotation
@RequestScoped
@EqualsAndHashCode
@ToString
@Priority(20)
public class TestBeanWithQualifierAndPriority20 implements TestInterface, Serializable {

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
