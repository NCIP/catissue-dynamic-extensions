
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public class DynamicQueryUtil
{

	public static DynamicQueryBean getDynamicQuery(Long containerID, DynamicQueryBean bean)
			throws NumberFormatException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Container container = Container.getContainer(containerID);
		if (container != null)
		{

			String dynamicTableName = container.getDbTableName();
			bean.getFromPart().append(dynamicTableName);
			bean.getWherePart().append(
					" and " + bean.getRecordEntryParamName() + ".IDENTIFIER = " + dynamicTableName
							+ ".IDENTIFIER ");

			List<Control> subFormCtrls = new ArrayList<Control>();

			for (Control ctrl : container.getControlsMap().values())
			{
				if (ctrl instanceof SubFormControl)
				{
					subFormCtrls.add(ctrl);
				}
				else if (bean.getControlCaption().equalsIgnoreCase(ctrl.getCaption()))
				{
					bean.getWherePart().append(
							" and " + dynamicTableName + "."
									+ ctrl.getColumnDefs().iterator().next().getColumnName()
									+ " = 'Yes'");
					break;
				}
			}
			for (Control ctrl : subFormCtrls)
			{
				SubFormControl subFormCtrl = (SubFormControl) ctrl;
				Container subFormContainer = subFormCtrl.getSubContainer();
				String dynamicTableNameOfSubCtrl = subFormContainer.getDbTableName();
				bean.getFromPart().append(", " + dynamicTableNameOfSubCtrl);
				bean.getWherePart().append(
						" and " + dynamicTableNameOfSubCtrl + ".PARENT_RECORD_ID = "
								+ dynamicTableName + ".IDENTIFIER ");

				for (Control subCtrl : subFormContainer.getControlsMap().values())
				{
					if (bean.getControlCaption().equalsIgnoreCase(subCtrl.getCaption()))
					{
						bean.getWherePart().append(
								" and " + dynamicTableNameOfSubCtrl + "."
										+ subCtrl.getColumnDefs().iterator().next().getColumnName()
										+ " = 'Yes'");
						break;
					}
				}
			}
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
									+ DynamicExtensionsUtility
											.getBaseAttributeOfcategoryAttribute(
													categoryAttributeInterface)
											.getColumnProperties().getName() + " = 'Yes'");
					break outerloop;

				}

			}
			getDynamicQueryForCategory(categoryEntityInterface, chieldTableassociation, bean);
		}
	}

}
