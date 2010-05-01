package edu.common.dynamicextensions.dao.impl;

import edu.common.dynamicextensions.entitymanager.DynamicExtensionBaseQueryBuilder;



/**
 * @author ravi_kumar
 *
 */
public class DynamicExtensionDBGroup
{
	private DynamicExtensionBaseQueryBuilder queryBuilder;
	private IDEDBUtility dbUtility;
	private String dataTypeMappingFile;

	/**
	 * @param queryBuilder
	 * @param dbUtility
	 * @param dataTypeMappingFile
	 */
	public DynamicExtensionDBGroup(DynamicExtensionBaseQueryBuilder queryBuilder,
			IDEDBUtility dbUtility,String dataTypeMappingFile)
	{
		this.queryBuilder=queryBuilder;
		this.dbUtility=dbUtility;
		this.dataTypeMappingFile=dataTypeMappingFile;
	}
	/**
	 * @return the queryBuilder
	 */
	public DynamicExtensionBaseQueryBuilder getQueryBuilder()
	{
		return queryBuilder;
	}

	/**
	 * @param queryBuilder the queryBuilder to set
	 */
	public void setQueryBuilder(DynamicExtensionBaseQueryBuilder queryBuilder)
	{
		this.queryBuilder = queryBuilder;
	}

	/**
	 * @return the dbUtility
	 */
	public IDEDBUtility getDbUtility()
	{
		return dbUtility;
	}

	/**
	 * @param dbUtility the dbUtility to set
	 */
	public void setDbUtility(IDEDBUtility dbUtility)
	{
		this.dbUtility = dbUtility;
	}

	/**
	 * @return the dataTypeMappingFile
	 */
	public String getDataTypeMappingFile()
	{
		return dataTypeMappingFile;
	}

	/**
	 * @param dataTypeMappingFile the dataTypeMappingFile to set
	 */
	public void setDataTypeMappingFile(String dataTypeMappingFile)
	{
		this.dataTypeMappingFile = dataTypeMappingFile;
	}

}
