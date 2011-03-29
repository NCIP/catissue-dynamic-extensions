
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
			String[] parameters = {"CPUML/TestModels/TestModel_withTags/edited/",APPLICATIONURL};
			PermissibleValuesClient.main(parameters);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testAddPermissibleValuesInXMLFormat()
	{
		try
		{
			System.out.println("Inside testAddPermissibleValuesInXMLFormat test case");
			String[] parameters = {"CPUML",APPLICATIONURL,"CPUML/TestModels/TestModel_withTags/original/TestModel_pv.xml","false"};
			PermissibleValuesClient.main(parameters);
			System.out.println("Outside testAddPermissibleValuesInXMLFormat test case");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testEditPermissibleValuesInXMLFormat()
	{
		try
		{
			System.out.println("Inside testEditPermissibleValuesInXMLFormat test case");
			String[] parameters = {"CPUML",APPLICATIONURL,"CPUML/TestModels/TestModel_withTags/edited/TestModel_pv.xml","false"};
			PermissibleValuesClient.main(parameters);
			System.out.println("Outside testEditPermissibleValuesInXMLFormat test case");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testAddPermissibleValuesInXMLFormatRhinovirus()
	{
		try
		{
			System.out.println("Inside testEditPermissibleValuesInXMLFormat test case");
			String[] parameters = {"CPUML",APPLICATIONURL,"CPUML/TestModels/TestModel_withTags/edited/PVs/Rhinovirus.xml","false"};
			PermissibleValuesClient.main(parameters);
			System.out.println("Outside testEditPermissibleValuesInXMLFormat test case");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testAddPermissibleValuesInXMLFormatNewPV()
	{
		try
		{
			System.out.println("Inside testEditPermissibleValuesInXMLFormat test case");
			String[] parameters = {"CPUML",APPLICATIONURL,"CPUML/TestModels/TestModel_withTags/edited/PVs/NewPVs.xml","false"};
			PermissibleValuesClient.main(parameters);
			System.out.println("Outside testEditPermissibleValuesInXMLFormat test case");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
