
package edu.wustl.metadata.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.type.NullableType;

import edu.wustl.common.hibernate.CanonicalFormProvider;
import edu.wustl.common.querysuite.querableobject.QueryableObjectUtility;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;

public class QueryableAttributeCanonicalFormProvider
		implements
			CanonicalFormProvider<QueryableAttributeInterface, Long>

{

	public final boolean equals(QueryableAttributeInterface object1,
			QueryableAttributeInterface object2)
	{
		if (object1 == null && object2 == null)
		{
			return true;
		}
		// TODO check this
		return object1.equals(object2);
	}

	public final QueryableAttributeInterface nullSafeFromCanonicalForm(Long queryableObjectId)
	{
		return queryableObjectId == null ? null : getObjectFromEntityCache(queryableObjectId);
	}

	public final Long nullSafeToCanonicalForm(QueryableAttributeInterface abstractMetadataObj)
	{
		return abstractMetadataObj == null ? null : abstractMetadataObj.getId();
	}

	public final NullableType canonicalFormType()
	{
		return Hibernate.LONG;
	}

	public Class<QueryableAttributeInterface> objectClass()
	{
		return QueryableAttributeInterface.class;
	}

	protected QueryableAttributeInterface getObjectFromEntityCache(Long identifier)
	{
		return QueryableObjectUtility.getQueryableAttributeFromCache(identifier);
	}
}
