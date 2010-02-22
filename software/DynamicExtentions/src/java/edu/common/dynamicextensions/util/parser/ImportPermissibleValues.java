
package edu.common.dynamicextensions.util.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author kunal_kamble
 * This class the imports the permissible values from csv file into the database.
 *
 */
public class ImportPermissibleValues
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static CategoryCSVFileParser catCSVFileParser;

	private static final String ENTITY_GROUP = "Entity_Group";

	private static final EntityCache ENTITY_CACHE = EntityCache.getInstance();

	/**
	 *
	 * @param filePath
	 * @param stinger the stinger validator object which is used to validate the pv strings.
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 */
	public ImportPermissibleValues(String filePath, String baseDir, Stinger stinger)
			throws DynamicExtensionsSystemException, FileNotFoundException
	{
		catCSVFileParser = new CategoryCSVFileParser(filePath, baseDir, stinger);
	}

	/**
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	public void importValues() throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, ParseException
	{
		CategoryHelperInterface categoryHelper = new CategoryHelper();

		try
		{
			Map<String, Collection<SemanticPropertyInterface>> finalPVs = new LinkedHashMap<String, Collection<SemanticPropertyInterface>>();
			List<String> presentPVs = new ArrayList<String>();
			while (catCSVFileParser.readNext())
			{
				EntityManagerInterface entityManager = EntityManager.getInstance();

				// First line in the category file is Category_Definition.
				if (ENTITY_GROUP.equals(catCSVFileParser.readLine()[0]))
				{
					continue;
				}

				// Fetch the entity group id.
				Long entityGroupId = entityManager.getEntityGroupId(catCSVFileParser
						.getEntityGroupName());
				CategoryValidator.checkForNullRefernce(entityGroupId, " ERROR AT LINE:"
						+ catCSVFileParser.getLineNumber() + " ENTITY GROUP WITH NAME "
						+ catCSVFileParser.getEntityGroupName() + " DOES NOT");

				catCSVFileParser.getCategoryValidator().setEntityGroupId(entityGroupId);

				EntityGroupInterface entityGroup = ENTITY_CACHE.getEntityGroupById(entityGroupId);

				while (catCSVFileParser.readNext())
				{
					boolean isOverridePVs = catCSVFileParser.isOverridePermissibleValues();
					if (ENTITY_GROUP.equals(catCSVFileParser.readLine()[0]))
					{
						break;
					}
					String entityName = catCSVFileParser.getEntityName();
					EntityInterface entity = entityGroup.getEntityByName(entityName);

					Long entityId = entityManager.getEntityId(entityName, entityGroupId);
					CategoryValidator.checkForNullRefernce(entityId, " ERROR AT LINE:"
							+ catCSVFileParser.getLineNumber() + " ENTITY WITH NAME " + entityName
							+ " DOES NOT EXIST");

					String attributeName = catCSVFileParser.getAttributeName();

					Map<String, Collection<SemanticPropertyInterface>> pvList = catCSVFileParser
							.getPermissibleValues();

					Long attributeId = entityManager.getAttributeId(attributeName, entityId);
					CategoryValidator.checkForNullRefernce(attributeId, " ERROR AT LINE:"
							+ catCSVFileParser.getLineNumber() + " ATTRIBUTE WITH NAME "
							+ attributeName + " DOES NOT EXIST");
					// Bug # 10432,10382
					// If this attribute is of type association (as in case of multi select),
					// it is required to fetch association's target entity's attribute id.
					Long assoAttrId = entityManager.getAssociationAttributeId(attributeId);
					if (assoAttrId != null)
					{
						attributeId = assoAttrId;
					}

					AttributeTypeInformationInterface attrTypeInfo = entityManager
							.getAttributeTypeInformation(attributeId);

					UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attrTypeInfo
							.getDataElement();

					if (userDefinedDE == null)
					{
						userDefinedDE = DomainObjectFactory.getInstance().createUserDefinedDE();
						attrTypeInfo.setDataElement(userDefinedDE);
						finalPVs = pvList;
					}
					else
					{
						if (isOverridePVs)
						{
							userDefinedDE.clearPermissibleValues();
						}

						for (PermissibleValueInterface permissibleValue : userDefinedDE
								.getPermissibleValueCollection())
						{
							presentPVs.add(permissibleValue.getValueAsObject().toString());
						}

						Set<String> pvListKeySet = pvList.keySet();
						for (String string : pvListKeySet)
						{
							if (!presentPVs.contains(string))
							{
								finalPVs.put(string, pvList.get(string));
							}
						}
						presentPVs.clear();
					}

					List<PermissibleValueInterface> permValues = categoryHelper
							.getPermissibleValueList(attrTypeInfo, finalPVs);

					userDefinedDE.addAllPermissibleValues(permValues);

					entityManager.updateAttributeTypeInfo(attrTypeInfo);
					ENTITY_CACHE.updatePermissibleValues(entity, attributeId, attrTypeInfo);
					finalPVs.clear();
				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("FATAL ERROR AT LINE:"
					+ catCSVFileParser.getLineNumber(), e);
		}
		catch (DynamicExtensionsSystemException dse)
		{
			throw new DynamicExtensionsSystemException("", dse);
		}

		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(
					"Error encountered while importing Permissible Values", e);

		}
		finally
		{
			catCSVFileParser.closeResources();
		}
	}

	/**
	 * @param args command line arguments
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void main(String args[]) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception("Please Specify the path for .csv file");
			}

			String filePath = args[0];
			Logger.out.info("The .csv file path is:" + filePath);

			ImportPermissibleValues importPVs = new ImportPermissibleValues(filePath, "", null);
			importPVs.importValues();

			Logger.out.info(" ");
			Logger.out.info("---------------------------------------");
			Logger.out.info("Added permissible values successfully!!");
			Logger.out.info("---------------------------------------");
			Logger.out.info(" ");
		}
		catch (Exception ex)
		{
			Logger.out.info("Exception: ", ex);
			throw new DynamicExtensionsSystemException("", ex);
		}
	}

}