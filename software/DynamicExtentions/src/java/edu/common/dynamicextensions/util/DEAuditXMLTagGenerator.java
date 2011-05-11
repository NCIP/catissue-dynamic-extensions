
package edu.common.dynamicextensions.util;

import java.lang.reflect.Field;

import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.audit.util.AuditXMLGenerator;
import edu.wustl.common.audit.util.AuditXMLTagGenerator;

/**
 * This class is to generate containment association  in audit XML for multiselect attribute.
 * The Class DEAuditXMLTagGenerator.
 * @author suhas_khot
 *
 */
public class DEAuditXMLTagGenerator extends AuditXMLTagGenerator
{

	/* (non-Javadoc)
	 * @see edu.wustl.common.audit.util.AuditXMLTagGenerator#updateAttributeTag(java.lang.StringBuffer, java.lang.reflect.Field)
	 */
	@Override
	protected void updateAttributeTag(StringBuffer auditableMetatdataXML, Field field)
	{
		if (field.getName().startsWith(DEConstants.COLLECTIONATTRIBUTE)
				&& AuditXMLGenerator.excludeAssociation)
		{
			AuditXMLGenerator.excludeAssociation = false;
			super.updateAttributeTag(auditableMetatdataXML, field);
			AuditXMLGenerator.excludeAssociation = true;
		}
		else
		{
			super.updateAttributeTag(auditableMetatdataXML, field);
		}

	}

	@Override
	protected void generateAttributeTag(StringBuffer auditableMetatdataXML, Field field)
	{
		if (!DEConstants.AUDIT_TABLENAME.equals(field.getName())
				&& !DEConstants.AUDIT_COL_NAME_MAP.equals(field.getName()))
		{
			super.generateAttributeTag(auditableMetatdataXML, field);
		}
	}
}
