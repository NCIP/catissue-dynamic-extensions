
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
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
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

	/**
	 * Generate script tag for auto complete.
	 *
	 * @param controlId the control id
	 * @param sourceHtmlComponentValues the source html component values
	 * @param applyTo the apply to
	 *
	 * @return the string
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	protected String generateScriptTagForAutoComplete(String controlId,
			String sourceHtmlComponentValues, String applyTo)
			throws DynamicExtensionsSystemException
	{
		String parentContainerId = "";
		String categoryEntityName = "";
		if (getParentContainer() != null && getParentContainer().getId() != null)
		{
			parentContainerId = getParentContainer().getId().toString();
			categoryEntityName = getParentContainer().getAbstractEntity().getName();
		}
		String attributeName = getBaseAbstractAttribute().getName();
		StringBuffer comboStringBuffer = new StringBuffer(700);
		comboStringBuffer
				.append("var myUrl= \"DEComboDataAction.do?controlId=")
				.append(controlId)
				.append("~containerIdentifier=")
				.append(parentContainerId)
				.append("~sourceControlValues=")
				.append(sourceHtmlComponentValues)
				.append("~categoryEntityName=")
				.append(categoryEntityName)
				.append("~attributeName=")
				.append(attributeName)
				.append(
						"\";var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});var combo = new Ext.form.ComboBox({store: ds,width:140,listWidth:240,hiddenName: 'CB_coord_")
				.append(getHTMLComponentName())
				.append(
						"',displayField:'excerpt',valueField: 'id',typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',triggerAction: 'all',minChars : ")
				.append(minQueryChar)
				.append(
						",queryDelay:500,lazyInit:true,emptyText:'--Select--',valueNotFoundText:'',selectOnFocus:'true',applyTo: '")
				.append(applyTo).append("'});");
		return comboStringBuffer.toString();
	}


}
