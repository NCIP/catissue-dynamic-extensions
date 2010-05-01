
package edu.common.dynamicextensions.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * This class is used for proving de upgrade fixes. 
 * @author kunal_kamble
 *
 */
public class DynamicExtensionsDBFixer
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String param = args[0];
		if (param.equalsIgnoreCase("upgrade_1.2.0_to_1.2.1"))
		{
			fixNullSourceNamesIssue();
		}
	}

	/**
	 * @throws IOException
	 */
	public static void fixNullSourceNamesIssue() throws IOException
	{
		FileWriter dbFixesSQL = null;
		try
		{

			EntityManagerInterface entityManager = EntityManager.getInstance();
			Collection<EntityGroupInterface> collection = entityManager.getAllEntitiyGroups();
			dbFixesSQL = new FileWriter(new File("deDBFixes.sql"));
			for (EntityGroupInterface entityGroupInterface : collection)
			{
				String query = new String();
				for (EntityInterface entityInterface : entityGroupInterface.getEntityCollection())
				{

					for (AssociationInterface associationInterface : entityInterface
							.getAssociationCollection())
					{
						if (associationInterface.getIsCollection())
						{
							query = "update DYEXTN_ROLE set name='"
									+ DEConstants.COLLECTIONATTRIBUTEROLE
									+ entityInterface.getName() + "' where identifier="
									+ associationInterface.getSourceRole().getId() + ";\n";
							dbFixesSQL.write(query);
							Logger.out.info(query);
						}
					}
				}

			}
			writeModelFixexQuery(dbFixesSQL);

		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage());
		}
		finally
		{
			dbFixesSQL.flush();
			dbFixesSQL.close();
		}

	}

	/**
	 * These sqls are required for fixing the cacore issues. 
	 * With the existing names we were not able to generate the cacore.  
	 * @param dbFixesSQL
	 * @throws IOException
	 */
	private static void writeModelFixexQuery(FileWriter dbFixesSQL) throws IOException
	{
		dbFixesSQL
				.append("update dyextn_abstract_metadata set name = 'hcgValue' where name like 'hCGValue';\n");
		dbFixesSQL
				.append("update dyextn_abstract_metadata set name = 'hcgValueOther' where name like 'hCGValueOther';\n");

	}
}
