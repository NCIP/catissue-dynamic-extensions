/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManagerHelper;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface;
import edu.wustl.dao.JDBCDAO;

/**
 * @author Vinayak Pawar
 *
 */
public class EntityUpdateCommand {	
	private static final String FILE_NAME_SUFFIX = "_" + DynamicExtensionsQueryBuilderConstantsInterface.FILE_NAME;
	
	private static final String CONTENT_TYPE_SUFFIX = "_" + DynamicExtensionsQueryBuilderConstantsInterface.CONTENT_TYPE;
	
	protected Map<Long, UpdateStatement> updates = new HashMap<Long, UpdateStatement>();
	
	public void setAttr(AttributeInterface attr, Object dataValue) {
		if (attr.getName().equals("id")) {
			throw new RuntimeException("Entity identifier can not be updated");
		}
		
		Long entityId = attr.getEntity().getId();
		UpdateStatement update = updates.get(entityId);
		if (update == null) {
			update = new UpdateStatement();
			update.setTableName(attr.getEntity().getTableProperties().getName());
			updates.put(entityId, update);
		}
		
			
		try {
			if (attr.getAttributeTypeInformation() instanceof FileAttributeTypeInformation) {
				setFileColumns(update, attr, dataValue);
			} else {
				dataValue = AbstractMetadataManagerHelper.getInstance().getDataValue((AbstractAttribute)attr, dataValue);
				update.setColumn(attr.getColumnProperties().getName(), dataValue);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error getting appropriate data value for attribute: " + attr.getEntity().getName() + "." + attr.getName(), e);
		}		
	}
	
	public void setAttr(AssociationInterface parentAssociation, Long parentId) {
		if (parentAssociation == null) {
			return;
		}
			
		Long entityId = parentAssociation.getTargetEntity().getId();
		UpdateStatement update = updates.get(entityId);
		if (update == null) {
			update = new UpdateStatement();
			update.setTableName(parentAssociation.getTargetEntity().getTableProperties().getName());
			updates.put(entityId, update);
		}
		
		update.setColumn(parentAssociation.getConstraintProperties().getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName(), parentId);
	}
	
	public void setIdCondition(Long id) {
		for (Map.Entry<Long, UpdateStatement> update : updates.entrySet()) {	
			update.getValue().setCondition("IDENTIFIER", id);
		}
	}
	
	public void execute(JDBCDAO jdbcDao) {
		for (UpdateStatement update : updates.values()) {
			update.execute(jdbcDao);
		}
	}
	
	private void setFileColumns(UpdateStatement update, AttributeInterface attr, Object dataValue) {
		String columnName = attr.getColumnProperties().getName();
		
		if (dataValue instanceof String) { 
			update.setColumn(columnName + FILE_NAME_SUFFIX, (String)dataValue);
			return;
		}
		
		FileAttributeRecordValue fileValue = (FileAttributeRecordValue) dataValue;
		String filename = null, contentType = null;
		byte[] content = null;
		
		if (fileValue != null) {
			filename = fileValue.getFileName();
			contentType = fileValue.getContentType();
			content = fileValue.getFileContent();
		}
		
		
		update.setColumn(columnName + FILE_NAME_SUFFIX, filename);
		update.setColumn(columnName + CONTENT_TYPE_SUFFIX, contentType);
		update.setColumn(columnName, content);
	}
}
