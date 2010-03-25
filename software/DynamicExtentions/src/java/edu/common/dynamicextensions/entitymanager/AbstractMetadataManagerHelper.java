
package edu.common.dynamicextensions.entitymanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Query;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.HQLPlaceHolderObject;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

/**
 * Helper class for AbstractMetadataManager.java class.
 *
 * @author Gaurav Sawant
 */
public final class AbstractMetadataManagerHelper
		implements
			EntityManagerExceptionConstantsInterface
{

	/**
	 * The single instance.
	 */
	private static AbstractMetadataManagerHelper helper = null;

	/**
	 * Instantiates a new abstract metadata manager helper.
	 */
	private AbstractMetadataManagerHelper()
	{
		// private constructor.
	}

	/**
	 * Gets the single instance of AbstractMetadataManagerHelper.
	 *
	 * @return single instance of AbstractMetadataManagerHelper.
	 */
	public static synchronized AbstractMetadataManagerHelper getInstance()
	{
		if (helper == null)
		{
			helper = new AbstractMetadataManagerHelper();
		}
		return helper;
	}

	/**
	 * Handle datatype.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataType
	 *            the data type
	 * @param klass
	 *            the klass
	 * @param returnedObj
	 *            the returned obj
	 * @param value
	 *            the value
	 * @param attrName
	 *            the attr name
	 *
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchMethodException
	 *             the no such method exception

	 */
	public void handleDatatype(AbstractAttribute attribute, String dataType, Class klass,
			Object returnedObj, Object value, String attrName) throws ClassNotFoundException,
			ParseException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException
	{
		Object dataValue = null;
		String dataTypeClassName;
		if ("Long".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = Long.valueOf(value.toString());
			}
			dataTypeClassName = Long.class.getName();
		}
		else if ("Float".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = new Float(value.toString());
			}
			dataTypeClassName = Float.class.getName();

		}
		else if ("Double".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = new Double(value.toString());
			}
			dataTypeClassName = Double.class.getName();
		}
		else if ("Short".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = Short.valueOf(value.toString());
			}
			dataTypeClassName = Short.class.getName();
		}
		else if ("Integer".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = Integer.valueOf(value.toString());
			}
			dataTypeClassName = Integer.class.getName();
		}
		else if ("Boolean".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				String boolVal = ("1".equals(value) || DEConstants.TRUE.equalsIgnoreCase(value
						.toString())) ? DEConstants.TRUE : DEConstants.FALSE;
				dataValue = Boolean.valueOf(boolVal);
			}
			dataTypeClassName = Boolean.class.getName();
		}
		else if ("Date".equals(dataType))
		{
			if (isNotEmptyString(value))
			{
				dataValue = getDateValue(attribute, value);
			}
			dataTypeClassName = Date.class.getName();
		}
		else
		{
			if (isNotEmptyString(value))
			{
				dataValue = value.toString();
			}
			dataTypeClassName = String.class.getName();
		}
		if (!("ByteArray".equals(dataType) || "File".equals(dataType)))
		{
			invokeSetterMethod(klass, attrName, Class.forName(dataTypeClassName), returnedObj,
					dataValue);
		}
	}

	/**
	 * Gets the date value.
	 *
	 * @param attribute
	 *            the attribute
	 * @param value
	 *            the value
	 *
	 * @return the date value
	 *
	 * @throws ParseException
	 *             the parse exception
	 */
	private Object getDateValue(final AbstractAttribute attribute, final Object value)
			throws ParseException
	{
		Object dataValue;
		Attribute attr = (Attribute) attribute;
		DateAttributeTypeInformation dateAttributeTypeInf = (DateAttributeTypeInformation) attr
				.getAttributeTypeInformation();

		String format = DynamicExtensionsUtility.getDateFormat(dateAttributeTypeInf.getFormat());

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
		dataValue = formatter.parse(value.toString());
		return dataValue;
	}

	/**
	 * Invoke setter method.
	 *
	 * @param klass
	 *            the klass
	 * @param property
	 *            the property
	 * @param argumentType
	 *            the argument type
	 * @param invokeOnObject
	 *            the invoke on object
	 * @param argument
	 *            the argument
	 *
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
	public void invokeSetterMethod(Class klass, String property, Class argumentType,
			Object invokeOnObject, Object argument) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		Method setter = klass.getMethod("set" + property, argumentType);
		setter.invoke(invokeOnObject, argument);
	}

	/**
	  * Sets the parameters on query.
	  *
	  * @param query
	  *            the query.
	  * @param counter
	  *            the counter.
	  * @param plcHolderObj
	  *            the plc holder obj.
	  * @param objectType
	  *            the object type.
	  */
	protected void setParametersOnQuery(final Query query, final int counter,
			final HQLPlaceHolderObject plcHolderObj, final String objectType)
	{
		if ("string".equals(objectType))
		{
			query.setString(counter, plcHolderObj.getValue().toString());
		}
		else if ("integer".equals(objectType))
		{
			query.setInteger(counter, Integer.parseInt(plcHolderObj.getValue().toString()));
		}
		else if ("long".equals(objectType))
		{
			query.setLong(counter, Long.parseLong(plcHolderObj.getValue().toString()));
		}
		else if ("boolean".equals(objectType))
		{
			query.setBoolean(counter, Boolean.parseBoolean(plcHolderObj.getValue().toString()));
		}
	}

	/**
	 * Rollback dao.
	 *
	 * @param dao
	 *            the dao.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception.
	 */
	protected void rollbackDao(final DAO dao) throws DynamicExtensionsSystemException
	{
		if (dao != null)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException excep)
			{
				throw new DynamicExtensionsSystemException("Not able to rollback the transaction.",
						excep);
			}
		}
	}

	/**
	 * Checks if the given string object is not null and not empty string.
	 *
	 * @param value
	 *            the object name.
	 *
	 * @return true, if checks if is not empty string.
	 */
	protected boolean isNotEmptyString(final Object value)
	{
		return value != null && !"".equals(value);
	}

	/**
	 * Gets the new entity record.
	 *
	 * @param identifier
	 *            the identifier.
	 *
	 * @return the new entity record.
	 */
	public EntityRecord getNewEntityRecord(final String identifier)
	{
		return new EntityRecord(Long.valueOf(identifier));
	}

}
