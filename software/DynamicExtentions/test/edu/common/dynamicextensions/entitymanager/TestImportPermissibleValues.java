
package edu.common.dynamicextensions.entitymanager;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.parser.DynamicallyImportPermissibleValues;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestImportPermissibleValues extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 */
	public void testAddPermissibleValues()
	{
		try
		{
			String[] parameters = {"CPUML/TestModels/TestModel_withTags/editedTestModel_pv.csv",
					"CPUML", APPLICATIONURL};
			DynamicallyImportPermissibleValues.main(parameters);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
