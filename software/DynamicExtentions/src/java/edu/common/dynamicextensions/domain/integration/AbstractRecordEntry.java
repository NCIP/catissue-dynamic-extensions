
package edu.common.dynamicextensions.domain.integration;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Host application must have a concrete implementation for this abstract class
 * @author deepali_ahirrao
 * @hibernate.class table="DYEXTN_ABSTRACT_RECORD_ENTRY"
 */
public abstract class AbstractRecordEntry extends AbstractDomainObject
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1235468709L;

	protected Long id;
	protected String activityStatus;
	protected AbstractFormContext formContext;

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_ABSTRACT_RE_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return Returns the activityStatus.
	 *  @hibernate.property name="activityStatus" column="ACTIVITY_STATUS" type="string" length="10"
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * 
	 * @return
	 * @hibernate.many-to-one column="ABSTRACT_FORM_CONTEXT_ID" class="edu.common.dynamicextensions.domain.integration.AbstractFormContext" constrained="true"
	 */
	public AbstractFormContext getFormContext()
	{
		return formContext;
	}

	public void setFormContext(AbstractFormContext formContext)
	{
		this.formContext = formContext;
	}

}
