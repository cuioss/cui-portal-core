package de.cuioss.portal.core.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("javadoc")
@ApplicationScoped
public class MockPortalServlet extends AbstractPortalServlet {

    private static final long serialVersionUID = -6932935998287415457L;

    @Getter
    @Setter
    private boolean enabled = false;

    @Getter
    @Setter
    private boolean loggedInUserRequired = false;

    @Getter
    private final Collection<String> requiredRoles = new ArrayList<>();

    @Setter
    private IOException throwMe = null;

    @Override
    public void executeDoGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (null != throwMe) {
            throw throwMe;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public void reset() {
        throwMe = null;
        enabled = false;
        loggedInUserRequired = false;
        requiredRoles.clear();
    }
}
