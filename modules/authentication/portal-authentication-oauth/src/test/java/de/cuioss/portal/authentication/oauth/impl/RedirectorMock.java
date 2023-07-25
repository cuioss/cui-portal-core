package de.cuioss.portal.authentication.oauth.impl;

import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.authentication.oauth.OauthRedirector;
import lombok.Getter;

@SuppressWarnings("javadoc")
@ApplicationScoped
public class RedirectorMock implements OauthRedirector {

    @Getter
    private String redirectUrl;

    @Override
    public void sendRedirect(String url) throws IllegalStateException {
        redirectUrl = url;
    }
}
