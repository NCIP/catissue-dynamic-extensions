/**
 * 
 */
package edu.common.dynamicextensions.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domain.FormAuditEvent;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.LabelInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.domain.AuditEvent;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dynamicextensions.caching.impl.ObjectFactoryImpl;

/**
 * @author Vinayak Pawar
 *
 */
public class FormAuditManager {
	private static final Logger logger = edu.wustl.common.util.logger.Logger.getLogger(ObjectFactoryImpl.class);
	
	private static final FormAuditManager instance = new FormAuditManager();
	
	public static FormAuditManager getInstance() {
		return instance;
	}
	
	public void audit(SessionDataBean sdb, ContainerInterface container, Map<BaseAbstractAttributeInterface, Object> valueMap, Long recordIdentifier, String operation) {
		StringBuilder xml = new StringBuilder();
		String formName = getFormName(container);
		
		xml.append("<form-submit>");	
		xml.append("<name>").append(formName).append("</name>");
		xml.append("<user>").append((sdb != null) ? sdb.getUserName() : "no-user").append("</user>");
		xml.append("<ip-address>").append((sdb != null) ? sdb.getIpAddress() : "no-ip").append("</ip-address>");
		xml.append("<record-identifier>").append(recordIdentifier).append("</record-identifier>");	
		xml.append(getFieldSetsXml(container, valueMap));
		xml.append("</form-submit>");
		
		persist(sdb, formName, recordIdentifier, xml.toString(), operation);
	}
	
	private void persist(SessionDataBean sdb, String formName, Long recordId, String xml, String operation) {
		AuditEvent auditEvent = new AuditEvent();
		auditEvent.setIpAddress((sdb != null) ? sdb.getIpAddress() : "no-ip");
		auditEvent.setTimestamp(Calendar.getInstance().getTime());
		auditEvent.setEventType((operation != null) ? operation : "");
		auditEvent.setUserId((sdb != null) ? sdb.getUserId() : null);
		
		try {
			HibernateDAO hibernateDao = DynamicExtensionsUtility.getHibernateDAO(sdb);
			hibernateDao.insert(auditEvent);
			
			FormAuditEvent formAuditEvent = new FormAuditEvent();
			formAuditEvent.setIdentifier(auditEvent.getId());
			formAuditEvent.setFormName(formName);
			formAuditEvent.setRecordId(recordId);
//			formAuditEvent.setFormDataXml(Hibernate.createClob(xml));
			hibernateDao.insert(formAuditEvent);
			hibernateDao.commit();
		} catch (Exception e) {
			logger.error("Failed to persist audit data", e);
		}
	}
	
	private String getFieldSetsXml(ContainerInterface curContainer, Map<BaseAbstractAttributeInterface, Object> curValueMap) {
		StringBuilder xml = new StringBuilder();
	
		List<ContainerInterface> containers = new ArrayList<ContainerInterface>();
		List<Map<BaseAbstractAttributeInterface, Object>> valueMaps = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
		
		containers.add(curContainer);
		valueMaps.add(curValueMap);
		while (!containers.isEmpty()) {
			curContainer = containers.remove(0);
			curValueMap = valueMaps.remove(0);
			if (curValueMap == null) {
				continue;
			}
			
			xml.append("<field-set>");
			xml.append("<model-entity>").append(getModelEntityName(curContainer)).append("</model-entity>");
			xml.append("<db-table>").append(getTableName(curContainer)).append("</db-table>");
			
			for (ControlInterface control : curContainer.getAllControls()) {
				if (control instanceof AbstractContainmentControlInterface) {
					addAssociationContainers((AbstractContainmentControlInterface)control, curValueMap, containers, valueMaps);
				} else if (control != null && !(control instanceof LabelInterface)){
					xml.append(getFieldXml(control, curValueMap));
				}
				
			}
			
			xml.append("</field-set>");
		
			for (ContainerInterface childContainer : curContainer.getChildContainerCollection()) {
				AssociationMetadataInterface parentChildAssoc = getAssociation(curContainer, childContainer);
				List<Map<BaseAbstractAttributeInterface, Object>> childValueMaps = ((List<Map<BaseAbstractAttributeInterface, Object>>)curValueMap.get(parentChildAssoc));
				if (childValueMaps == null || childValueMaps.isEmpty()) {
					continue;
				}
				
				containers.add(childContainer);
				valueMaps.add(childValueMaps.get(0));
			}
		}
		
		return xml.toString();
	}
	
	private void addAssociationContainers(AbstractContainmentControlInterface control, Map<BaseAbstractAttributeInterface, Object> curValueMap, List<ContainerInterface> containers, List<Map<BaseAbstractAttributeInterface, Object>> valueMaps) {
		Object assocMap = curValueMap.get(control);
		
		if (assocMap instanceof List) {
			List<Map<BaseAbstractAttributeInterface, Object>> assocMapList = (List<Map<BaseAbstractAttributeInterface, Object>>)assocMap;
			for (Map<BaseAbstractAttributeInterface, Object> map : assocMapList) {
				containers.add(control.getContainer());
				valueMaps.add(map);
			}
		} else if (assocMap != null) {
			containers.add(control.getContainer());
			valueMaps.add((Map<BaseAbstractAttributeInterface, Object>)assocMap);
		}
	}
	
	private String getFieldXml(ControlInterface control, Map<BaseAbstractAttributeInterface, Object> valueMap) {
		StringBuilder xml = new StringBuilder();
		xml.append("<field>");
		xml.append("<ui-label>").append(control.getCaption()).append("</ui-label>");
		xml.append("<model-attr>").append(getModelAttrName(control)).append("</model-attr>");
		
		BaseAbstractAttributeInterface bAttr = control.getBaseAbstractAttribute();
		AbstractAttributeInterface attr = null;
		if (bAttr instanceof CategoryAttributeInterface) {
			attr = ((CategoryAttributeInterface)bAttr).getAbstractAttribute();
		} else {
			attr = (AbstractAttributeInterface)bAttr;
		}
				
		if (attr instanceof AssociationInterface) {
			xml.append(getCollectionXml(bAttr, (AssociationInterface)attr, valueMap));
		} else {
			xml.append("<db-column>").append(getDbColumnName(control)).append("</db-column>")
			   .append("<value>").append(getAttrValue(control, valueMap)).append("</value>");
		}
		
		xml.append("</field>");
		return xml.toString();
	}
	
	private String getCollectionXml(BaseAbstractAttributeInterface bAttr, AssociationInterface association, Map<BaseAbstractAttributeInterface, Object> valueMap) {
		StringBuilder xml = new StringBuilder();
		List<Map<BaseAbstractAttributeInterface, Object>> elementValueMaps = (List<Map<BaseAbstractAttributeInterface, Object>>)valueMap.get(bAttr);

		xml.append("<collection>")
		   .append("<db-table>").append(association.getTargetEntity().getTableProperties().getName()).append("</db-table>");
		if (elementValueMaps != null) {
			for (Map<BaseAbstractAttributeInterface, Object> elementValueMap : elementValueMaps) {
				if (elementValueMap == null || elementValueMap.isEmpty()) {
					continue;
				}
				
				for (Map.Entry<BaseAbstractAttributeInterface, Object> elementMapEntry : elementValueMap.entrySet()) {
					if (elementMapEntry != null) {
						AttributeInterface elementAttr = (AttributeInterface)elementMapEntry.getKey();							
						xml.append("<element>")
						   .append("<db-column>").append(elementAttr.getColumnProperties().getName()).append("</db-column>")
						   .append("<value>").append(elementMapEntry.getValue()).append("</value>")
						   .append("</element>");
					}
				}
			}			
		}
		xml.append("</collection>");
		return xml.toString();
	}
				
	
	//
	// Helper methods
	//
	
	private String getFormName(ContainerInterface container) {
		String name;
		if (container.getAbstractEntity() instanceof CategoryEntityInterface) {
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface)container.getAbstractEntity();
			name = categoryEntity.getCategory().getName();
		} else {
			name = container.getAbstractEntity().getName();
		}
		
		return name;
	}
	
	private String getModelEntityName(ContainerInterface container) {
		AbstractEntityInterface entity = null;
		if (container.getAbstractEntity() instanceof CategoryEntityInterface) {
			entity = ((CategoryEntityInterface)container.getAbstractEntity()).getEntity();
		} else {
			entity = container.getAbstractEntity();
		}
		
		return entity.getEntityGroup().getName() + "." + entity.getName();
	}
	
	private String getModelAttrName(ControlInterface control) {
		BaseAbstractAttributeInterface attr = control.getBaseAbstractAttribute();
		String attrName;
		if (attr instanceof CategoryAttributeInterface) {
			attrName = ((CategoryAttributeInterface) attr).getAbstractAttribute().getName();
		} else {
			attrName = attr.getName();
		}
		
		return attrName;
	}
	
	
	private String getTableName(ContainerInterface container) {
		AbstractEntityInterface entity = null;
		if (container.getAbstractEntity() instanceof CategoryEntityInterface) {
			entity = ((CategoryEntityInterface)container.getAbstractEntity()).getEntity();
		} else {
			entity = container.getAbstractEntity();
		}
		
		return entity.getTableProperties().getName();
	}
	
	private String getDbColumnName(ControlInterface control) {
		BaseAbstractAttributeInterface attr = control.getBaseAbstractAttribute();
		AttributeInterface entityAttr = null;
		if (attr instanceof CategoryAttributeInterface) {
			entityAttr = (AttributeInterface)((CategoryAttributeInterface) attr).getAbstractAttribute();
		} else if (attr instanceof AttributeInterface){
			entityAttr = (AttributeInterface) attr;
		} 
		
		return (entityAttr != null) ? entityAttr.getColumnProperties().getName() : "unknown";
	}
	
	private AssociationMetadataInterface getAssociation(ContainerInterface parentContainer, ContainerInterface childContainer) {
		return parentContainer.getAbstractEntity().getAssociation(childContainer.getAbstractEntity());
	}
	
	private String getAttrValue(ControlInterface control, Map<BaseAbstractAttributeInterface, Object> valueMap) {
		BaseAbstractAttributeInterface attr = control.getBaseAbstractAttribute();
		Object value = valueMap.get(attr);
		return (value != null) ? value.toString() : "null";
	}
}
