
package edu.common.dynamicextensions.entitymanager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * This class provides the methods that builds the queries that are specific to ORACLE database 
 * 
 * @author Rahul Ner
 */

public class DynamicExtensionMySQLQueryBuilder extends DynamicExtensionBaseQueryBuilder
{

	/**
	 * This method returns the query to add foreign key constraint in the given child entity
	 * that refers to identifier column of the parent.
	 * @param entity
	 * @return
	 */
	/*
	 * changed by: pavan
	 * Method signature is different than the DynamicExtensionsBaseQueryBuilder
	 * thus it is changed to match. Thus it can be overridden correctly
	 * 
	 */

	protected String getForeignKeyRemoveConstraintQueryForInheritance(
			AbstractEntityInterface entity, AbstractEntityInterface parentEntity)
	{
		StringBuffer foreignKeyConstraint = new StringBuffer();

		String tableName = entity.getTableProperties().getName();
		String foreignConstraintName = entity.getConstraintProperties().getConstraintName() + UNDERSCORE
				+ parentEntity.getConstraintProperties().getConstraintName();

		foreignKeyConstraint.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(DROP_KEYWORD).append(WHITESPACE).append(FOREIGN_KEY_KEYWORD).append(
						WHITESPACE).append(foreignConstraintName);

		return foreignKeyConstraint.toString();
	}
	
	/**
	 * Converts value to Object data type for MySql database 
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
