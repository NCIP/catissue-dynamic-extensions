/**
 * 
 */

package edu.common.dynamicextensions.ui.webui.actionform;

import java.util.List;

import edu.common.dynamicextensions.entitymanager.EntityRecord;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author chetan_patil
 *
 */
public class RecordListForm extends AbstractActionForm
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	/**
	 * 
	 */
	String containerIdentifier = null;

	/**
	 * 
	 */
	List<EntityRecord> entityRecordList = null;

	/**
	 * 
	 */
	String mode = null;

	/**
	 * @return the recordSnippetList
	 */
	public List<EntityRecord> getEntityRecordList()
	{
		return entityRecordList;
	}

	/**
	 * @param recordSnippetList the recordSnippetList to set
	 */
	public void setEntityRecordList(List<EntityRecord> entityRecordList)
	{
		this.entityRecordList = entityRecordList;
	}

	/**
	 * Overrides getFormId() method of ActionForm
	 * @return the form identifier
	 */
	public int getFormId()
	{
		return 0;
	}

	/**
	 * Overrides reset() method of ActionForm
	 */
	protected void reset()
	{
		// TODO This method will be provided if required.
	}

	/**
	 * Overrides setAllValues() method of ActionForm
	 * @param abstractDomainObject AbstractDomainObject
	 */
	public void setAllValues(AbstractDomainObject abstractDomainObject)
	{
		// TODO This method will be provided if required.
	}

	/**
	 * @return the containerIdentifier
	 */
	public String getContainerIdentifier()
	{
		return containerIdentifier;
	}

	/**
	 * @param containerIdentifier the containerIdentifier to set
	 */
	public void setContainerIdentifier(String containerIdentifier)
	{
		this.containerIdentifier = containerIdentifier;
	}

	/**
	 * @return the mode
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}

	/**
	 * This method set Identifier of newly added object by AddNew operation into FormBean
	 * which initialized AddNew operation.
	 * @param addNewFor - add New For.
	 * @param addObjectIdentifier - Identifier of newly added object by AddNew operation
	 */
	@Override
	public void setAddNewObjectIdentifier(String addNewFor, Long addObjectIdentifier)
	{
		// TODO Auto-generated method stub
		
	}

}
