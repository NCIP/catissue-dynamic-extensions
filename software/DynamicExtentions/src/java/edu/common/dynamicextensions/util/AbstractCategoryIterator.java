
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;

/**
 * This is an abstract class for category iteration.
 * @author shrishail_kalshetty
 *
 * @param <T> T type object.
 */
public abstract class AbstractCategoryIterator<T extends Object>
{

	/**
	 * Category object.
	 */
	protected transient CategoryInterface category;

	/**
	 * Parameterized constructor.
	 * @param categoryInterface Category object.
	 */
	public AbstractCategoryIterator(CategoryInterface categoryInterface)
	{
		this.category = categoryInterface;
	}

	/**
	 * Iterate the category.
	 * @param object Object of T type.
	 */
	public void iterateCategory(T object)
	{
		final CategoryEntityInterface catEntity = this.category.getRootCategoryElement();

		object = processRootCategoryElement(catEntity);
		final ContainerInterface container = (ContainerInterface)catEntity.getContainerCollection().iterator().next();
		processContainer(container, object);

	}

	protected void processContainer(ContainerInterface container, T mainObject)
	{

		List<ControlInterface> controlCollection = new ArrayList<ControlInterface>(container
				.getAllControlsUnderSameDisplayLabel());
		List<Object> rowDataList = new ArrayList<Object>();
		Collections.sort(controlCollection);
		Collections.reverse(controlCollection);
		for (ControlInterface control : controlCollection)
		{

			if(control instanceof ContainmentAssociationControl)
			{
				//category association
				AbstractContainmentControl containmentControl = (AbstractContainmentControl) control;
				T innnerObject = processCategoryAssociation((CategoryAssociationInterface)control.getBaseAbstractAttribute());
				processContainer(containmentControl.getContainer(), innnerObject);
				postprocessCategoryAssociation(innnerObject, mainObject);
			}
			else
			{
				//category attribute
				CategoryAttributeInterface catAttribute =(CategoryAttributeInterface) control.getBaseAbstractAttribute();
				if(catAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{
					//multiselect attribute
					AssociationInterface associationInterface = (AssociationInterface) catAttribute
					.getAbstractAttribute();
					T innnerObject = processMultiSelect(associationInterface);
					processCategoryAttribute(catAttribute, mainObject);
					postprocessCategoryAssociation(innnerObject, mainObject);
				}
				else
				{
					//normal attribtue
					processCategoryAttribute(catAttribute, mainObject);
				}
			}

		}
	}

	/**
	 * Process each category entity.
	 * @param categoryEntity category entity object.
	 * @param mainObject main object.
	 */
	protected void processCategoryEntity(CategoryEntityInterface categoryEntity, T mainObject)
	{

		for (CategoryAttributeInterface attributeInterface : categoryEntity
				.getCategoryAttributeCollection())
		{
			if (attributeInterface.getAbstractAttribute() instanceof AssociationInterface)
			{
				AssociationInterface associationInterface = (AssociationInterface) attributeInterface
						.getAbstractAttribute();
				T innnerObject = processMultiSelect(associationInterface);
				processCategoryAttribute(attributeInterface, innnerObject);
				postprocessCategoryAssociation(innnerObject, mainObject);

			}
			else
			{
				processCategoryAttribute(attributeInterface, mainObject);
			}
		}
		for (CategoryAssociationInterface categoryAssociation : categoryEntity
				.getCategoryAssociationCollection())
		{
			T innnerObject = processCategoryAssociation(categoryAssociation);
			processCategoryEntity(categoryAssociation.getTargetCategoryEntity(), innnerObject);
			postprocessCategoryAssociation(innnerObject, mainObject);
		}
	}

	/**
	 * Process root category element.
	 * @param rootCategoryEntity CategoryEntityInterface object.
	 * @return T type object.
	 */
	protected abstract T processRootCategoryElement(CategoryEntityInterface rootCategoryEntity);

	/**
	 * process each category entity attributes.
	 * @param attribute category attribute.
	 * @param object T type object.
	 */
	protected abstract void processCategoryAttribute(CategoryAttributeInterface attribute, T object);

	/**
	 * process each category entity associations.
	 * @param categoryAssociation Category association.
	 * @return T type object.
	 */
	protected abstract T processCategoryAssociation(CategoryAssociationInterface categoryAssociation);

	/**
	 * post process for each category association.
	 * @param innerObject T type object.
	 * @param mainObject T type object.
	 */
	protected abstract void postprocessCategoryAssociation(T innerObject, T mainObject);

	/**
	 * Process multi-select attribute.
	 * @param associationInterface Category association.
	 * @return  T type object.
	 */
	protected abstract T processMultiSelect(AssociationInterface associationInterface);
}
