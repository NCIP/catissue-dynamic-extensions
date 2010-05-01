
package edu.common.dynamicextensions.domaininterface.databaseproperties;

import java.util.Collection;

/**
 * These are the data base properties for an association.
 * @author geetika_bangard,pavan_kalantri
 */
public interface ConstraintPropertiesInterface extends DatabasePropertiesInterface
{

	/**
	 * This method retrieves the name of the constraint
	 * @return name of the constraint
	 */
	String getConstraintName();

	/**
	 * This method Will set the name of the constraint on the entity
	 * @param constraintName name to be set
	 */
	void setConstraintName(String constraintName);

	/**
	 * It will return the collection of sourceEntityConstraintKeyProperties used in inheritance and association
	 * @return collection of sourceEntityConstraintKeyProperties
	 */
	Collection<ConstraintKeyPropertiesInterface> getSrcEntityConstraintKeyPropertiesCollection();

	/**
	 * It will set the sourceEntityConstraintKeyPropertiesCollection
	 * @param srcEntityConstraintCollection 
	 */
	void setSrcEntityConstraintKeyPropertiesCollection(
			Collection<ConstraintKeyPropertiesInterface> srcEntityConstraintKeyPropertiesCollection);

	/**
	 * It will return the collection of targetEntityConstraintKeyProperties used in inheritance and association
	 * @return collection of targetEntityConstraintKeyProperties
	 */
	Collection<ConstraintKeyPropertiesInterface> getTgtEntityConstraintKeyPropertiesCollection();

	/**
	 * It will set the targetEntityConstraintKeyPropertiesCollection
	 * @param tgtEntityConstraint
	 */
	void setTgtEntityConstraintKeyPropertiesCollection(
			Collection<ConstraintKeyPropertiesInterface> tgtEntityConstraintKeyPropertiesCollection);

	/**
	 * It will clear the srcEntityConstraintCollection and then add srcEntityConstraint to it
	 * @param srcEntityConstraint
	 */
	void setSrcEntityConstraintKeyProp(ConstraintKeyPropertiesInterface srcEntityConstraint);

	/**
	* It will clear the tgtEntityConstraintCollection and then add tgtEntityConstraint to it
	* @param TgtEntityConstraint
	*/
	void setTgtEntityConstraintKeyProp(ConstraintKeyPropertiesInterface TgtEntityConstraint);

	/**
	 * It will retrieve the 1st object from srcEntityConsraintCollection and will return it.
	 * @return srcEntityConstraint 
	 */
	ConstraintKeyPropertiesInterface getSrcEntityConstraintKeyProperties();

	/**
	 * It will retrieve the 1st object from tgtEntityConsraintCollection and will return it.
	 * @return TgtEntityConstraint
	 */
	ConstraintKeyPropertiesInterface getTgtEntityConstraintKeyProperties();

	/**
	 * It will add the given tgtCnstrKeyProp object to the tgtEntityConstraintCollection
	 * @param tgtCnstrKeyProp
	 */
	void addTgtConstraintKeyProperties(ConstraintKeyPropertiesInterface tgtCnstrKeyProp);

	/**
	 * It will add the given srcCnstrKeyProp object to the srcEntityConstraintCollection
	 * @param srcCnstrKeyProp
	 */
	void addSrcConstaintKeyProperties(ConstraintKeyPropertiesInterface srcCnstrKeyProp);

}
