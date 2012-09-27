
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.AssignDataException;

/**
 * This Class represents the UserDefined DataElements
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_USERDEFINED_DE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
/**
 * @author deepali_ahirrao
 *
 */
public class UserDefinedDE extends DataElement implements UserDefinedDEInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1148563078444213122L;
	/**
	 * By default the permissible values are ordered alphabetically.
	 */
	protected Boolean isOrdered = true;
	

	/** The order. */
	protected String order = "Ascending";

	/**
	 * Collection of PermissibleValues
	 */
	protected Collection<PermissibleValueInterface> permissibleValueCollection = new LinkedHashSet<PermissibleValueInterface>();
	/**
	 * Collection of default permissible values
	 */
	protected Collection<PermissibleValueInterface> defaultPermissibleValues = new LinkedHashSet<PermissibleValueInterface>();

	/**
	 * Activation date of Version
	 */
	protected Date activationDate;

	/**
	 * Set all values from the form.
	 * @param abstractActionForm the ActionForm
	 * @throws AssignDataException if data is not in proper format.
	 */
	public void setAllValues(AbstractActionForm abstractActionForm) throws AssignDataException
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method returns the Collection of PermissibleValues.
	 *
	 * @hibernate.set name="permissibleValueCollection" table="DYEXTN_USERDEF_DE_VALUE_REL"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="USER_DEF_DE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-many-to-many class="edu.common.dynamicextensions.domain.PermissibleValue" column="PERMISSIBLE_VALUE_ID"
	 */
	public Collection<PermissibleValueInterface> getPermissibleValueCollection()
	{
		return permissibleValueCollection;
	}

	/**
	 * This method sets the permissibleValueCollection to the given Collection of PermissibleValues.
	 * @param permissibleValueCollection The permissibleValueCollection to set.
	 */
	public void setPermissibleValueCollection(
			Collection<PermissibleValueInterface> permissibleValueCollection)
	{
		this.permissibleValueCollection = permissibleValueCollection;
	}

	/**
	 * This method adds a PermissibleValue to the Collection of PermissibleValues.
	 * @param permissibleValue the PermissibleValue to be added.
	 */
	public void addPermissibleValue(PermissibleValueInterface permissibleValue)
	{
		if (this.permissibleValueCollection == null)
		{
			this.permissibleValueCollection = new LinkedHashSet<PermissibleValueInterface>();
		}
		this.permissibleValueCollection.add(permissibleValue);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface#clearPermissibleValues()
	 */
	public void clearPermissibleValues()
	{
		this.permissibleValueCollection = new LinkedHashSet<PermissibleValueInterface>();
	}

	/**
	 * @param permissibleValueColl
	 */
	public void addAllPermissibleValues(Collection<PermissibleValueInterface> permissibleValueColl)
	{
		if (this.permissibleValueCollection == null)
		{
			this.permissibleValueCollection = new LinkedHashSet<PermissibleValueInterface>();
		}
		this.permissibleValueCollection.addAll(permissibleValueColl);
	}

	/**
	 *
	 * @param permissibleValueList
	 */
	private Collection<PermissibleValueInterface> sortPermissibleValuesList(
			Collection<PermissibleValueInterface> permissibleValues)
	{
		List<PermissibleValueInterface> permissibleValuesList = new ArrayList<PermissibleValueInterface>();
		permissibleValuesList.addAll(permissibleValues);
		if (permissibleValuesList != null && !permissibleValuesList.isEmpty())
		{
			Collections.sort(permissibleValuesList, new Comparator<PermissibleValueInterface>()
			{

				public int compare(PermissibleValueInterface permissibleValueInterface1,
						PermissibleValueInterface permissibleValueInterface2)
				{
					if(permissibleValueInterface1.getId()!=null && permissibleValueInterface2.getId()!=null)
					{
					return permissibleValueInterface1.getId().compareTo(
							permissibleValueInterface2.getId());
					}
					return 0;
				}
			});
		}
		return permissibleValuesList;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface#isOrdered()
	 *
	 * @hibernate.property name="isOrdered" type="boolean" column="IS_ORDERED"
	 */
	public Boolean getIsOrdered()
	{
		return isOrdered;
	}

	/**
	 * This private method is for hibernate to set parent
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface#setOrdered(boolean)
	 */
	public void setIsOrdered(Boolean isOrdered)
	{
		this.isOrdered = isOrdered;
	}

	/**
	 * getPermissibleValues.
	 */
	public Collection<PermissibleValueInterface> getPermissibleValues()
	{
		return this.id != null
				? sortPermissibleValuesList(permissibleValueCollection)
				: permissibleValueCollection;
	}

	/**
	 * @return activationDate
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface#getActivationDate()
	 */
	public Date getActivationDate()
	{
		return activationDate;
	}

	/**
	 * @param versionActivationDate version Activation Date
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface
	 * #setActivationDate(java.util.Date)
	 */
	public void setActivationDate(Date versionActivationDate)
	{
		this.activationDate = versionActivationDate;
	}

	/**
	 * This method returns the Collection of PermissibleValues.
	 *
	 * @hibernate.set name="defaultPermissibleValues" table="DYEXTN_USERDEF_DEF_PV_REL"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="USER_DEF_DE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-many-to-many class="edu.common.dynamicextensions.domain.PermissibleValue" column="PERMISSIBLE_VALUE_ID"
	 */
	public Collection<PermissibleValueInterface> getDefaultPermissibleValues()
	{
		return defaultPermissibleValues;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface#
	 * setDefaultPermissibleValues(java.util.Collection)
	 */
	public void setDefaultPermissibleValues(
			Collection<PermissibleValueInterface> defaultPermissibleValues)
	{
		this.defaultPermissibleValues = defaultPermissibleValues;
	}

	/**
	 * Gets the order.
	 * @return the order
	 */
	public String getOrder()
	{
		return order;
	}


	/**
	 * Sets the order.
	 * @param order the order to set
	 */
	public void setOrder(String order)
	{
		this.order = order;
	}

}
