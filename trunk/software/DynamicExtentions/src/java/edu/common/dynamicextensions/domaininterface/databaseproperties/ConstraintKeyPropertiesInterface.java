
package edu.common.dynamicextensions.domaininterface.databaseproperties;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;

public interface ConstraintKeyPropertiesInterface
{

	/**
	 *It will fetch the record from srcPrimaryKeyAttributeCollection & will return
	 * @return AttributeTypeInformationInterface
	 */
	AttributeInterface getSrcPrimaryKeyAttribute();

	/**
	 *It will fetch the record from srcPrimaryKeyAttributeCollection & will return
	 * @return AttributeTypeInformationInterface
	 */
	ColumnPropertiesInterface getTgtForiegnKeyColumnProperties();

	/**
	 * It will add the primaryKeyAttribute to the srcPrimaryKeyAttributeCollection
	 */
	void setSrcPrimaryKeyAttribute(AttributeInterface primaryKeyAttribute);

	/**
	 *It will fetch the record from srcPrimaryKeyAttributeCollection & will return
	 * @return AttributeTypeInformationInterface
	 */
	void setTgtForiegnKeyColumnProperties(ColumnPropertiesInterface foriegnKeyColumnProperties);

}
