package de.cuioss.portal.core.test.mocks.core;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.portal.core.storage.SessionStorage;
import de.cuioss.portal.core.storage.impl.MapStorageImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Mock Variant of {@link PortalSessionStorage}
 *
 * @author Oliver Wolff
 */
@ApplicationScoped
@PortalSessionStorage
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PortalSessionStorageMock extends MapStorageImpl<Serializable, Serializable> implements SessionStorage {

    private static final long serialVersionUID = 2230273572072236755L;

    @Getter
    @Setter
    private boolean throwIllegalStateOnAccess = false;

    @Getter
    @Setter
    private boolean throwIllegalStateOnAccessOnce = false;

    @Override
    public boolean containsKey(final Serializable key) {
        checkThrowException();
        return super.containsKey(key);
    }

    @Override
    public Serializable get(final Serializable key) {
        checkThrowException();
        return super.get(key);
    }

    @Override
    public Serializable get(final Serializable key, final Serializable defaultValue) {
        checkThrowException();
        return super.get(key, defaultValue);
    }

    @Override
    public Serializable remove(final Serializable key) {
        checkThrowException();
        return super.remove(key);
    }

    @Override
    public void put(final Serializable key, final Serializable object) {
        checkThrowException();
        super.put(key, object);
    }

    private void checkThrowException() {
        if (throwIllegalStateOnAccessOnce) {
            throwIllegalStateOnAccessOnce = false;
            throw new IllegalStateException("Boom (Once)");
        }
        if (throwIllegalStateOnAccess) {
            throw new IllegalStateException("Boom");
        }
    }

}
