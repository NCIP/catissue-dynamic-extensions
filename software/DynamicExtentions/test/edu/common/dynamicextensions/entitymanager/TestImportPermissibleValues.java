
package edu.common.dynamicextensions.entitymanager;

import edu.common.dynamicextensions.client.PermissibleValuesClient;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

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
			String[] parameters = {"CPUML/TestModels/TestModel_withTags/edited/TestModel_pv.csv",
					"CPUML", APPLICATIONURL};
			PermissibleValuesClient.main(parameters);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
