
package edu.common.dynamicextensions.util;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * This is an helper API for creating category.
 * @author mandar_shidhore
 * @author kunal_kamble
 */
public interface CategoryHelperInterface
{

	/**
	 * These are constants to be used while creating controls.
	 */
	public enum ControlEnum {
		TEXT_FIELD_CONTROL("textField"), LIST_BOX_CONTROL("listBox"), DATE_PICKER_CONTROL(
				"datePicker"), FILE_UPLOAD_CONTROL("fileUpload"), RADIO_BUTTON_CONTROL(
				"radioButton"), TEXT_AREA_CONTROL("textArea"), CHECK_BOX_CONTROL("checkBox"), COMBO_BOX_CONTROL(
				"comboBox"), LABEL_CONTROL("label"), MULTISELECT_CHECKBOX_CONTROL(
				"multiselectCheckBox");

		String value;

		ControlEnum()
		{
			// TODO Auto-generated constructor stub
		}

		ControlEnum(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}

		public static ControlEnum get(String value)
		{
			ControlEnum[] ControlEnumValues = ControlEnum.values();

			for (ControlEnum ce : ControlEnumValues)
			{
				if (ce.getValue().equalsIgnoreCase(value))
				{
					return ce;
				}
			}
			return null;
		}
	};

	/**
	 * Create a new category if category with the given name does not exist.
	 * @param name name by which we wish to create the category.
	 * @return category
	 * @throws DynamicExtensionsSystemException
	 */
	CategoryInterface getCategory(String name) throws DynamicExtensionsSystemException;

	/**
	 * Create category container and category entity from given entity.
	 * @param entity entity used to create a category entity and category container.
	 * @param containerCaption container name on UI.
	 * @param CategoryInterface category
	 * @param categoryEntityName
	 * @return container.
	 */
	ContainerInterface createOrUpdateCategoryEntityAndContainer(EntityInterface entity,
			String containerCaption, CategoryInterface category, String... categoryEntityName);

	/**
	 * Set the root category entity of this category.
	 * @param container root category entity's container.
	 * @param category
	 */
	void setRootCategoryEntity(ContainerInterface container, CategoryInterface category);

	/**
	 * Add a controls to category containers. Attribute name is used to get the attribute of the entity.
	 * controlValue is used to select the type of control desired. controlCaption is used to modify the
	 * UI property i.e. label of the attribute. Lastly a list of permissible values is passed if any permissible
	 * values subset exist for an attribute.
	 * @param entity used to create a category entity.
	 * @param attributeName name of the attribute belonging to the entity.
	 * @param container the container created for category entity to which we wish to add a control.
	 * @param controlType type of control to be created. (e.g. ControlEnum.TEXT_FIELD_CONTROL)
	 * @param permissibleValues in case of radio buttons, lists and combo boxes, the list of permissible values is required, optional otherwise.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	ControlInterface addOrUpdateControl(EntityInterface entity, String attributeName,
			ContainerInterface container, ControlEnum controlType, String controlCaption,
			String heading, List<FormControlNotesInterface> controlNotes,
			Map<String, Object> rulesMap, Map<String, String> permValueOptions, long lineNumber,
			Set<String>... permissibleValues)
			throws DynamicExtensionsSystemException;

	/**
	 * Associate category containers with each other.
	 * E.G. User (1) ------userstudyassociation------> (*) Study. Here user category entity's container will be sourceContainer,
	 * study category entity's container will be targetContainer, associationNameList will contain source roles
	 * for all associations between the containers. noOfEntries indicates multiplicity.
	 * @param category
	 * @param entityGroup
	 * @param sourceContainer equivalent to main form
	 * @param targetContainer equivalent to sub form
	 * @param associationList association(s) present between two entities involved.
	 * e.g 'userstudyassociation' as depicted above
	 * @param noOfEntries indicates multiplicity. e.g. one-to-one (1) or one-to-many (-1) etc.
	 * @param string
	 * @return CategoryAssociationControlInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	CategoryAssociationControlInterface associateCategoryContainers(CategoryInterface category,
			EntityGroupInterface entityGroup, ContainerInterface sourceContainer,
			ContainerInterface targetContainer, List<AssociationInterface> associationList,
			int noOfEntries, String string) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * Method returns the next sequenceNumber
	 * @param container category entity container
	 * @return next sequence number
	 */
	int getNextSequenceNumber(ContainerInterface container);

	/**
	 * It will create a list of permissible values by reading the file.
	 * @param entity entity to which attribute belongs.
	 * @param attributeName name of the attribute whose permissible values are needed.
	 * @param desiredPermissibleValues collection of permissible values.
	 * @return List of permissible values
	 * @throws DynamicExtensionsApplicationException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	Set<PermissibleValueInterface> createPermissibleValuesList(EntityInterface entity,
			String attributeName, Long lineNo,
			Set<String> desiredPermissibleValues)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * This method will add the instance information to the given path.
	 * @param path path which is to be updated for instance information.
	 * @param instance instance string
	 * @throws DynamicExtensionsSystemException exception.
	 */
	void addInstanceInformationToPath(PathInterface path, String instance)
			throws DynamicExtensionsSystemException;

	/**
	 * This will return the list of permissible values.
	 * @param attributeTypeInformation attribute type information.
	 * @param desiredPermissibleValues desired values collection.
	 * @return list of permissible value.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws ParseException exception.
	 */
	List<PermissibleValueInterface> getPermissibleValueList(
			AttributeTypeInformationInterface attributeTypeInformation,
			Set<String> desiredPermissibleValues)
			throws DynamicExtensionsSystemException, ParseException;

	/**
	 * It will check whether any category entity with the name categoryEntityName is present in category if,
	 * present will return that else will create a new category entity with the given name.
	 * @param category category in which to search for category entity.
	 * @param entity entity from which to create the category entity.
	 * @param categoryEntityName name of the catgory entity.
	 * @return created or found category entity.
	 */
	CategoryEntityInterface createOrUpdateCategoryEntity(CategoryInterface category,
			EntityInterface entity, String categoryEntityName);

	/**
	 * It will create a category attribute with the name attributeName, for categoryEntity.
	 * @param entity entity from which to search for underlying attribute.
	 * @param attributeName name of the attribtue in entity.
	 * @param categoryEntity category entity in which to add the category attribute.
	 * @return created category attribute.
	 */
	CategoryAttributeInterface createCategoryAttribute(EntityInterface entity,
			String attributeName, CategoryEntityInterface categoryEntity);

	/**
	 * It will associate category entities given as parameter in sourceCategoryEntity & targetCategoryEntity.
	 * @param sourceCategoryEntity source category entity for association.
	 * @param targetCategoryEntity target category entity for association.
	 * @param name name of the category association
	 * @param numberOfentries multiplicity of the association wheather its single or multiline.
	 * @param entityGroup entity group.
	 * @param associationList list of associations.
	 * @param instance instance string.
	 * @return created association.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	CategoryAssociationInterface associateCategoryEntities(
			CategoryEntityInterface sourceCategoryEntity,
			CategoryEntityInterface targetCategoryEntity, String name, int numberOfentries,
			EntityGroupInterface entityGroup, List<AssociationInterface> associationList,
			String instance) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * It will set the default control options for each type of control.
	 * @param control control for which to set options.
	 * @param controlType type of the control.
	 * @param isLazyPvLoading in case of combobox is pv's to be loaded dynamically or not.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	void setDefaultControlsOptions(ControlInterface control, ControlEnum controlType,boolean isLazyPvLoading)
			throws DynamicExtensionsSystemException;

	/**
	 * It will set the options for given control by reading the csv file.
	 * e.g. columns, iscalculated etc.
	 * @param control control for which to set options.
	 * @param nextLine next line read.
	 * @throws DynamicExtensionsSystemException exception
	 */
	void setOptions(DynamicExtensionBaseDomainObjectInterface dyextnBaseDomainObject,
			Map<String, String> options, long lineNumber) throws DynamicExtensionsSystemException;

	/**
	 * return the category attribute with the given name which is created using the given entity & attribtue name.
	 * @param entity entity from which to search the attribute.
	 * @param attributeName name of the attribute.
	 * @param categoryEntity category entity in which to add it.
	 * @return created category attribute.
	 */
	CategoryAttributeInterface getCategoryAttribute(EntityInterface entity, String attributeName,
			CategoryEntityInterface categoryEntity);

	ControlInterface addOrUpdateLabelControl(EntityInterface entity, ContainerInterface container,
			String controlCaption, long lineNumber, int xPosition, int yPosition);

	/**
	 * @param temp
	 */
	void removeAllSeprators(ContainerInterface temp);

	/**
	 * Separates the instance information form the string of the format entityName[instance]
	 * @param categoryEntityName
	 * @return
	 */
	Long getInsatnce(String categoryEntityName);

	/**
	 * This method will release the lock on the category so that other users can use it
	 * for furthure.
	 * @param category category on which the lock is released.
	 */
	void releaseLockOnCategory(CategoryInterface category);

	/**
	 * This method will verify weather this category attribute is marked as populateFromXml.
	 * If the attribute is marked then its properties will be set accordingly & category is also
	 * marked as populateFromXml & its concept code collection will be modified according to the
	 * category attribute.
	 * If only related attribute is marked as populateFromXml then validation will be thrown.
	 * If Attribute is marked populateFromXml & it does not have XPath tag then validation will be thrown.
	 * @param catAttribute category attribute which is to be checked for populateFromXml tag.
	 * @param category it's underlying category.
	 * @throws DynamicExtensionsSystemException thrown if any of the above mentioned criteria's failed.
	 *
	 */
	void updateCategoryAttributeForXmlPopulation(CategoryAttributeInterface catAttribute,
			CategoryInterface category) throws DynamicExtensionsSystemException;

	/**
	 *
	 * @param categoryAttribute
	 * @param permissibleValue
	 * @throws @throws DynamicExtensionsSystemException
	 */
	void setDefaultValue(CategoryAttributeInterface categoryAttribute,
			PermissibleValueInterface permissibleValue, boolean isNotAttributeTypeInfo)
			throws DynamicExtensionsSystemException;

}