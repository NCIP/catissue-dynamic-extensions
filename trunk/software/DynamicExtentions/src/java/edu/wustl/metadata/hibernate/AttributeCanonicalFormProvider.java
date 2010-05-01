package edu.wustl.metadata.hibernate;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.cab2b.server.cache.EntityCache;

public class AttributeCanonicalFormProvider extends AbstractMetadataCanonicalFormProvider<AttributeInterface> {

    public Class<AttributeInterface> objectClass() {
        return AttributeInterface.class;
    }

    @Override
    protected AttributeInterface getObjectFromEntityCache(Long identifier) {
        return EntityCache.getInstance().getAttributeById(identifier);
    }
}
