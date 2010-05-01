/*
 * Created on Aug 31, 2007
 * @author
 *
 */

package edu.common.dynamicextensions.xmi.exporter;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.entitymanager.DataTypeFactory;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.exception.DataTypeFactoryInitializationException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author preeti_lodha
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public enum DatatypeMappings {

	STRING(EntityManagerConstantsInterface.STRING_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.String";
		}

	},
	INTEGER(EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Integer";
		}
	},
	BOOLEAN(EntityManagerConstantsInterface.BOOLEAN_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Boolean";
		}
	},
	LONG(EntityManagerConstantsInterface.LONG_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Long";
		}
	},
	SHORT(EntityManagerConstantsInterface.SHORT_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Short";
		}
	},
	DATE(EntityManagerConstantsInterface.DATE_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.util.Date";
		}
	},
	FLOAT(EntityManagerConstantsInterface.FLOAT_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Float";
		}
	},
	DOUBLE(EntityManagerConstantsInterface.DOUBLE_ATTRIBUTE_TYPE) {

		public String getJavaClassMapping()
		{
			return "java.lang.Double";
		}
	};

	String value;

	DatatypeMappings(String value)
	{
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static DatatypeMappings get(String value)
	{
		DatatypeMappings[] datatypeMappings = DatatypeMappings.values();

		for (DatatypeMappings datatypeMapping : datatypeMappings)
		{
			if (datatypeMapping.getValue().equalsIgnoreCase(value))
			{
				return datatypeMapping;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getJavaClassMapping()
	{
		Logger.out.info("Java Mapping for datatype not found");
		return null;
	}

	/**
	 * @return
	 * @throws DataTypeFactoryInitializationException
	 */
	public String getSQLClassMapping() throws DataTypeFactoryInitializationException
	{
		return DataTypeFactory.getInstance().getDatabaseDataType(value);
	}

	/**
	 * @param args
	 * @throws DataTypeFactoryInitializationException
	 */
	public static void main(String[] args) throws DataTypeFactoryInitializationException
	{
		DomainObjectFactory.getInstance().createFloatAttribute();
//		Variables.databasenames = "MYSQL";
		//System.out.println(d.getJavaClassMapping());
		//System.out.println(d.getSQLClassMapping());
	}
}
