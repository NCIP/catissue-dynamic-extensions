
package edu.common.dynamicextensions.skiplogic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public enum RelationalOperator {

	EQUALS,

	GREATER_THAN,

	LESS_THAN;

	public boolean evaluateCondition(Object externalValue, Object internalValue) throws DynamicExtensionsSystemException
	{
		boolean result;
		if (this == EQUALS)
		{
			int value = compareEquals(externalValue, internalValue);
			result = value == 0 ? true : false;
		}
		else if (this == GREATER_THAN)
		{
			int value = compareEquals(externalValue, internalValue);
			result = value == 1 ? true : false;
		}
		else
		{
			int value = compareEquals(externalValue, internalValue);
			result = value == -1 ? true : false;
		}
		return result;
	}

	/**
	 * Compare equals.
	 * @param externalValue the external value
	 * @param internalValue the internal value
	 * @return the int
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private int compareEquals(Object externalValue, Object internalValue) throws DynamicExtensionsSystemException
	{
		int comparisionValue = -1;
		if (externalValue instanceof Integer)
		{
			Integer intValue = (Integer) internalValue;
			Integer extValue = (Integer) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Long)
		{
			Long intValue = (Long) internalValue;
			Long extValue = (Long) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Float)
		{
			Float intValue = (Float) internalValue;
			Float extValue = (Float) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof String)
		{
			String intValue = (String) internalValue;
			String extValue = (String) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Date)
		{
			Date intValue = (Date) internalValue;
			Date extValue = (Date) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Double)
		{
			Double intValue = (Double) internalValue;
			Double extValue = (Double) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Short)
		{
			Short intValue = (Short) internalValue;
			Short extValue = (Short) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}
		else if (externalValue instanceof Boolean)
		{
			Boolean intValue = (Boolean) internalValue;
			Boolean extValue = (Boolean) externalValue;
			comparisionValue = intValue.compareTo(extValue);
		}else if (externalValue instanceof List)
		{
			for(Map<BaseAbstractAttributeInterface, String> map : (List<Map<BaseAbstractAttributeInterface, String>>)externalValue)
			{
				if(map.values().contains(internalValue))
				{
					comparisionValue = 0;
					break;
				}else
				{
					comparisionValue = -1;
				}
			}
		}

		return comparisionValue;
	}
}
