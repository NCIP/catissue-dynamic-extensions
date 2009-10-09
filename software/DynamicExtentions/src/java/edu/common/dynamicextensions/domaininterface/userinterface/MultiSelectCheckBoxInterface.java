package edu.common.dynamicextensions.domaininterface.userinterface;

import java.util.List;


/**
 * This class is used to create new control of multiselectCheckBox type
 * @author suhas_khot
 *
 */
public interface MultiSelectCheckBoxInterface extends SelectInterface,MultiSelectInterface
{

		/**
		 * @return the listOfValues
		 */
		public List getListOfValues();

		
		/**
		 * @param listOfValues the listOfValues to set
		 */
		public void setListOfValues(List listOfValues);
		
}
