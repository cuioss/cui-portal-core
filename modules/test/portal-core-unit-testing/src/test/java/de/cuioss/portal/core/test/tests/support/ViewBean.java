package de.cuioss.portal.core.test.tests.support;

import java.io.Serializable;

import javax.faces.view.ViewScoped;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("javadoc")
@ViewScoped
@EqualsAndHashCode
@ToString
public class ViewBean implements Serializable {

    private static final long serialVersionUID = 1979191939901287141L;

    @Getter
    @Setter
    private String name;

    @Getter
    private boolean initCalled = false;

    public String initBean() {
        initCalled = true;
        return null;
    }
}
