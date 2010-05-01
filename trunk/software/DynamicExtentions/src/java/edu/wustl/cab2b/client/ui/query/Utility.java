package edu.wustl.cab2b.client.ui.query;

import edu.wustl.cab2b.client.ui.util.ClientConstants;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;


public class Utility
{
    public static LogicalOperator getLogicalOperator(String operator) 
    {
        LogicalOperator logicalOperator = LogicalOperator.And;
        if (ClientConstants.OPERATOR_OR.equals(operator)) {
            logicalOperator = LogicalOperator.Or;
        }

        return logicalOperator;
    }

}
