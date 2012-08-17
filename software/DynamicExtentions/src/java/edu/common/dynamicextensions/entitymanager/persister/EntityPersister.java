/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author Vinayak Pawar
 *
 */
public class EntityPersister {
	
	public Long insertRecord(EntityInterface entity, Map<AbstractAttributeInterface, Object> dataValue) {		
		return insertRecord(entity, dataValue, null, null);
	}
		
	public Long insertRecord(EntityInterface entity, Map<AbstractAttributeInterface, Object> dataValue, AssociationInterface parentAssociation, Long parentId) {
		return insertRecord(getJdbcDao(), entity, dataValue, parentAssociation, parentId);
		
	}
	
	@SuppressWarnings({ "unchecked" })
	public Long insertRecord(JDBCDAO jdbcDao, EntityInterface entity, Map<AbstractAttributeInterface, Object> dataValue, AssociationInterface parentAssociation, Long parentId) {
		EntityInsertCommand insertCommand = new EntityInsertCommand(entity);
								
		// If empty, insert row with only identifier column value.
		if (dataValue == null) {
			dataValue = new HashMap<AbstractAttributeInterface, Object>();
		}
			
		for (Entry<AbstractAttributeInterface, Object> dataValueEntry : dataValue.entrySet()) {
			AbstractAttribute attr = (AbstractAttribute) dataValueEntry.getKey();				
			if (attr instanceof AttributeInterface) {
				insertCommand.setAttr((AttributeInterface)attr, dataValueEntry.getValue());
			}
		}
			
		if (parentAssociation != null) {
			insertCommand.setAttr(parentAssociation, parentId);
		}
		
		// execute and get long id				
		Long entityId = insertCommand.execute(jdbcDao);		
		for (Entry<AbstractAttributeInterface, Object> dataValueEntry : dataValue.entrySet()) {
			AbstractAttribute attr = (AbstractAttribute) dataValueEntry.getKey();
			if (!(attr instanceof AssociationInterface)) {
				continue;
			}
				
			AssociationInterface association = (AssociationInterface) attr;
			List<Map<AbstractAttributeInterface, Object>> listOfMapsForContainedEntity = (List<Map<AbstractAttributeInterface, Object>>)dataValueEntry.getValue();
			for (Map<AbstractAttributeInterface, Object> valueMapForContainedEntity : listOfMapsForContainedEntity)	{
				insertRecord(jdbcDao, association.getTargetEntity(), valueMapForContainedEntity, association, entityId);
			}
		}
					
		return entityId;						
	}	
	
	
	public void updateRecord(EntityInterface entity, Map<AbstractAttributeInterface, Object> dataValue, Long id) {
		updateRecord(getJdbcDao(), entity, dataValue, id);	
	}
	
	@SuppressWarnings("unchecked")
	public void updateRecord(JDBCDAO jdbcDao, EntityInterface entity, Map<AbstractAttributeInterface, Object> dataValue, Long id) {
		try {
			if (dataValue == null) {
				return;
			}
			
			EntityUpdateCommand updateCommand = new EntityUpdateCommand();			
			for (Map.Entry<AbstractAttributeInterface, Object> dvEntry : dataValue.entrySet()) {
				AbstractAttributeInterface absAttr = dvEntry.getKey();
				
				if (absAttr instanceof AttributeInterface) {
					updateCommand.setAttr((AttributeInterface)absAttr, dvEntry.getValue());				
				} else if (absAttr instanceof AssociationInterface) {
					AssociationInterface association = (AssociationInterface)absAttr;
					Set<Long> associatedRecIds = getAssociatedRecordIds(jdbcDao, association, id);

					Object value = dvEntry.getValue();
					List<Map<AbstractAttributeInterface, Object>> listOfMapsForContainedEntity = (List<Map<AbstractAttributeInterface, Object>>) value;
					for (Map<AbstractAttributeInterface, Object> valueMapForContainedEntity : listOfMapsForContainedEntity) {
						edu.common.dynamicextensions.domain.EntityRecord entityRecord = new edu.common.dynamicextensions.domain.EntityRecord();
						Long recordId = (Long) valueMapForContainedEntity.get(entityRecord);
							
						if (recordId != null) {
							updateRecord(jdbcDao, association.getTargetEntity(), valueMapForContainedEntity, recordId);
							associatedRecIds.remove(recordId);
						} else {
							insertRecord(jdbcDao, association.getTargetEntity(), valueMapForContainedEntity, association, id);
						}						
					}
					
					for (Long recordId : associatedRecIds) {
						deleteRecord(jdbcDao, association.getTargetEntity(), recordId);
					}
				}				
			}
			
			updateCommand.setIdCondition(id);
			updateCommand.execute(jdbcDao);		
		} catch (Exception e) {
			throw new RuntimeException("Error updating entity: " + entity.getName() + ", identifier: " + id, e);
		}
	}
	
	
	public void deleteRecord(EntityInterface entity, Long id)  {
		deleteRecord(getJdbcDao(), entity, id);
	}
	
	public void deleteRecord(JDBCDAO jdbcDao, EntityInterface entity, Long id) {
		while (entity != null) {
			for (AssociationInterface association : entity.getAssociationCollection()) {
				deleteAssociation(jdbcDao, association, id);					
			}
			
			EntityDeleteCommand deleteCommand = new EntityDeleteCommand(entity, id);
			deleteCommand.execute(jdbcDao);
					
			entity = entity.getParentEntity();
		}			
	}
	
	public void updateAssociation(AssociationInterface association, Long entityId, Long fkId) {
		updateAssociation(getJdbcDao(), association, entityId, fkId);			
	}
	
	public void updateAssociation(JDBCDAO jdbcDao, AssociationInterface association, Long entityId, Long fkId) {
		EntityUpdateCommand updateCommand = new EntityUpdateCommand();
		updateCommand.setAttr(association, fkId);
		updateCommand.setIdCondition(entityId);
		updateCommand.execute(jdbcDao);
	}
	
	private void deleteAssociation(JDBCDAO jdbcDao, AssociationInterface association, Long foreignKey) {
		Set<Long> entityIds = getAssociatedRecordIds(jdbcDao, association, foreignKey);
			
		for (Long entityId : entityIds) {
			deleteRecord(jdbcDao, association.getTargetEntity(), entityId);
		}			
	}
								
	@SuppressWarnings("unchecked")
	private Set<Long> getAssociatedRecordIds(JDBCDAO jdbcDao, AssociationInterface association, Long foreignKey) {
		String tableName = association.getTargetEntity().getTableProperties().getName();
		String foreignKeyName = association.getConstraintProperties().getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName();
		
		Set<Long> entityIds = new HashSet<Long>();
		StringBuilder selectSql = new StringBuilder("SELECT IDENTIFIER FROM ").append(tableName)
				.append(" WHERE ").append(foreignKeyName).append(" = ?");
		
		try {
			LinkedList<ColumnValueBean> columnValues = new LinkedList<ColumnValueBean>();
			columnValues.add(new ColumnValueBean(foreignKeyName, foreignKey));
			
			List<List<String>> rowStringIds = jdbcDao.executeQuery(selectSql.toString(), null, columnValues);
			
			for (List<String> row : rowStringIds) {
				entityIds.add(Long.parseLong(row.get(0)));
			}							
		} catch (Exception e) {
			throw new RuntimeException("Failed:  " + selectSql.toString() + ", key = " + foreignKey, e);
		}
			
		return entityIds;
	}
	
	private JDBCDAO getJdbcDao() {
		try {
			return DynamicExtensionsUtility.getJDBCDAO();
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining JDBC DAO", e);
		}
	}
}
