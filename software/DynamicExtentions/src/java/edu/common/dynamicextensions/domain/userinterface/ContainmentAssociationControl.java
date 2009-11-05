/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;

/**
 * @author vishvesh_mulay
 * @hibernate.joined-subclass table="DYEXTN_CONTAINMENT_CONTROL"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class ContainmentAssociationControl extends AbstractContainmentControl
		implements
			ContainmentAssociationControlInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#getControlHTML(java.lang.String)
	 */
	protected String getControlHTML(String htmlString)
	{
		return htmlString;
	}

	/**
	 * This method returns true if the cardinality of the Containment Association is One to Many.
	 * @return true if Caridnality is One to Many, false otherwise.
	 */
	public boolean isCardinalityOneToMany()
	{
		boolean isOneToMany = false;
		AssociationInterface associationInterface = (AssociationInterface) this
				.getBaseAbstractAttribute();
		RoleInterface targetRole = associationInterface.getTargetRole();
		if (targetRole.getMaximumCardinality() == Cardinality.MANY)
		{
			isOneToMany = true;
		}
		return isOneToMany;
	}

	/**
	 *
	 */
	public List<String> getValueAsStrings() {
		// TODO Auto-generated method stub
		return null;
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
