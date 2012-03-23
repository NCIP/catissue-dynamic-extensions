
package edu.common.dynamicextensions.ui.webui.actionform;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author sujay_narkar
 *
 */
public class DataEntryForm
{

	/**
	 *
	 */
	private static final long serialVersionUID = -7828307676065035418L;

	/**
	 *
	 */
	protected String entitySaved;
	/**
	 *
	 */
	protected String showFormPreview = "false";

	/**
	 *
	 */
	protected String recordIdentifier;
	/**
	 *
	 */
	protected String isShowTemplateRecord = "";
	/**
	 *
	 */
	protected List<String> errorList;
	/**
	 *
	 */
	protected String mode = WebUIManagerConstants.EDIT_MODE;
	/**
	 *
	 */
	protected String childContainerId;
	/**
	 *
	 */
	protected String childRowId;
	/**
	 *
	 */
	protected String dataEntryOperation = "";
	/*
	 *
	 */
	protected boolean isTopLevelEntity = true;

	protected String previewBack;

	protected String containerId;

	/**
	 * @return the containerId
	 */
	public String getContainerId()
	{
		return containerId;
	}

	/**
	 * @param containerId the containerId to set
	 */
	public void setContainerId(final String containerId)
	{
		this.containerId = containerId;
	}

	/**
	 * @return formId
	 */
	public int getFormId()
	{
		return 0;
	}

	/**
	 * @param arg0 abstractDomainObject
	 */
	public void setAllValues(final AbstractDomainObject arg0)
	{
		//TODO empty method
	}

	/**
	 *
	 */
	protected void reset()
	{
		//TODO empty method
	}

	/**
	 *
	 * @return entitySaved
	 */
	public String getEntitySaved()
	{
		return entitySaved;
	}

	/**
	 *
	 * @param entitySaved entitySaved
	 */
	public void setEntitySaved(final String entitySaved)
	{
		this.entitySaved = entitySaved;
	}

	/**
	 *
	 * @return String showFormPreview
	 */
	public String getShowFormPreview()
	{
		return showFormPreview;
	}

	/**
	 *
	 * @param showFormPreview String showFormPreview
	 */
	public void setShowFormPreview(final String showFormPreview)
	{
		this.showFormPreview = showFormPreview;
	}

	/**
	 * @return recordIdentifier
	 */
	public String getRecordIdentifier()
	{
		return recordIdentifier;
	}

	public void setRecordIdentifier(final String recordIdentifier)
	{
		this.recordIdentifier = recordIdentifier;
	}

	/**
	 * @return the errorList
	 */
	public List<String> getErrorList()
	{
		return new ArrayList<String>();
	}

	/**
	 * @param errorList the errorList to set
	 */
	public void setErrorList(final List<String> errorList)
	{
		this.errorList = errorList;
	}

	/**
	 * @return Returns the mode.
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * @param mode The mode to set.
	 */
	public void setMode(final String mode)
	{
		this.mode = mode;
	}

	/**
	 *
	 * @return
	 */
	public String getChildContainerId()
	{
		return childContainerId;
	}

	/**
	 *
	 * @param childContainerId
	 */
	public void setChildContainerId(final String childContainerId)
	{
		this.childContainerId = childContainerId;
	}

	/**
	 *
	 * @return
	 */
	public String getChildRowId()
	{
		return childRowId;
	}

	/**
	 *
	 * @param childRowId
	 */
	public void setChildRowId(final String childRowId)
	{
		this.childRowId = childRowId;
	}

	/**
	 *
	 * @return
	 */
	public String getDataEntryOperation()
	{
		return dataEntryOperation;
	}

	/**
	 *
	 * @param operation
	 */
	public void setDataEntryOperation(final String operation)
	{
		this.dataEntryOperation = operation;
	}

	/**
	 * @return Returns the isTopLevelEntity.
	 */
	public boolean getIsTopLevelEntity()
	{
		return isTopLevelEntity;
	}

	/**
	 * @param isTopLevelEntity The isTopLevelEntity to set.
	 */
	public void setIsTopLevelEntity(final boolean isTopLevelEntity)
	{
		this.isTopLevelEntity = isTopLevelEntity;
	}

	/**
	 * @return the previewBack
	 */
	public String getPreviewBack()
	{
		return previewBack;
	}

	/**
	 * @param previewBack the previewBack to set
	 */
	public void setPreviewBack(final String previewBack)
	{
		this.previewBack = previewBack;
	}

	/**
	 *
	 * @return
	 */
	public String getIsShowTemplateRecord()
	{
		return isShowTemplateRecord;
	}

	/**
	 *
	 * @param isShowTemplateRecord
	 */
	public void setIsShowTemplateRecord(String isShowTemplateRecord)
	{
		this.isShowTemplateRecord = isShowTemplateRecord;
	}
}