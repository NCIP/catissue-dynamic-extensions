/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.domain;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_ATTRIBUTE_TYPE_INFO"
 */
public abstract class AttributeTypeInformation extends DynamicExtensionBaseDomainObject
		implements
			AttributeTypeInformationInterface
{

	/**
	 * Serial Version Unique Identifief
	 */
	protected static final long serialVersionUID = 1234567890L;

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEX_ATTR_TYP_INFO_SEQ"
	 * @return Returns the id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 *
	 */
	protected Collection<DataElementInterface> dataElementCollection = new HashSet<DataElementInterface>();

	/**
	 *
	 */
	protected Collection<PermissibleValueInterface> defaultValueCollection = new HashSet<PermissibleValueInterface>();

	/**
	 * @hibernate.set name="dataElementCollection" table="DYEXTN_DATA_ELEMENT"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ATTRIBUTE_TYPE_INFO_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.DataElement"
	 * @return Returns the dataElementCollection.
	 */
	private Collection getDataElementCollection()
	{
		return dataElementCollection;
	}

	/**
	 * @param dataElementCollection The dataElementCollection to set.
	 */
	private void setDataElementCollection(Collection dataElementCollection)
	{
		this.dataElementCollection = dataElementCollection;
	}

	/**
	 *
	 * @return
	 */
	public DataElementInterface getDataElement()
	{
		DataElementInterface dataElementInterface = null;
		if (dataElementCollection != null)
		{
			Iterator dataElementIterator = dataElementCollection.iterator();
			if (dataElementIterator.hasNext())
			{
				dataElementInterface = (DataElement) dataElementIterator.next();
			}
		}
		return dataElementInterface;
	}

	/**
	 *
	 * @param sourceEntity
	 */
	public void setDataElement(DataElementInterface dataElementInterface)
	{
		if (dataElementCollection == null)
		{
			dataElementCollection = new HashSet();
		}
		this.dataElementCollection.add(dataElementInterface);
	}

	/**
	 * @param dataElementInterface
	 */
	public void removeDataElement(DataElementInterface dataElementInterface)
	{
		if (dataElementCollection != null && dataElementInterface != null)
		{
			this.dataElementCollection.remove(dataElementInterface);
		}
	}

	/**
	 * @hibernate.set name="defaultValueCollection" table="DYEXTN_PERMISSIBLE_VALUE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ATTRIBUTE_TYPE_INFO_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.PermissibleValue"
	 * @return Returns the dataElementCollection.
	 */
	private Collection getDefaultValueCollection()
	{
		return defaultValueCollection;
	}

	/**
	 * @param dataElementCollection The dataElementCollection to set.
	 */
	private void setDefaultValueCollection(Collection defaultValueCollection)
	{
		this.defaultValueCollection = defaultValueCollection;
	}

	/**
	 *
	 * @return
	 */
	public PermissibleValueInterface getDefaultValue()
	{
		PermissibleValueInterface permissibleValueInterface = null;
		if (defaultValueCollection != null && !defaultValueCollection.isEmpty())
		{
			Iterator dataElementIterator = defaultValueCollection.iterator();
			permissibleValueInterface = (PermissibleValueInterface) dataElementIterator.next();
		}
		return permissibleValueInterface;
	}

	/**
	 *
	 * @param sourceEntity
	 */
	public void setDefaultValue(PermissibleValueInterface permissibleValue)
	{
		if (defaultValueCollection == null)
		{
			defaultValueCollection = new HashSet();
		}
		if (this.getDefaultValue() != null)
		{
			defaultValueCollection.remove(this.getDefaultValue());
		}
		this.defaultValueCollection.add(permissibleValue);
	}

	/**
	 *
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public abstract PermissibleValueInterface getPermissibleValueForString(String value)
			throws ParseException;

}
