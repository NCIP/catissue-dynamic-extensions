package edu.common.dynamicextensions.skiplogic;

import java.util.Comparator;


/**
 * @author Kunal 
 * for sorting conditionStatement Objects in ascending order
 */
public class ConditionStatementComparator implements Comparator<ConditionStatements>
{

	
	@Override
	public int compare(ConditionStatements o1, ConditionStatements o2)
	{
		return (o1.getInsertationOrder()< o2.getInsertationOrder() ? -1 : (o1==o2 ? 0 : 1));
	}

}
