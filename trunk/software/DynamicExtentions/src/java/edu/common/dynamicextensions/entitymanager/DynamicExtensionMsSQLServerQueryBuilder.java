
package edu.common.dynamicextensions.entitymanager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public class DynamicExtensionMsSQLServerQueryBuilder extends DynamicExtensionBaseQueryBuilder
{

	private String nullQryKewrd = "";
	private String nullQryRlbkKewrd = "";

	/**
	 * This method builds the query part for the newly added attribute.
	 * 
	 * @param attribute
	 *            Newly added attribute in the entity.
	 * @param attrRlbkQries
	 *            This list is updated with the roll back queries for the actual
	 *            queries.
	 * @return String The actual query part for the new attribute.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected String processAddAttribute(Attribute attribute, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String columnName = attribute.getColumnProperties().getName();
		String tableName = attribute.getEntity().getTableProperties().getName();
		String type = "";
		StringBuffer newAttrQuery = new StringBuffer("");
		newAttrQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(ADD_KEYWORD).append(WHITESPACE).append(
						getQueryPartForAttribute(attribute, type, true));

		StringBuffer newAttrRlbkQry = new StringBuffer("");
		newAttrRlbkQry.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(DROP_KEYWORD).append(WHITESPACE).append(COLUMN_KEYWORD).append(WHITESPACE)
				.append(columnName);

		if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
		{
			newAttrQuery.append(COMMA).append(extraColumnQueryStringForFileAttribute(attribute));
			newAttrRlbkQry.append(COMMA).append(
					dropExtraColumnQueryStringForFileAttribute(attribute));
		}

		attrRlbkQries.add(newAttrRlbkQry.toString());

		return newAttrQuery.toString();
	}

	/**
	 * This method generates the alter table query to drop columns.
	 * 
	 * @param tableName
	 * @param columnNames
	 * @return String altered query
	 */
	protected String getDropColumnQuery(String tableName, List<String> columnNames)
	{
		StringBuffer alterQuery = new StringBuffer();

		alterQuery.append(ALTER_TABLE);
		alterQuery.append(tableName);
		alterQuery.append(WHITESPACE);
		alterQuery.append(DROP_KEYWORD);
		alterQuery.append(COLUMN_KEYWORD);

		for (int i = 0; i < columnNames.size(); i++)
		{
			alterQuery.append(columnNames.get(i));
			if (i != columnNames.size() - 1)
			{
				alterQuery.append(COMMA);
			}
		}
		return alterQuery.toString();
	}

	/**
	 * This method returns the query for the attribute to modify its data type.
	 * 
	 * @param attribute
	 * @param savedAttr
	 * @param modifyAttributeRollbackQuery
	 * @param tableName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getAttributeDataTypeChangedQuery(Attribute attribute,
			Attribute savedAttr, List<String> mdfyAttrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		String tableName = attribute.getEntity().getTableProperties().getName();
		String type = "";

		StringBuffer mdfyAttrRlbkQry = new StringBuffer("");

		StringBuffer mdfyAttrQuery = new StringBuffer("");
		mdfyAttrQuery.append(ALTER_TABLE).append(tableName).append(ADD_KEYWORD).append(
				getQueryPartForAttribute(attribute, type, false));

		mdfyAttrRlbkQry.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(ALTER_COLUMN_KEYWORD).append(WHITESPACE).append(
						getQueryPartForAttribute(savedAttr, type, false));

		List<String> mdfyAttrQries = new ArrayList<String>();
		mdfyAttrQries.add(ALTER_TABLE + tableName + DROP_KEYWORD + COLUMN_KEYWORD
				+ savedAttr.getColumnProperties().getName());

		setValueForKeyWord(attribute, savedAttr);

		// Added by: Kunal : Two more extra columns file name and content type
		// need to be added to the table.
		if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
		{
			mdfyAttrQuery.append(COMMA).append(extraColumnQueryStringForFileAttribute(attribute));
			mdfyAttrRlbkQry.append(COMMA).append(
					dropExtraColumnQueryStringForFileAttribute(attribute));
		}

		mdfyAttrQuery.append(nullQryKewrd);
		mdfyAttrRlbkQry.append(nullQryRlbkKewrd);

		mdfyAttrRlbkQries.add(mdfyAttrRlbkQry.toString());

		mdfyAttrQries.add(mdfyAttrQuery.toString());

		return mdfyAttrQries;
	}

	/**
	 * This method sets Value For KeyWord like nullQryKewrd,nullQryRlbkKewrd.
	 * @param attribute
	 * @param savedAttr
	 */
	private void setValueForKeyWord(Attribute attribute, Attribute savedAttr)
	{
		if (attribute.getIsNullable() && !savedAttr.getIsNullable())
		{
			nullQryKewrd = WHITESPACE + NULL_KEYWORD + WHITESPACE;
			nullQryRlbkKewrd = WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD + WHITESPACE;
		}
		else if (!attribute.getIsNullable() && savedAttr.getIsNullable())
		{
			nullQryKewrd = WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD + WHITESPACE;
			nullQryRlbkKewrd = WHITESPACE + NULL_KEYWORD + WHITESPACE;

		}
	}

	/**
	 * Converts value to Object data type for MsSqlServer database
	 * 
	 * @param valueObj
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected Object convertValueToObject(Object valueObj) throws DynamicExtensionsSystemException
	{
		Object value = "";

		ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) valueObj);
		try
		{
			value = new ObjectInputStream(bais).readObject();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error while converting the data", e);
		}
		catch (ClassNotFoundException e)
		{
			throw new DynamicExtensionsSystemException("Error while converting the data", e);
		}
		return value;
	}

}
