/**
 *
 */

package edu.common.dynamicextensions.skiplogic;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.Hibernate;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author Gaurav_mehta
 *
 */
public class PrimitiveCondition implements Condition
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(PrimitiveCondition.class);

	/** The identifier. */
	private Long identifier;

	/** The category attribute identifier. */
	private CategoryAttributeInterface categoryAttribute;

	/** The relational operator. */
	private RelationalOperator relationalOperator;

	/** The object value. */
	private Blob objectValue;

	/** The action. */
	private Action action;

	/**
	 * Sets the identifier.
	 * @param identifier the new identifier
	 */
	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Gets the identifier.
	 * @return the identifier
	 */
	public Long getIdentifier()
	{
		return identifier;
	}

	/**
	* Check condition.
	* @param objectValueState the object value state
	* @return true, if check condition
	 * @throws DynamicExtensionsSystemException
	*/
	public boolean checkCondition(Map<BaseAbstractAttributeInterface, Object> objectValueState,
			ContainerInterface controllingContainer) throws DynamicExtensionsSystemException
	{
		Object categoryAttributeValue = getCategoryObjectValue(controllingContainer
				.getContainerValueMap());
		if (categoryAttributeValue == null)
		{
			categoryAttributeValue = getCategoryObjectValue(objectValueState);
		}
		Object definedConditionValue = getValue();
		if (definedConditionValue == null || categoryAttributeValue == null)
		{
			return false;
		}
		return getRelationalOperator().evaluateCondition(categoryAttributeValue,
				definedConditionValue);
	}

	/**
	 * Gets the category attribute identifier.
	 * @return the categoryAttributeIdentifier
	 */
	public CategoryAttributeInterface getCategoryAttribute()
	{
		return categoryAttribute;
	}

	/**
	 * Sets the category attribute identifier.
	 * @param categoryAttribute the categoryAttributeIdentifier to set
	 */
	public void setCategoryAttribute(CategoryAttributeInterface categoryAttribute)
	{
		this.categoryAttribute = categoryAttribute;
	}

	/**
	 * Gets the value.
	 * @return the value
	 * @throws DynamicExtensionsSystemException
	 */
	public Object getValue() throws DynamicExtensionsSystemException
	{
		ObjectInputStream ois = null;
		try
		{
			ois = new ObjectInputStream(getObjectValue().getBinaryStream());
			return ois.readObject();
		}
		catch (IOException e)
		{
			LOGGER.error("Error occured while reading Object value from file", e);
			throw new DynamicExtensionsSystemException(
					"Error occured while reading Object value from file", e);
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.error("Error occured while reading Object value from file", e);
			throw new DynamicExtensionsSystemException(
					"Error occured while reading Object value from file", e);
		}
		catch (SQLException e)
		{
			LOGGER.error("Error occured while reading Blob Object from database", e);
			throw new DynamicExtensionsSystemException(
					"Error occured while reading Blob Object from database", e);
		}
		finally
		{
			if (ois != null)
			{
				closeStream(ois);
			}
		}
	}

	/**
	 * Sets the value.
	 * @param value the value to set
	 * @throws DynamicExtensionsSystemException
	 */
	public void setValue(Object value) throws DynamicExtensionsSystemException
	{
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			byte[] buff = bos.toByteArray();
			setObjectValue(Hibernate.createBlob(buff));
		}
		catch (IOException e)
		{
			LOGGER.error("Error occured while writing Object value to file", e);
			throw new DynamicExtensionsSystemException(
					"Error occured while writing Object value to file", e);
		}
		finally
		{
			closeStream(bos);
			closeStream(oos);
		}

	}

	/**
	 * @param relationalOperator the relationalOperator to set
	 */
	public void setRelationalOperator(RelationalOperator relationalOperator)
	{
		this.relationalOperator = relationalOperator;
	}

	/**
	 * @return the relationalOperator
	 */
	public RelationalOperator getRelationalOperator()
	{
		return relationalOperator;
	}

	/**
	 * Sets the object value.
	 * @param objectValue the new object value
	 */
	private void setObjectValue(Blob objectValue)
	{
		this.objectValue = objectValue;
	}

	/**
	 * Gets the object value.
	 * @return the object value
	 */
	private Blob getObjectValue()
	{
		return objectValue;
	}

	/**
	 * Sets the action.
	 * @param action the new action
	 */
	public void setAction(Action action)
	{
		this.action = action;
	}

	/**
	 * Gets the action.
	 * @return the action
	 */
	public Action getAction()
	{
		return action;
	}

	private void closeStream(Closeable stream) throws DynamicExtensionsSystemException
	{
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			LOGGER.error("Error occured while closing file stream", e);
			throw new DynamicExtensionsSystemException("Error occured while closing file stream", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object getCategoryObjectValue(
			Map<BaseAbstractAttributeInterface, Object> objectValueState)
	{
		if (objectValueState.get(categoryAttribute) == null)
		{
			Set<Entry<BaseAbstractAttributeInterface, Object>> entrySet = objectValueState
					.entrySet();
			for (Entry<BaseAbstractAttributeInterface, Object> entry : entrySet)
			{
				if (entry.getKey() instanceof CategoryAssociationInterface
						&& entry.getValue() != null)
				{
					List list = (List) entry.getValue();
					if (!list.isEmpty())
					{
						Map<BaseAbstractAttributeInterface, Object> objectValue = (Map<BaseAbstractAttributeInterface, Object>) list
								.iterator().next();
						Object value = getCategoryObjectValue(objectValue);
						if (value != null)
						{
							return value;
						}
					}
				}
			}
		}
		return objectValueState.get(categoryAttribute);
	}

}
