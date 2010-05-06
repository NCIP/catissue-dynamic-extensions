package edu.wustl.cab2b.client.ui.query;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.client.ui.util.ClientConstants;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;

/**
 * @author Chandrakant Talele
 */
public class UtilityTest extends DynamicExtensionsBaseTestCase {

    public void testGetLogicalOperatorOR() {
        assertEquals(LogicalOperator.Or, Utility.getLogicalOperator(ClientConstants.OPERATOR_OR));
    }

    public void testGetLogicalOperatorAND() {
        assertEquals(LogicalOperator.And, Utility.getLogicalOperator(ClientConstants.OPERATOR_AND));
    }
}