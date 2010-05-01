package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.FormulaInterface;

/**
 * @version 1.0
 * @created 16-June-2009 12:20:07 PM
 * @hibernate.class table="DYEXTN_FORMULA"
 */
public class Formula extends DynamicExtensionBaseDomainObject implements FormulaInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	/**
	 * 
	 */
	protected String expression;
	
	/**
	 * @hibernate.property name="expression" type="string" column="EXPRESSION" length="255"
	 * @return Returns the note.
	 */
	public String getExpression() 
	{
		return expression;
	}
	/**
	 * 
	 * @param expression
	 */
	public void setExpression(String expression) 
	{
		this.expression = expression;
	}

	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_FORMULA_SEQ"
	 */
	public Long getId()
	{
		return id;
	}
}
