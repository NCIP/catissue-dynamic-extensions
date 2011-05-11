
package edu.common.dynamicextensions.util.templategenerator;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * This is an abstract class for category iteration.
 * @author shrishail_kalshetty
 *
 * @param <T> T type object.
 */
public abstract class AbstractEntityIterator<T extends Object>
{

	/**
	 * Category object.
	 */
	protected transient EntityInterface entity;

	/**
	 * Parameterized constructor.
	 * @param categoryInterface Category object.
	 */
	public AbstractEntityIterator(EntityInterface entity)
	{
		this.entity = entity;
	}

	/**
	 * Iterate the category.
	 * @param object Object of T type.
	 */
	public void iterateEntity(T object)
	{
		object = processMainEntity(this.entity);
		processEntity(entity, object);

	}

	/**
	 * Process each category entity.
	 * @param categoryEntity category entity object.
	 * @param mainObject main object.
	 */
	protected void processEntity(EntityInterface entity, T mainObject)
	{

		for (AttributeInterface attributeInterface : entity
				.getAttributeCollection())
		{
				processAttribute(attributeInterface, mainObject);
		}
		for (AssociationInterface association : entity
				.getAssociationCollection())
		{
			T innnerObject = processAssociation(association);
			processEntity(association.getTargetEntity(), innnerObject);
			postprocessAssociation(innnerObject, mainObject);
		}
	}

	/**
	 * Process Main entity element.
	 * @param mainEntity CategoryEntityInterface object.
	 * @return T type object.
	 */
	protected abstract T processMainEntity(EntityInterface mainEntity);

	/**
	 * process each category entity attributes.
	 * @param attribute category attribute.
	 * @param object T type object.
	 */
	protected abstract void processAttribute(AttributeInterface attribute, T object);

	/**
	 * process each category entity associations.
	 * @param categoryAssociation Category association.
	 * @return T type object.
	 */
	protected abstract T processAssociation(AssociationInterface association);

	/**
	 * post process for each category association.
	 * @param innerObject T type object.
	 * @param mainObject T type object.
	 */
	protected abstract void postprocessAssociation(T innerObject, T mainObject);

}
