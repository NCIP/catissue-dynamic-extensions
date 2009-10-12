
package edu.common.dynamicextensions.processor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.actionForm.AbstractActionForm;

/**
 * @author sujay_narkar
 * @author chetan_patil
 *
 */
public class LoadDataEntryFormProcessor
{

	/**
	 * Empty Constructor
	 */
	protected LoadDataEntryFormProcessor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method gets the new instance of the entity processor to the caller.
	 * @return EntityProcessor EntityProcessor instance
	 */
	public static LoadDataEntryFormProcessor getInstance()
	{
		return new LoadDataEntryFormProcessor();
	}

	/**
	 *
	 * @param actionForm
	 * @param containerInterface
	 * @param recordIdentifier
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException DynamicExtensionsApplicationException
	 */
	public ContainerInterface loadDataEntryForm(AbstractActionForm actionForm,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> valueMap, String mode,
			String recordIdentifier) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		DataEntryForm dataEntryForm = (DataEntryForm) actionForm;

		if (mode != null)// && mode.equalsIgnoreCase(WebUIManagerConstants.VIEW_MODE))
		{
			containerInterface.setMode(mode);
		}

		if (valueMap != null && !valueMap.isEmpty())
		{
			containerInterface.setContainerValueMap(valueMap);
		}
		List processedContainersList = new ArrayList<ContainerInterface>();
		DynamicExtensionsUtility.setAllInContextContainers(containerInterface,
				processedContainersList);

		dataEntryForm.setContainerInterface(containerInterface);
		

		if (dataEntryForm.getErrorList() == null)
		{
			List<String> errorList = new ArrayList<String>();
			dataEntryForm.setErrorList(errorList);
		}
		if (dataEntryForm.getShowFormPreview() == null)
		{
			dataEntryForm.setShowFormPreview("");
		}
		if (recordIdentifier != null)
		{
			dataEntryForm.setRecordIdentifier(recordIdentifier);
		}
		else
		{
			dataEntryForm.setRecordIdentifier("");
		}
		return containerInterface;
	}
	/**
	 *
	 * @param entityInterface
	 * @param recordIdentifier
	 * @return
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<BaseAbstractAttributeInterface, Object> getValueMapFromRecordId(
			AbstractEntityInterface entityInterface, String recordIdentifier)
			throws NumberFormatException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, SQLException
	{
		Map<BaseAbstractAttributeInterface, Object> recordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		if (recordIdentifier != null && !recordIdentifier.equals(""))
		{
			//Quick fix:
			if (entityInterface instanceof EntityInterface)
			{
				EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
				Map map = entityManagerInterface.getRecordById((EntityInterface) entityInterface,
						Long.valueOf(recordIdentifier));
				recordMap = map;
			}
			else
			{
				CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
				CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) entityInterface;
				Long recordId = categoryManagerInterface
						.getRootCategoryEntityRecordIdByEntityRecordId(Long
								.valueOf(recordIdentifier), categoryEntityInterface
								.getTableProperties().getName());
				recordMap = categoryManagerInterface.getRecordById(categoryEntityInterface,
						recordId);
			}
		}

		return recordMap;
	}
}