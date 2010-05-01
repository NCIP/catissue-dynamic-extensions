
package edu.common.dynamicextensions.entitymanager;

public class DataTypeInformation
{

	public String name;

	public String digitsBeforeDecimal;

	public String digitsAfterDecimal;

	public String databaseDataType;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the digitsBeforeDecimal
	 */
	public String getDigitsBeforeDecimal()
	{
		return digitsBeforeDecimal;
	}

	/**
	 * @param digitsBeforeDecimal the digitsBeforeDecimal to set
	 */
	public void setDigitsBeforeDecimal(String digitsBeforeDecimal)
	{
		this.digitsBeforeDecimal = digitsBeforeDecimal;
	}

	/**
	 * @return the digitsAfterDecimal
	 */
	public String getDigitsAfterDecimal()
	{
		return digitsAfterDecimal;
	}

	/**
	 * @param digitsAfterDecimal the digitsAfterDecimal to set
	 */
	public void setDigitsAfterDecimal(String digitsAfterDecimal)
	{
		this.digitsAfterDecimal = digitsAfterDecimal;
	}

	/**
	 * @return the databaseDataType
	 */
	public String getDatabaseDataType()
	{
		return databaseDataType;
	}

	/**
	 * @param databaseDataType the databaseDataType to set
	 */
	public void setDatabaseDataType(String databaseDataType)
	{
		this.databaseDataType = databaseDataType;
	}

}
