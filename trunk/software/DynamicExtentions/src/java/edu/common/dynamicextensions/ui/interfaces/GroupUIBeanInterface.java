/*
 * Created on Nov 16, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.interfaces;

import java.util.List;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface GroupUIBeanInterface
{

	/**
	 * 
	 * @return group description
	 */
	String getGroupDescription();

	/**
	 * 
	 * @param groupDescription : description for the group
	 */
	void setGroupDescription(String groupDescription);

	/**
	 * 
	 * @return groupName 
	 */
	String getGroupName();

	/**
	 * 
	 * @param groupName : Name of group
	 */
	void setGroupName(String groupName);

	/**
	 * 
	 * @return Group operation performed :  Save or show next page
	 */
	String getGroupOperation();

	/**
	 * 
	 * @param groupOperation : Group operation performed - Save or show next page
	 */
	void setGroupOperation(String groupOperation);

	/**
	 * 
	 * @return create new group or use existing
	 */
	String getCreateGroupAs();

	/**
	 * 
	 * @param createGroupAs Create group or use existing
	 */
	void setCreateGroupAs(String createGroupAs);

	/**
	 * @return
	 */
	List getGroupList();

	/**
	 * 
	 * @param groupList
	 */
	void setGroupList(List groupList);

	/**
	 * 
	 * @return
	 */
	String getGroupNameText();

	/**
	 * 
	 * @param groupNameText
	 */
	void setGroupNameText(String groupNameText);

}
