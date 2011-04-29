
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;

/**
 * This is an abstract class extended by CategoryAttribute, AbstractAttribute.
 * This class stores basic information needed for metadata objects.
 * @version 1.0
 * @created 28-Sep-2006 12:20:06 PM
 * @hibernate.joined-subclass table="DYEXTN_BASE_ABSTRACT_ATTRIBUTE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @hibernate.cache  usage="read-write"
 */
public abstract class BaseAbstractAttribute extends AbstractMetadata
		implements
			BaseAbstractAttributeInterface
{
	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1234567890L;

}
