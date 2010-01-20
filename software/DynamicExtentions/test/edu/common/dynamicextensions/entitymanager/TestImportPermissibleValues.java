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
			String[] parameters = {PV_FILE_PATH+"PermissibleValues.csv",PV_FILE_PATH,APPLICATIONURL};
			DynamicallyImportPermissibleValues.main(parameters);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
