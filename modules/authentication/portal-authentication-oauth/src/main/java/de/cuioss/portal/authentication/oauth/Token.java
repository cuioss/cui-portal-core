package de.cuioss.portal.authentication.oauth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Oauth2 token.
 *
 * @author Matthias Walliczek
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token implements Serializable {

    private static final long serialVersionUID = 1814898874197817661L;

    private String id_token;
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String state;
}
