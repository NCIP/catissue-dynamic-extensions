package edu.common.dynamicextensions.ui.webui.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.util.logger.Logger;

/**
 * 
 * @author mandar_shidhore
 *
 */
public class MultiselectUsingComboAndListTag extends TagSupport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	private String addNewUserActionName;

	public String getAddNewUserActionName()
	{
		return addNewUserActionName;
	}
	
	public void setAddNewUserActionName(String addNewUserActionName)
	{
		this.addNewUserActionName = addNewUserActionName;
	}

	@Override
	public int doStartTag() throws JspException
	{
		Logger.out.debug("Now rendering multiselect using MultiselectUsingComboAndListTag....");
		return SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException
	{	
		try
		{
			pageContext.getRequest().setAttribute("addNewUserActionName", addNewUserActionName);
			pageContext.include("../../../pages/de/MultiSelectUsingCombo.jsp");
		}
		catch (Exception e)
		{
			Logger.out.debug("Exception!! No response generated.");
			throw new JspException("Exception encountered while rendering custom tag MultiselectUsingComboAndListTag");
		}
		
		return EVAL_PAGE;
	}

}
