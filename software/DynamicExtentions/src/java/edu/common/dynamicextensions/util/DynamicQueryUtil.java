
package edu.common.dynamicextensions.util;

import java.util.Collection;

import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.cache.EntityCache;

public class DynamicQueryUtil
{

	public static DynamicQueryBean getDynamicQuery(Long containerID, DynamicQueryBean bean)
			throws NumberFormatException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		final ContainerInterface container = DynamicExtensionsUtility
				.getContainerByIdentifier(containerID.toString());

		EntityInterface staticEntity = EntityCache.getInstance().getEntityById(
				Long.valueOf(bean.getStaticEntityId()));

		AbstractEntityInterface abstractEntity = container.getAbstractEntity();
		if (abstractEntity instanceof CategoryEntityInterface)
		{
			CategoryInterface category = ((CategoryEntityInterface) abstractEntity).getCategory();
			CategoryEntityInterface rootCategoryElement = category.getRootCategoryElement();
			EntityInterface dynamicEntity = rootCategoryElement.getEntity();
			Collection<AssociationInterface> asntCollection = staticEntity
					.getAssociationCollection();
			AssociationInterface asntInterface = null;
			for (AssociationInterface association : asntCollection)
			{
				if (association.getTargetEntity().equals(dynamicEntity))
				{
					asntInterface = association;
					break;
				}
			}
			String fkName = asntInterface.getConstraintProperties()
					.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.getName();
			String dynamicTableName = asntInterface.getTargetEntity().getTableProperties()
					.getName();
			bean.getFromPart().append(dynamicTableName);
			bean.getWherePart().append(
					" and " + bean.getRecordEntryParamName() + "."+((AttributeInterface) asntInterface.getEntity()
							.getPrimaryKeyAttributeCollection().iterator().next()).getColumnProperties()
							.getName()+" = " + dynamicTableName
							+ "." + fkName);

			getDynamicQueryForCategory(rootCategoryElement, asntInterface, bean);
		}

		return bean;

	}

	private static void getDynamicQueryForCategory(CategoryEntityInterface rootCategoryElement,
			AssociationInterface association, DynamicQueryBean bean)
	{
		Collection<CategoryEntityInterface> childCategories = rootCategoryElement
				.getChildCategories();
		String tableName = "";

		AssociationInterface chieldTableassociation = null;
		outerloop : for (CategoryEntityInterface categoryEntityInterface : childCategories)
		{
			String parentTableName = association.getTargetEntity().getTableProperties().getName();
			String primaryKey = ((AttributeInterface) association.getEntity()
					.getPrimaryKeyAttributeCollection().iterator().next()).getColumnProperties()
					.getName();
			for (PathAssociationRelationInterface pathAssociationRelationInterface : categoryEntityInterface
					.getPath().getPathAssociationRelationCollection())
			{
				chieldTableassociation = pathAssociationRelationInterface.getAssociation();
				ConstraintKeyPropertiesInterface chieldTableConstraintKeyProperties = chieldTableassociation
						.getConstraintProperties().getTgtEntityConstraintKeyProperties();
				String fkName = chieldTableConstraintKeyProperties
						.getTgtForiegnKeyColumnProperties().getName();
				tableName = chieldTableassociation.getTargetEntity().getTableProperties().getName();
				bean.getFromPart().append(" ," + tableName);
				bean.getWherePart().append(
						" and " + parentTableName + "." + primaryKey + " = " + tableName + "."
								+ fkName);
			}
			for (CategoryAttributeInterface categoryAttributeInterface : categoryEntityInterface
					.getCategoryAttributeCollection())
			{
				ControlInterface control = CategoryHelper.getControl(
						(ContainerInterface) (categoryEntityInterface.getContainerCollection()
								.iterator().next()), categoryAttributeInterface);
				if (control != null
						&& control.getCaption().equalsIgnoreCase(bean.getControlCaption()))
				{
					bean.getWherePart().append(
							" and "
									+ tableName
									+ "."
									+ DynamicExtensionsUtility.getBaseAttributeOfcategoryAttribute(
											categoryAttributeInterface).getColumnProperties()
											.getName() + " = 'Yes'");
					break outerloop;

				}

			}
			getDynamicQueryForCategory(categoryEntityInterface, chieldTableassociation, bean);
		}
	}

}
