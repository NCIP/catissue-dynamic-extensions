package edu.wustl.metadata.hibernate;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.wustl.cab2b.server.cache.EntityCache;

public class AssociationCanonicalFormProvider extends AbstractMetadataCanonicalFormProvider<AssociationInterface> {

    public Class<AssociationInterface> objectClass() {
        return AssociationInterface.class;
    }

    @Override
    protected AssociationInterface getObjectFromEntityCache(Long identifier) {
        return EntityCache.getInstance().getAssociationById(identifier);
    }
}
