
package edu.common.dynamicextensions.domaininterface.userinterface;


/**
 * ListBoxInterface stores necessary information for generating ListBox control on
 * dynamically generated user interface.
 * @author geetika_bangard
 */
public interface ListBoxInterface extends SelectInterface,MultiSelectInterface
{
	
	/**
	 * @return
	 */
	public Boolean getIsUsingAutoCompleteDropdown();
	
	/**
	 * @param isUsingAutoCompleteDropdown
	 */
	public void setIsUsingAutoCompleteDropdown(Boolean isUsingAutoCompleteDropdown);

	/**
	 * This method returns the Number of rows to be displayed on the UI for ListBox.
	 * @return the Number of rows to be displayed on the UI for ListBox.
	 */
	Integer getNoOfRows();

	/**
	 * This method sets the Number of rows to be displayed on the UI for ListBox.
	 * @param noOfRows the Number of rows to be set for ListBox.
	 */
	void setNoOfRows(Integer noOfRows);

}
