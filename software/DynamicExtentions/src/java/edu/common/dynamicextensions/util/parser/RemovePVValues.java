/**
 *
 */

package edu.common.dynamicextensions.util.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class RemovePVValues
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * main method
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{

		RemovePVValues obj = new RemovePVValues();
		obj.remove(args);
	}

	/**
	 * @param args
	 */
	private void remove(String[] args)
	{
		if (args != null)
		{
			String entitygroupName = args[0];
			String entityName = args[1];
			String attributeName = args[2];
			String filePath = args[3];
			Logger.out.info("Filepath " + filePath);
			try
			{
				removePV(entitygroupName, entityName, attributeName, filePath);
			}
			catch (Exception ex)
			{
				Logger.out.info("Exception: " + ex.getMessage());
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * @param entitygroupName
	 * @param entityName
	 * @param attributeName
	 * @param filePath
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 */
	private void removePV(String entitygroupName, String entityName, String attributeName,
			String filePath) throws DynamicExtensionsSystemException, IOException
	{

		EntityManagerInterface entityManager = EntityManager.getInstance();
		Long entityGroupId = entityManager.getEntityGroupId(entitygroupName);
		Long entityId = entityManager.getEntityId(entityName, entityGroupId);
		Long attributeId = entityManager.getAttributeId(attributeName, entityId);
		Long dataElementId = entityManager.getAttributeTypeInformation(attributeId)
				.getDataElement().getId();
		AttributeTypeInformationInterface attrTypeInfo = entityManager
				.getAttributeTypeInformation(attributeId);
		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attrTypeInfo
				.getDataElement();
		ArrayList<String> sqlList = new ArrayList<String>();
		ArrayList<String> delPVList = new ArrayList<String>();
		if (!filePath.equals("none"))
		{
			validateFileExist(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath)));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				if (line != null && line.trim().length() != 0)//skip the line if it is blank
				{
					delPVList.add(line);
				}
			}
		}

		Logger.out.info("The total no. of PVs to be deleted :" + delPVList.size());
		Collection<PermissibleValueInterface> prevPVValues = userDefinedDE
				.getPermissibleValueCollection();

		String strDELSTRCONPV = "delete from DYEXTN_STRING_CONCEPT_VALUE where identifier=";
		String strDELPV = "delete from DYEXTN_PERMISSIBLE_VALUE  where identifier=";
		String strUSRDEFDEREL = "delete from DYEXTN_USERDEF_DE_VALUE_REL where PERMISSIBLE_VALUE_ID=";
		String strUSRDEFDE = "delete from DYEXTN_USERDEFINED_DE where identifier=";
		String strDATAELE = "delete from DYEXTN_DATA_ELEMENT where identifier=";

		for (PermissibleValueInterface permissibleValue : prevPVValues)
		{
			for (String delPVValue : delPVList)
			{
				if (delPVValue.equalsIgnoreCase(permissibleValue.getValueAsObject().toString()))
				{
					//Add to list only PVs which are part of delPVValue i.e. specified in file
					sqlList.add(strDELSTRCONPV + permissibleValue.getId());
					sqlList.add(strDELPV + permissibleValue.getId());
					String stsql = strUSRDEFDEREL + permissibleValue.getId()
							+ " and USER_DEF_DE_ID= " + userDefinedDE.getId();
					sqlList.add(stsql);
				}

			}
			if (filePath.equals("none"))
			{
				//Remove all PV values
				sqlList.add(strDELSTRCONPV + permissibleValue.getId());
				sqlList.add(strDELPV + permissibleValue.getId());
				String stsql = strUSRDEFDEREL + permissibleValue.getId() + " and USER_DEF_DE_ID= "
						+ userDefinedDE.getId();
				sqlList.add(stsql);
			}

		}
		if (filePath.equals("none"))
		{
			//Also remove UserdefinedDE and data element
			sqlList.add(strUSRDEFDE + userDefinedDE.getId());
			sqlList.add(strDATAELE + userDefinedDE.getId());

		}
		Logger.out.info("Removing PV values from" + entitygroupName + " entityGroupId: "
				+ entityGroupId);
		Logger.out.info("entityName: " + entityName + " entityId: " + entityId);
		Logger.out.info("attributeName: " + attributeName + " attributeId: " + attributeId);
		Logger.out.info("dataElementId: " + dataElementId);
		executeSQL(sqlList);
		Logger.out.info("Removed PVs successfully");

	}

	/**
	 * @param sqlList
	 * @throws DAOException
	 */
	private void executeSQL(ArrayList<String> sqlList) throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcdao = null;
		try
		{
			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			for (String str : sqlList)
			{
				jdbcdao.executeUpdate(str);
				Logger.out.info("-" + str);

			}
			jdbcdao.commit();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error occured during DB operation.", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcdao);
		}
	}

	/**
	 * @param filePath
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateFileExist(String filePath) throws DynamicExtensionsSystemException
	{
		if (filePath != null)
		{
			Logger.out.info("file :" + filePath);
			File objFile = new File(filePath);
			if (!objFile.exists())
			{
				throw new DynamicExtensionsSystemException(
						"Please verify that form definition file exist at path: " + filePath);
			}
		}
	}
}