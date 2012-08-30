
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;

/**
 * This Class represents the category.
 * @hibernate.joined-subclass table="DYEXTN_CAT_ASSO_CTL"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class CategoryAssociationControl extends AbstractContainmentControl
		implements
			CategoryAssociationControlInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This method returns true if the cardinality of the Containment Association is One to Many.
	 * @return true if Caridnality is One to Many, false otherwise.
	 */
	public boolean isCardinalityOneToMany()
	{
		boolean isOneToMany = false;
		CategoryAssociationInterface associationInterface = (CategoryAssociationInterface) this
				.getBaseAbstractAttribute();
		if (associationInterface.getTargetCategoryEntity().getNumberOfEntries() == -1)
		{
			isOneToMany = true;
		}
		return isOneToMany;
	}


	/**
	 *
	 */
	public List<String> getValueAsStrings()
	{
		List<String> list = new ArrayList<String>();
		String tValue = null;
		for (Map valueMap : (List<Map>)value)
		{	
			tValue = (String) valueMap.values().iterator().next();
			if(tValue != null && !"".equals(tValue))
			{
				list.add(tValue);
			}
		}
		return list;
	}


	/**
	 *
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}
	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}
}
