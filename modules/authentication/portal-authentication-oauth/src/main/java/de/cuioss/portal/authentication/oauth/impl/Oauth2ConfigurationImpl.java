package de.cuioss.portal.authentication.oauth.impl;

import java.util.List;

import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Default implementation.
 *
 * @author Matthias Walliczek
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2ConfigurationImpl implements Oauth2Configuration {

    private static final long serialVersionUID = 6666334248327168722L;

    private String clientId;

    private String clientSecret;

    private String authorizeUri;

    private String userInfoUri;

    private String tokenUri;

    private String externalContextPath;

    private String initialScopes;

    private String logoutUri;

    private List<String> roleMapperClaims;

    private String logoutRedirectParamName;

    private String postLogoutRedirectUri;

    private boolean logoutWithIdTokenHintEnabled;
}
