
package edu.common.dynamicextensions.domaininterface;

import edu.common.dynamicextensions.util.global.DEConstants.ValueDomainType;

public interface CaDSRValueDomainInfoInterface extends DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * @return Returns the id.
	 */
	Long getId();

	/**
	 * @return the datatype
	 */
	String getDatatype();

	/**
	 * @param datatype the datatype to set
	 */
	void setDatatype(String datatype);

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AssociationInterface#getAssociationDirection()
	 */
	ValueDomainType getValueDomainType();

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AssociationInterface#setAssociationDirection(edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection)
	 */
	void setValueDomainType(ValueDomainType valueDomainType);

}
