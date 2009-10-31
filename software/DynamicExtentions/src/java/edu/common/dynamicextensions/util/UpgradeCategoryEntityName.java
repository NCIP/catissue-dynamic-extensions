/**
 *
 */

package edu.common.dynamicextensions.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;


/**
 * this class upgrades the category entity name to the newer names of format
 * format of <root_entity_name>[instance_Number]<entity_name>[instance_Number]
 * @author suhas_khot
 *
 */
public class UpgradeCategoryEntityName implements DynamicExtensionsQueryBuilderConstantsInterface
{

	/**
	 * @param args
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException,SQLException, DynamicExtensionsApplicationException
	{
		upgradeCategoryEntityNames();

	}

	/**
	 * this method upgrade the older category names to the newer names
	 * @throws DynamicExtensionsSystemException fails to retrieve objects
	 * @throws DAOException fails to execute query
	 * @throws SQLException fails to execute query
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void upgradeCategoryEntityNames() throws DynamicExtensionsSystemException,
			SQLException, DynamicExtensionsApplicationException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		Collection<Long> categoryEntityIdColl = entityManager.getAllCategoryEntityId();
		EntityManagerUtil entityManagerUtil = new EntityManagerUtil();
		if (categoryEntityIdColl != null && !categoryEntityIdColl.isEmpty())
		{
			for (Long categoryEntityId : categoryEntityIdColl)
			{
				StringBuffer query = new StringBuffer();
				query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER);
				query.append(WHITESPACE + FROM_KEYWORD + WHITESPACE
						+ DEConstants.PATH_TABLE_NAME + WHITESPACE);
				query.append(WHITESPACE + WHERE_KEYWORD + WHITESPACE
						+ DEConstants.CATEGORY_ENTITY_ID + EQUAL
						+ categoryEntityId);
				List<Long> pathIdColl = entityManagerUtil.getResultInList(query.toString());
				if (pathIdColl != null && !pathIdColl.isEmpty())
				{
					Long pathId = Long.valueOf(pathIdColl.get(0).toString());
					Collection<Long> pathAssociationRelationIds = entityManager
							.getPathAssociationRelationIds(pathId);
					String categoryEntityName = getCategoryEntityName(pathAssociationRelationIds);
					if (categoryEntityName != null && categoryEntityName.trim().length() > 0)
					{
						updateCategoryName(categoryEntityName, categoryEntityId);
					}
				}
				else
				{
					String categoryEntityName = entityManager
							.getCategoryEntityNameByCategoryEntityId(categoryEntityId);
					if (categoryEntityName != null && categoryEntityName.trim().length() > 0)
					{
						int numberOfOccurances = 0;
						char[] catEntityName = categoryEntityName.toCharArray();
						for(char character : catEntityName)
						{
							if((Character.toString(character)).trim().equalsIgnoreCase(DEConstants.CLOSING_SQUARE_BRACKET))
							{
								numberOfOccurances=numberOfOccurances+1;
							}
						}
						if (numberOfOccurances < DEConstants.TWO)
						{
							String newCategoryName = categoryEntityName + categoryEntityName;
							updateCategoryName(newCategoryName, categoryEntityId);
						}
					}
				}
			}
		}
	}

	/**
	 * this method updates the categoryEntity Name
	 * @param categoryEntityName name of category Entity
	 * @param categoryEntityId Identifier of category Entity
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void updateCategoryName(String categoryEntityName, Long categoryEntityId)
			throws DynamicExtensionsSystemException, SQLException
	{
		StringBuffer query = new StringBuffer();
		List<String> queryList = new ArrayList<String>();
		query.append(UPDATE_KEYWORD);
		query.append(WHITESPACE
				+ DEConstants.ABSTRACT_METADATA_TABLE_NAME);
		query.append(WHITESPACE + SET_KEYWORD + WHITESPACE
				+ DEConstants.NAME + EQUAL + "'"
				+ categoryEntityName + "'" + WHITESPACE);
		query.append(WHERE_KEYWORD + WHITESPACE + IDENTIFIER + EQUAL + categoryEntityId);
		queryList.add(query.toString());
		EntityManagerUtil.executeDML(queryList);
	}

	/**
	 * This method retrieves the category names from pathAssociationRelationIds
	 * @param pathAssociationRelationIds
	 * @return category entity name
	 * @throws DynamicExtensionsSystemException fails to gets records
	 */
	private static String getCategoryEntityName(Collection<Long> pathAssociationRelationIds)
			throws DynamicExtensionsSystemException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		StringBuffer categoryEntityName = new StringBuffer();
		if (pathAssociationRelationIds != null && !pathAssociationRelationIds.isEmpty())
		{
			for (Long pathAssociationRelationId : pathAssociationRelationIds)
			{
				Long associationId = entityManager
						.getAssociationIdFrmPathAssoRelationId(pathAssociationRelationId);
				String srcEntityName = entityManager
						.getSrcEntityNameFromAssociationId(associationId);
				String tgtEntityName = entityManager
						.getTgtEntityNameFromAssociationId(associationId);
				Long srcInstanceId = entityManager
						.getSrcInstanceIdFromAssociationRelationId(pathAssociationRelationId);
				Long tgtInstanceId = entityManager
						.getTgtInstanceIdFromAssociationRelationId(pathAssociationRelationId);
				if(categoryEntityName.toString().trim().length()>0 && categoryEntityName.toString().contains(srcEntityName))
				{
					categoryEntityName.append(tgtEntityName
							+ DEConstants.OPENING_SQUARE_BRACKET + tgtInstanceId.toString()
							+ DEConstants.CLOSING_SQUARE_BRACKET);
				}
				else
				{
					categoryEntityName.append(srcEntityName + DEConstants.OPENING_SQUARE_BRACKET
							+ srcInstanceId + DEConstants.CLOSING_SQUARE_BRACKET + tgtEntityName
							+ DEConstants.OPENING_SQUARE_BRACKET + tgtInstanceId.toString()
							+ DEConstants.CLOSING_SQUARE_BRACKET);
				}
			}
		}
		return categoryEntityName.toString();
	}
}
