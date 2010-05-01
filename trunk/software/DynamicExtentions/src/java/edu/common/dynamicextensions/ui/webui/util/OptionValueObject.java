/*
 * Created on Nov 22, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.webui.util;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OptionValueObject
{

	private String optionName = null;
	private String optionDescription = null;
	private String optionConceptCode = null;

	/**
	 * @return
	 */
	public String getOptionConceptCode()
	{
		return this.optionConceptCode;
	}

	/**
	 * @param optionConceptCode
	 */
	public void setOptionConceptCode(String optionConceptCode)
	{
		this.optionConceptCode = optionConceptCode;
	}

	/**
	 * @return
	 */
	public String getOptionDescription()
	{
		return this.optionDescription;
	}

	/**
	 * @param optionDescription
	 */
	public void setOptionDescription(String optionDescription)
	{
		this.optionDescription = optionDescription;
	}

	/**
	 * @return
	 */
	public String getOptionName()
	{
		return this.optionName;
	}

	/**
	 * @param optionName
	 */
	public void setOptionName(String optionName)
	{
		this.optionName = optionName;
	}

}
