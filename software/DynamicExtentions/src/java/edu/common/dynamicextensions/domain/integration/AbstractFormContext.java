
package edu.common.dynamicextensions.domain.integration;

import java.util.Collection;
import java.util.HashSet;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Host application must have a concrete implementation for this abstract class
 * @author deepali_ahirrao
 * @hibernate.class table="DYEXTN_ABSTRACT_FORM_CONTEXT"
 */
public abstract class AbstractFormContext extends AbstractDomainObject
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1235468709L;

	protected Long id;
	protected String formLabel;
	protected Long containerId;
	protected String activityStatus;
	protected Collection<AbstractRecordEntry> recordEntryCollection = new HashSet<AbstractRecordEntry>();

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_ABSTRACT_FRM_CTXT_SEQ"
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
	 * @return Returns the formLabel.
	 *  @hibernate.property name="formLabel" column="FORM_LABEL" type="string" length="255"
	 */
	public String getFormLabel()
	{
		return formLabel;
	}

	public void setFormLabel(String formLabel)
	{
		this.formLabel = formLabel;
	}

	/**
	 * @return Returns the containerId.
	 *  @hibernate.property name="containerId" column="CONTAINER_ID" type="long" length="30"
	 */
	public Long getContainerId()
	{
		return containerId;
	}

	public void setContainerId(Long containerId)
	{
		this.containerId = containerId;
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
	 * @return Returns the recordEntryCollection.
	 * @hibernate.set name="recordEntryCollection" table="DYEXTN_ABSTRACT_RECORD_ENTRY" cascade="save-update"
	 * inverse="true" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_FORM_CONTEXT_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.integration.AbstractRecordEntry"
	 * @return
	 */
	public Collection<AbstractRecordEntry> getRecordEntryCollection()
	{
		return recordEntryCollection;
	}

	public void setRecordEntryCollection(Collection<AbstractRecordEntry> recordEntryColn)
	{
		this.recordEntryCollection = recordEntryColn;
	}

}
