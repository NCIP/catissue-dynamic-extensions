
package edu.wustl.common.querysuite.querableobject;

import java.util.Comparator;

import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;

/**
 * This class implements Comparator for QueryableAttributeInterface. It compares 
 * attributes on basis of their names.
 * 
 * @author pavan_kalantri
 */

public class QueryableAttributeInterfaceComparator
		implements
			Comparator<QueryableAttributeInterface>
{

	/**
	 * This method compares the given attributes by name
	 * @param attribute1
	 * @param attribute2
	 * 
	 * @return -1 if name of first attribute is smaller
	 *          0 if both attribute has same name
	 *          1 if name of first attribute is greater
	 */
	public int compare(QueryableAttributeInterface attribute1,
			QueryableAttributeInterface attribute2)
	{
		return attribute1.getName().compareToIgnoreCase(attribute2.getName());
	}

}
