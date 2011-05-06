
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;

public interface AbstractEntityInterface extends AbstractMetadataInterface
{

	/**
	 * The table properties object contains name of the dynamically created table.
	 * @return
	 */
	TablePropertiesInterface getTableProperties();

	/**
	 * @param tableProperties
	 */
	void setTableProperties(TablePropertiesInterface tableProperties);

	/**
	*
	* @return
	*/
	Collection getContainerCollection();

	/**
	 *
	 * @param containerCollection
	 */
	void setContainerCollection(Collection containerCollection);

	/**
	 *
	 * @param containerInterface
	 */
	void addContainer(ContainerInterface containerInterface);

	/**
	 * @return Returns the isAbstract.
	 */
	boolean isAbstract();

	/**
	 * Clears the attibute list.
	 */
	void removeAllAttributes();

	/**
	 *
	 * @return ConstraintProperties
	 */
	ConstraintPropertiesInterface getConstraintProperties();

	/**
	 *
	 * @param constraintProperties
	 */
	void setConsraintProperties(ConstraintPropertiesInterface constraintProperties);

	/**
	 * @param targetEntity
	 * @return
	 */
	AssociationMetadataInterface getAssociation(AbstractEntityInterface targetEntity);

	/**
	 * This method will return the entityGroup to which this entity belongs.
	 * @return entityGroup
	 */
	EntityGroupInterface getEntityGroup();
}
