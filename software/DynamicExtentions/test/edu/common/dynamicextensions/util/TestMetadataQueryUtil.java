package edu.common.dynamicextensions.util;

import java.io.FileNotFoundException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.MetadataQueryUtility;
import edu.wustl.dao.exception.DAOException;

public class TestMetadataQueryUtil extends DynamicExtensionsBaseTestCase
{

	public void testTableNamesColumnNamesQuery()
	{
		String[] args = new String[]{"src/conf/metadataQuery.xml",
				"tableNamesColumnNamesQuery",
				"test", "PhyContactInfo"};
		MetadataQueryUtility metadataQueryUtility;
		try
		{
			metadataQueryUtility = new MetadataQueryUtility(args[0], args[1]);
			metadataQueryUtility.generateCSV(args);
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
		}
		catch (DAOException e)
		{
			fail();
		}
		catch (FileNotFoundException e)
		{
			fail();
		}

	}
}
