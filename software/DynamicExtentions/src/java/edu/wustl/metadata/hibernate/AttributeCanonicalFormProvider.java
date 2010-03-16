package edu.wustl.metadata.hibernate;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.wustl.cab2b.server.cache.EntityCache;

public class AttributeCanonicalFormProvider
		extends
			AbstractMetadataCanonicalFormProvider<AbstractAttributeInterface>
{

    public Class<AbstractAttributeInterface> objectClass()
	{
		return AbstractAttributeInterface.class;
    }

    @Override
	protected AbstractAttributeInterface getObjectFromEntityCache(Long identifier)
	{
		AbstractAttributeInterface abstractAttributeInterface = null;
		try
		{
			abstractAttributeInterface = EntityCache.getInstance().getAttributeById(identifier);
		}
		catch (Exception exception)
		{
			abstractAttributeInterface = EntityCache.getInstance().getAssociationById(identifier);
		}
		return abstractAttributeInterface;
    }
}
