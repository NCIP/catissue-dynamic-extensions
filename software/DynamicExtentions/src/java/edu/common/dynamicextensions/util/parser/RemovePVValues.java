/**
 *
 */

package edu.common.dynamicextensions.util.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * The Class RemovePVValues.
 *
 * @author falguni_sachde
 */
public class RemovePVValues
{

    /**
     * The Constant STR_DELSTRCONPV.
     */
    private static final String STR_DELSTRCONPV = "delete from DYEXTN_STRING_CONCEPT_VALUE" +
    		" where identifier=";

    /**
     * The Constant STR_DELPV.
     */
    private static final String STR_DELPV = "delete from DYEXTN_PERMISSIBLE_VALUE  where identifier=";

    /**
     * The Constant STR_USRDEFDEREL.
     */
    private static final String STR_USRDEFDEREL = "delete from DYEXTN_USERDEF_DE_VALUE_REL " +
    		"where PERMISSIBLE_VALUE_ID=";

    /**
     * The Constant STR_USRDEFDE.
     */
    private static final String STR_USRDEFDE = "delete from DYEXTN_USERDEFINED_DE where identifier=";

    /**
     * The Constant STR_DATAELE.
     */
    private static final String STR_DATAELE = "delete from DYEXTN_DATA_ELEMENT where identifier=";

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
     * main method.
     *
     * @param args
     *            command line arguments
     */
	public static void main(String[] args)
	{

		RemovePVValues obj = new RemovePVValues();
		obj.remove(args);
	}

	/**
     * Removes the.
     *
     * @param args
     *            the args
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
     * Removes the pv.
     *
     * @param entitygroupName
     *            the entitygroup name
     * @param entityName
     *            the entity name
     * @param attributeName
     *            the attribute name
     * @param filePath
     *            the file path
     * @throws DynamicExtensionsSystemException
     *             the dynamic extensions system exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
	private void removePV(String entitygroupName, String entityName, String attributeName,
			String filePath) throws DynamicExtensionsSystemException, IOException
    {

        EntityManagerInterface entityManager = EntityManager.getInstance();
        Long entityGroupId = entityManager.getEntityGroupId(entitygroupName);
        Long entityId = entityManager.getEntityId(entityName, entityGroupId);
        Long attributeId = entityManager
                .getAttributeId(attributeName, entityId);
        Long dataElementId = entityManager.getAttributeTypeInformation(
                attributeId).getDataElement().getId();
        AttributeTypeInformationInterface attrTypeInfo = entityManager
                .getAttributeTypeInformation(attributeId);
        UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attrTypeInfo
                .getDataElement();
        List<String> delPVList = getDeletePVList(filePath);

        Logger.out.info("The total no. of PVs to be deleted :"
                + delPVList.size());
        Collection<PermissibleValueInterface> prevPVValues = userDefinedDE
                .getPermissibleValueCollection();

        List<String> sqlList = addToSQLList(filePath, userDefinedDE,
                delPVList, prevPVValues);
        if ("none".equals(filePath))
        {
            // Also remove UserdefinedDE and data element
            sqlList.add(STR_USRDEFDE + userDefinedDE.getId());
            sqlList.add(STR_DATAELE + userDefinedDE.getId());

        }
        Logger.out.info("Removing PV values from" + entitygroupName
                + " entityGroupId: " + entityGroupId);
        Logger.out.info("entityName: " + entityName + " entityId: " + entityId);
        Logger.out.info("attributeName: " + attributeName + " attributeId: "
                + attributeId);
        Logger.out.info("dataElementId: " + dataElementId);
        executeSQL(sqlList);
        Logger.out.info("Removed PVs successfully");

    }

    /**
     * Adds the to sql list.
     *
     * @param filePath
     *            the file path
     * @param userDefinedDE
     *            the user defined de
     * @param delPVList
     *            the del pv list
     * @param prevPVValues
     *            the prev pv values
     * @return the list
     */
    private List<String> addToSQLList(String filePath,
            UserDefinedDEInterface userDefinedDE, List<String> delPVList,
            Collection<PermissibleValueInterface> prevPVValues)
    {
        List<String> sqlList = new ArrayList<String>();
		for (PermissibleValueInterface permissibleValue : prevPVValues)
		{
			for (String delPVValue : delPVList)
			{
				if (delPVValue.equalsIgnoreCase(permissibleValue.getValueAsObject().toString()))
				{
					//Add to list only PVs which are part of delPVValue i.e. specified in file
					sqlList.add(STR_DELSTRCONPV + permissibleValue.getId());
					sqlList.add(STR_DELPV + permissibleValue.getId());
					String stsql = STR_USRDEFDEREL + permissibleValue.getId()
							+ " and USER_DEF_DE_ID= " + userDefinedDE.getId();
					sqlList.add(stsql);
				}

			}
			if ("none".equals(filePath))
			{
				//Remove all PV values
				sqlList.add(STR_DELSTRCONPV + permissibleValue.getId());
				sqlList.add(STR_DELPV + permissibleValue.getId());
				String stsql = STR_USRDEFDEREL + permissibleValue.getId() + " and USER_DEF_DE_ID= "
						+ userDefinedDE.getId();
				sqlList.add(stsql);
			}

		}
        return sqlList;
    }

    /**
     * Gets the delete pv list.
     *
     * @param filePath
     *            the file path
     * @return the delete pv list
     * @throws DynamicExtensionsSystemException
     *             the dynamic extensions system exception
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private List<String> getDeletePVList(String filePath)
            throws DynamicExtensionsSystemException, FileNotFoundException,
            IOException
    {
        ArrayList<String> delPVList = new ArrayList<String>();
		if (!"none".equals(filePath))
		{
			validateFileExist(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
					filePath)));
			String line = null; // NOPMD by gaurav_sawant on 5/6/10 7:39 PM
			while ((line = reader.readLine()) != null) // NOPMD by gaurav_sawant on 5/6/10 7:39 PM
			{
			  //skip the line if it is blank
				if (line != null && line.trim().length() != 0)// NOPMD by gaurav_sawant on 5/6/10 7:40 PM
				{
					delPVList.add(line);
				}
			}
		}
        return delPVList;
    }

	/**
     * Execute sql.
     *
     * @param sqlList
     *            the sql list
     * @throws DynamicExtensionsSystemException
     *             the dynamic extensions system exception
     */
	private void executeSQL(List<String> sqlList) throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcdao = null; // NOPMD by gaurav_sawant on 5/6/10 7:39 PM
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
     * Validate file exist.
     *
     * @param filePath
     *            the file path
     * @throws DynamicExtensionsSystemException
     *             the dynamic extensions system exception
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
                        "Please verify that form definition file exist at path: "
                                + filePath);
			}
		}
	}
}
