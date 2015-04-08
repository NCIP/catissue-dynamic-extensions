package edu.wustl.dynamicextensions.formdesigner.utility;

import java.io.Serializable;
import java.util.Comparator;

import edu.common.dynamicextensions.domain.nui.PermissibleValue;



public class PermissibleValueComparator implements Comparator<PermissibleValue>,Serializable
{

	/**
	 * Compare.
	 * @param permissibleValue1 the permissible value1
	 * @param permissibleValue2 the permissible value2
	 * @return the int
	 */
	@Override
	public int compare(PermissibleValue permissibleValue1,
			PermissibleValue permissibleValue2)
	{
		String value1 = permissibleValue1.getValue().toString();
		String value2 = permissibleValue2.getValue().toString();
		return value1.compareToIgnoreCase(value2);
	}
}
