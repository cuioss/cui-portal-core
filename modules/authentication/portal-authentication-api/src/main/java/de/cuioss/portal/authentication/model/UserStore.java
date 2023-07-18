package de.cuioss.portal.authentication.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A {@link UserStore} represents an entry, local, or ldap server. The optional
 * display name can be used for adjusting the technical name. If they do not
 * differ, you can use {@link #UserStore(String)} as simplified constructor.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class UserStore implements Serializable {

    private static final long serialVersionUID = -1854435554671404250L;

    @Getter
    @NonNull
    private final String name;

    @Getter
    @NonNull
    private final String displayName;

    /**
     * Constructor.
     *
     * @param name to be set, must not be null. Will be used for {@link #getName()}
     *             and {@link #getDisplayName()}
     */
    public UserStore(@NonNull final String name) {
        this.name = name;
        displayName = name;
    }
}
