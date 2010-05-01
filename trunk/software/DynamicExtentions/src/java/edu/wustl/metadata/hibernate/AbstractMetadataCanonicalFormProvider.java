package edu.wustl.metadata.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.type.NullableType;

import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.wustl.common.hibernate.CanonicalFormProvider;

public abstract class AbstractMetadataCanonicalFormProvider<T extends AbstractMetadataInterface>
        implements
            CanonicalFormProvider<T, Long> {

    public final boolean equals(T object1, T object2) {
        if (object1 == null && object2 == null) {
            return true;
        }
        // TODO check this
        return object1.equals(object2);
    }

    public final T nullSafeFromCanonicalForm(Long entityId) {
        return entityId == null ? null : getObjectFromEntityCache(entityId);
    }

    public final Long nullSafeToCanonicalForm(T abstractMetadataObj) {
        return abstractMetadataObj == null ? null : abstractMetadataObj.getId();
    }

    public final NullableType canonicalFormType() {
        return Hibernate.LONG;
    }

    public abstract Class<T> objectClass();

    protected abstract T getObjectFromEntityCache(Long identifier);
}
