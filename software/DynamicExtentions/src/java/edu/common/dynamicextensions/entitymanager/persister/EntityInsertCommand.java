/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManagerHelper;
import edu.wustl.dao.JDBCDAO;

/**
 * @author Vinayak Pawar
 *
 */
public class EntityInsertCommand {
	protected EntityInterface entity;
	
	protected Map<Long, InsertStatement> inserts = new LinkedHashMap<Long, InsertStatement>();
	
	protected EntityUpdateCommand blobUpdateCommand;
	
	public EntityInsertCommand(EntityInterface entity) {
		this.entity = entity;
		
		Stack<EntityInterface> entities = new Stack<EntityInterface>();
		entities.push(entity);
		
		while (entity.getParentEntity() != null) {
			entity = entity.getParentEntity();
			entities.push(entity);
		}
		
		while (!entities.isEmpty()) {
			entity = entities.pop();
			InsertStatement insert = new InsertStatement();
			insert.setTableName(entity.getTableProperties().getName());
			inserts.put(entity.getId(), insert);
		}		
	}
	
	public void setAttr(AttributeInterface attr, Object dataValue) {
		if (attr.getAttributeTypeInformation() instanceof FileAttributeTypeInformation) {
			if (blobUpdateCommand == null) {
				blobUpdateCommand = new EntityUpdateCommand();
			}
			blobUpdateCommand.setAttr(attr, dataValue);
			return;
		}
		
		Long entityId = attr.getEntity().getId();
		InsertStatement insert = inserts.get(entityId);
	
		try {
			dataValue = AbstractMetadataManagerHelper.getInstance().getDataValue((AbstractAttribute)attr, dataValue);
		} catch (Exception e) {
			throw new RuntimeException("Error getting appropriate data value for attribute: " + attr.getEntity().getName() + "." + attr.getName(), e);
		}
		
		insert.setColumn(attr.getColumnProperties().getName(), dataValue);
	}
	
	public void setAttr(AssociationInterface parentAssociation, Long parentId) {
		if (parentAssociation == null) {
			return;
		}
		
		Long entityId = parentAssociation.getTargetEntity().getId();
		InsertStatement insert = inserts.get(entityId);
		insert.setColumn(parentAssociation.getConstraintProperties().getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName(), parentId);
	}
			
	public Long execute(JDBCDAO jdbcDao) {		
		Long identifier = null;
		
		for (InsertStatement insert : inserts.values()) {
			insert.setIdentifier(identifier);
			identifier = insert.execute(jdbcDao);
		}
		
		if (blobUpdateCommand != null) {
			blobUpdateCommand.setIdCondition(identifier);
			blobUpdateCommand.execute(jdbcDao);
		}
		
		return identifier;		
	}		
}
