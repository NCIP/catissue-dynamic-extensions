
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SelectInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.wustl.common.beans.NameValueBean;

/**
 * @author rahul_ner
 * @hibernate.joined-subclass table="DYEXTN_SELECT_CONTROL"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public abstract class SelectControl extends Control
		implements
			AssociationControlInterface,
			SelectInterface
{

	public static int minQueryChar = 3;

	protected String separator = "";

	protected List<NameValueBean> optionList = new ArrayList<NameValueBean>();

	protected Collection<AssociationDisplayAttributeInterface> associationDisplayAttributeCollection = new HashSet<AssociationDisplayAttributeInterface>();

	/**
	 * This method Returns the associationDisplayAttributeCollection.
	 * @hibernate.set name="associationDisplayAttributeCollection" table="DYEXTN_ASSO_DISPLAY_ATTR"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="SELECT_CONTROL_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.AssociationDisplayAttribute"
	 * @return Returns the associationDisplayAttributeCollection.
	 */
	public Collection<AssociationDisplayAttributeInterface> getAssociationDisplayAttributeCollection()
	{
		return associationDisplayAttributeCollection;
	}

	/**
	 * @param associationDisplayAttributeCollection The associationDisplayAttributeCollection to set.
	 */
	public void setAssociationDisplayAttributeCollection(
			Collection<AssociationDisplayAttributeInterface> associationDisplayAttributeCollection)
	{
		this.associationDisplayAttributeCollection = associationDisplayAttributeCollection;
	}

	/**
	 * @return Returns the separator
	 * @hibernate.property name="separator" type="string" column="SEPARATOR_STRING"
	 */
	public String getSeparator()
	{
		return separator;
	}

	/**
	 * @param separator The separator to set.
	 */
	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface#addAssociationDisplayAttribute(edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface)
	 */
	public void addAssociationDisplayAttribute(
			AssociationDisplayAttributeInterface associationDisplayAttribute)
	{
		associationDisplayAttributeCollection.add(associationDisplayAttribute);
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface#removeAssociationDisplayAttribute(edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface)
	 */
	public void removeAssociationDisplayAttribute(
			AssociationDisplayAttributeInterface associationDisplayAttribute)
	{
		associationDisplayAttributeCollection.remove(associationDisplayAttribute);
	}

	/**
	 *
	 */
	public void removeAllAssociationDisplayAttributes()
	{
		associationDisplayAttributeCollection.clear();
	}

	/**
	 * getValueList
	 * @param association
	 * @param valueList
	 */
	protected void getValueList(AssociationInterface association, List<String> valueList)
	{
		if (association.getIsCollection())
		{
			Collection<AbstractAttributeInterface> attributes = association.getTargetEntity()
					.getAllAbstractAttributes();
			Collection<AbstractAttributeInterface> filteredAttributes = EntityManagerUtil
					.filterSystemAttributes(attributes);
			List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
					filteredAttributes);
			if(value != null && value.toString().length() !=0)
			{
				List<Map> values = (List<Map>) value;
				if (values != null)
				{
					for (Map valueMap : values)
					{
						String value = (String) valueMap.get(attributesList.get(0));
						if(value != null && !"".equals(value))
						{
							valueList.add(value);
						}
					}
				}
			}
		}
		else
		{
			if (value instanceof List)
			{
				for (Long obj : (List<Long>) value)
				{
					valueList.add(obj.toString());
				}
			}

		}

	}


	public List<NameValueBean> getOptionList()
	{
		return optionList;
	}


	public void setOptionList(List<NameValueBean> optionList)
	{
		this.optionList = optionList;
	}

}
