
package edu.wustl.metadata.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.type.NullableType;

import edu.wustl.common.hibernate.CanonicalFormProvider;
import edu.wustl.common.querysuite.querableobject.QueryableObjectUtility;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

public class QueryableObjectCanonicalFormProvider
		implements
			CanonicalFormProvider<QueryableObjectInterface, Long>

{

	public final boolean equals(QueryableObjectInterface object1, QueryableObjectInterface object2)
	{
		if (object1 == null && object2 == null)
		{
			return true;
		}
		// TODO check this
		return object1.equals(object2);
	}

	public final QueryableObjectInterface nullSafeFromCanonicalForm(Long queryableObjectId)
	{
		return queryableObjectId == null ? null : getObjectFromEntityCache(queryableObjectId);
	}

	public final Long nullSafeToCanonicalForm(QueryableObjectInterface abstractMetadataObj)
	{
		return abstractMetadataObj == null ? null : abstractMetadataObj.getId();
	}

	public final NullableType canonicalFormType()
	{
		return Hibernate.LONG;
	}

	public Class<QueryableObjectInterface> objectClass()
	{
		return QueryableObjectInterface.class;
	}

	protected QueryableObjectInterface getObjectFromEntityCache(Long identifier)
	{
		return QueryableObjectUtility.getQueryableObjectFromCache(identifier);
	}
}
