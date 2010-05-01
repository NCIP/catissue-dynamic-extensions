
package edu.wustl.common.querysuite.querableobject;

import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * This class is used in Query for creating a wrapper on the Attribute & Category Attribute
 * which can be used in Query. Cause now onwards Query can be Created on the CategoryAttribute
 * or Attribute of the category or Entity.
 * @author pavan_kalantri
 *
 */
public abstract class AbstractQueryableAttribute implements QueryableAttributeInterface
{

	protected QueryableObjectInterface queryObject;

	/**
	 * It will check on the basis of id of the Attribute or categoryAttribute From which
	 * the QueryableAttribute is Created.
	 * @return true if id are equals else false.
	 */
	public boolean equals(Object queryableAttribute)
	{
		boolean isEqual = false;
		if (queryableAttribute instanceof QueryableAttributeInterface &&
				((QueryableAttributeInterface) queryableAttribute).getId().equals(this.getId()))
		{
			isEqual = true;
		}
		return isEqual;
	}

	@Override
	public int hashCode()
	{
		return 1;
	}
}
