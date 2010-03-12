
package edu.common.dynamicextensions.entitymanager;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * This class provides the methods that builds the queries that are specific to ORACLE database
 *
 * @author Rahul Ner
 */
public class DynamicExtensionPostGreSQLQueryBuilder extends DynamicExtensionBaseQueryBuilder
{

	/**
	 * This method returns the query for the attribute to modify its data type.
	 * @param attribute
	 * @param savedAttribute
	 * @param modifyAttributeRollbackQuery
	 * @param tableName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected List getAttributeDataTypeChangedQuery(Attribute attribute, Attribute savedAttribute,
			List modifyAttrRollbackQueryList) throws DynamicExtensionsSystemException
	{
		String tableName = attribute.getEntity().getTableProperties().getName();
		String columnName = attribute.getColumnProperties().getName();

		String type = "TYPE";

		StringBuffer modifyAttributeQuery = new StringBuffer();
		modifyAttributeQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName);
		modifyAttributeQuery.append(WHITESPACE).append(ALTER_KEYWORD).append(WHITESPACE);
		modifyAttributeQuery.append(getQueryPartForAttribute(attribute, type, false));

		StringBuffer modifyAttributeRollbackQuery = new StringBuffer();
		modifyAttributeRollbackQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName)
				.append(WHITESPACE);
		modifyAttributeRollbackQuery.append(ALTER_KEYWORD).append(WHITESPACE);
		modifyAttributeRollbackQuery.append(getQueryPartForAttribute(savedAttribute, type, false));

		String nullPartQuery = "";
		String nullPartRollbackQuery = "";

		if (attribute.getIsNullable() && !savedAttribute.getIsNullable())
		{
			nullPartQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + ALTER_KEYWORD
					+ WHITESPACE + columnName + WHITESPACE + DROP_KEYWORD + WHITESPACE
					+ NOT_KEYWORD + WHITESPACE + NULL_KEYWORD;
			nullPartRollbackQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE
					+ ALTER_KEYWORD + WHITESPACE + columnName + WHITESPACE + SET_KEYWORD
					+ WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD;
		}
		else if (!attribute.getIsNullable() && savedAttribute.getIsNullable())
		{
			nullPartQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + ALTER_KEYWORD
					+ WHITESPACE + columnName + WHITESPACE + SET_KEYWORD + WHITESPACE + NOT_KEYWORD
					+ WHITESPACE + NULL_KEYWORD;
			nullPartRollbackQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE
					+ ALTER_KEYWORD + WHITESPACE + columnName + WHITESPACE + DROP_KEYWORD
					+ WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD;
		}

		List modifyAttributeQueryList = new ArrayList();
		modifyAttributeQueryList.add(modifyAttributeQuery.toString());
		modifyAttributeQueryList.add(nullPartQuery);

		modifyAttrRollbackQueryList.add(modifyAttributeRollbackQuery.toString());
		modifyAttrRollbackQueryList.add(nullPartRollbackQuery);

		return modifyAttributeQueryList;
	}
}
