package edu.wustl.metadata.hibernate;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;

public class EntityCanonicalFormProvider extends AbstractMetadataCanonicalFormProvider<EntityInterface> {

    public Class<EntityInterface> objectClass() {
        return EntityInterface.class;
    }

    @Override
    protected EntityInterface getObjectFromEntityCache(Long identifier) {
        return EntityCache.getInstance().getEntityById(identifier);
    }
}
