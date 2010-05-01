
package edu.common.dynamicextensions.entitymanager;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.validationrules.Rule;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * This Class is a mock class to test EntityManager
 * @author chetan_patil
 */
public class MockEntityManager extends DynamicExtensionsBaseTestCase
{

	int identifier = 1;
	int sequence = 1;
	int tableIndex = 0;
	int columnIndex = 0;

	public ContainerInterface createContainerForGivenEntity(String containerName,
			AbstractEntityInterface entity) throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		ContainerInterface containerInterface = null;
		ControlInterface controlInterface = null;

		containerInterface = domainObjectFactory.createContainer();
		containerInterface.setButtonCss("actionButton");
		containerInterface.setCaption("DummyContainer");
		containerInterface.setMainTableCss("formRequiredLabel");
		containerInterface.setRequiredFieldIndicatior("*");
		containerInterface.setRequiredFieldWarningMessage("indicates mandatory fields.");
		containerInterface.setTitleCss("formTitle");

		containerInterface.setAbstractEntity(entity);

		EntityInterface entityInterface = (EntityInterface) entity;

		Collection abstractAttributeCollection = entityInterface.getAbstractAttributeCollection();
		Iterator abstractAttributeCollectionIterator = abstractAttributeCollection.iterator();
		while (abstractAttributeCollectionIterator.hasNext())
		{
			AttributeInterface attributeInterface = (AttributeInterface) abstractAttributeCollectionIterator
					.next();
			if (attributeInterface.getName().equals("name"))
			{
				controlInterface = initializeTextField(attributeInterface);
			}
			else if (attributeInterface.getName().equals("description"))
			{
				controlInterface = initializeTextArea(attributeInterface);
			}
			else if (attributeInterface.getName().equals("dateOfJoining"))
			{
				controlInterface = initializeDatePicker(attributeInterface);
			}
			else if (attributeInterface.getName().equals("gender"))
			{
				controlInterface = initializeComboBox(attributeInterface);
			}
			containerInterface.addControl(controlInterface);
		}
		return containerInterface;
	}

	/**
	 * This method returns a dummy Container instance populated with dummy
	 * Controls, Entity and its attributes.	 *
	 * @param containerName Name of the Container
	 * @return Container instance
	 * @throws DynamicExtensionsApplicationException On failure to create Container instance
	 */
	public ContainerInterface getContainer(String containerName)
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityGroup entityGroup = (EntityGroup) domainObjectFactory.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		ContainerInterface containerInterface = null;
		ControlInterface controlInterface = null;
		EntityInterface entityInterface = null;

		containerInterface = domainObjectFactory.createContainer();
		containerInterface.setButtonCss("actionButton");
		containerInterface.setCaption("DummyContainer");
		containerInterface.setMainTableCss("formRequiredLabel");
		containerInterface.setRequiredFieldIndicatior("*");
		containerInterface.setRequiredFieldWarningMessage("indicates mandatory fields.");
		containerInterface.setTitleCss("formTitle");

		entityInterface = initializeEntity(entityGroup);
		containerInterface.setAbstractEntity((AbstractEntityInterface) entityInterface);
		entityInterface.getContainerCollection().add(containerInterface);

		Collection abstractAttributeCollection = entityInterface.getAbstractAttributeCollection();
		Iterator abstractAttributeCollectionIterator = abstractAttributeCollection.iterator();
		while (abstractAttributeCollectionIterator.hasNext())
		{
			AttributeInterface attributeInterface = (AttributeInterface) abstractAttributeCollectionIterator
					.next();
			if (attributeInterface.getName().equals("name"))
			{
				controlInterface = initializeTextField(attributeInterface);
			}
			else if (attributeInterface.getName().equals("description"))
			{
				controlInterface = initializeTextArea(attributeInterface);
			}
			else if (attributeInterface.getName().equals("dateOfJoining"))
			{
				controlInterface = initializeDatePicker(attributeInterface);
			}
			else if (attributeInterface.getName().equals("gender"))
			{
				controlInterface = initializeComboBox(attributeInterface);
			}
			containerInterface.addControl(controlInterface);
		}
		return containerInterface;
	}

	/**
	 * This method creates the populated dummy Entity instance
	 *
	 * @return Manually created dummy Entity along with its attributes
	 * @throws DynamicExtensionsApplicationException On failure to create Entity
	 */
	public EntityInterface initializeEntity(EntityGroupInterface entityGroup)
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AbstractAttributeInterface abstractAttributeInterface = null;

		EntityInterface person = createAndPopulateEntity();

		person.setName("Person");
		person.setCreatedDate(new Date());
		person.setDescription("This is a dummy entity");
		person.setLastUpdated(person.getCreatedDate());

		// Attribute 1
		abstractAttributeInterface = initializeDateAttribute();
		person.addAbstractAttribute(abstractAttributeInterface);
		abstractAttributeInterface.setEntity(person);

		// Attribute 2
		abstractAttributeInterface = initializeStringAttribute("gender", "Male");
		((AttributeInterface) abstractAttributeInterface).getAttributeTypeInformation()
				.setDataElement(initializeDataElement());
		person.addAbstractAttribute(abstractAttributeInterface);
		abstractAttributeInterface.setEntity(person);

		// Attribute 3
		abstractAttributeInterface = initializeStringAttribute("name", "");
		person.addAbstractAttribute(abstractAttributeInterface);
		abstractAttributeInterface.setEntity(person);
		entityGroup.addEntity(person);
		person.setEntityGroup(entityGroup);
		return person;
	}

	public EntityInterface initializeEntity1() throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface atributeInterface = null;
		EntityInterface person = createAndPopulateEntity();

		person.setName("Person");
		person.setCreatedDate(new Date());
		person.setDescription("This is a dummy entity");
		person.setLastUpdated(person.getCreatedDate());

		// Attribute 1
		atributeInterface = initializeDateAttribute();
		person.addAbstractAttribute(atributeInterface);
		atributeInterface.setEntity(person);

		// Attribute 2
		atributeInterface = initializeStringAttribute("gender", "Male");
		((AttributeInterface) atributeInterface).getAttributeTypeInformation().setDataElement(
				initializeDataElement());
		person.addAbstractAttribute(atributeInterface);
		atributeInterface.setEntity(person);

		// Attribute 3
		atributeInterface = initializeStringAttribute("name", "");
		person.addAbstractAttribute(atributeInterface);
		atributeInterface.setEntity(person);
		EntityManagerUtil.addIdAttribute(person);
		return person;
	}

	public EntityInterface initializeEntity2() throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface attributeInterface = null;
		EntityInterface bioInformation = createAndPopulateEntity();

		bioInformation.setName("BioInformation");
		bioInformation.setCreatedDate(new Date());
		bioInformation.setDescription("This is a dummy entity");
		bioInformation.setLastUpdated(bioInformation.getCreatedDate());

		// Attribute 1
		attributeInterface = initializeLongAttribute();
		bioInformation.addAbstractAttribute(attributeInterface);
		attributeInterface.setEntity(bioInformation);

		// Attribute 2
		attributeInterface = initializeDoubleAttribute();
		((AttributeInterface) attributeInterface).getAttributeTypeInformation().setDataElement(
				initializeDataElement());
		bioInformation.addAbstractAttribute(attributeInterface);
		attributeInterface.setEntity(bioInformation);
		EntityManagerUtil.addIdAttribute(bioInformation);
		return bioInformation;
	}

	public EntityInterface initializeEntity3() throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface attributeInterface = null;
		EntityInterface personal = createAndPopulateEntity();

		personal.setName("Personal");
		personal.setCreatedDate(new Date());
		personal.setDescription("This is a dummy entity");
		personal.setLastUpdated(personal.getCreatedDate());

		// Attribute 1
		attributeInterface = initializeStringAttribute("phone", "");
		((AttributeInterface) attributeInterface).getAttributeTypeInformation().setDataElement(
				initializeDataElement());
		personal.addAbstractAttribute(attributeInterface);
		attributeInterface.setEntity(personal);

		RuleInterface ruleUnique = new Rule();
		ruleUnique.setName("unique");
		attributeInterface.addRule(ruleUnique);

		// Attribute 2
		attributeInterface = initializeStringAttribute("address", "");
		personal.addAbstractAttribute(attributeInterface);
		attributeInterface.setEntity(personal);
		EntityManagerUtil.addIdAttribute(personal);

		RuleInterface ruleRequired = new Rule();
		ruleRequired.setName("required");
		attributeInterface.addRule(ruleRequired);

		return personal;
	}

	public EntityInterface initializeEntity4() throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityInterface person = createAndPopulateEntity();

		person.setName("Person");
		person.setCreatedDate(new Date());
		person.setDescription("This is a dummy entity");
		person.setLastUpdated(person.getCreatedDate());
		EntityManagerUtil.addIdAttribute(person);
		return person;
	}

	protected RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * This method creates populated dummy StringAttributeTypeInformation
	 * @param attributeName Name of the Attribute
	 * @param defaultValue Default value of the Attribute
	 * @return Manually created StringAttributeTypeInformation instance
	 */
	public AttributeInterface initializeStringAttribute(String attributeName,
			String stringDefaultValue)
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface stringAttribute = domainObjectFactory.createStringAttribute();
		StringValueInterface defaultValue = domainObjectFactory.createStringValue();

		defaultValue.setValue(stringDefaultValue);
		stringAttribute.setCreatedDate(new Date());
		stringAttribute.setDescription("This is a dummy StringAttributeTypeInformation");
		//stringAttribute.setIsCollection(new Boolean(false));
		stringAttribute.setIsIdentified(new Boolean(true));
		stringAttribute.setIsPrimaryKey(new Boolean(false));
		stringAttribute.setLastUpdated(stringAttribute.getCreatedDate());
		stringAttribute.setName(attributeName);

		StringAttributeTypeInformation stringTypeInfo = (StringAttributeTypeInformation) stringAttribute
				.getAttributeTypeInformation();

		stringTypeInfo.setDefaultValue(defaultValue);
		stringTypeInfo.setSize(new Integer(stringDefaultValue.length()));

		return stringAttribute;
	}

	/**
	 * This method creates populated dummy DateAttributeTypeInformation
	 *
	 * @return Manually created DateAttributeTypeInformation instance
	 * @throws DynamicExtensionsApplicationException On failure to create Date
	 */
	public AttributeInterface initializeDateAttribute()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface dateAttribute = domainObjectFactory.createDateAttribute();
		DateValueInterface defaultValue = domainObjectFactory.createDateValue();

		dateAttribute.setCreatedDate(new Date());
		dateAttribute.setDescription("This is a dummy DateAttributeTypeInformation");
		//dateAttribute.setIsCollection(new Boolean(false));
		dateAttribute.setIsIdentified(new Boolean(true));
		dateAttribute.setIsPrimaryKey(new Boolean(false));
		dateAttribute.setLastUpdated(dateAttribute.getCreatedDate());
		dateAttribute.setName("dateOfJoining");

		try
		{
			defaultValue.setValue(Utility
					.parseDate("11-11-1982", CommonServiceLocator.getInstance().getDatePattern()));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		dateAttribute.getAttributeTypeInformation().setDefaultValue(defaultValue);

		return dateAttribute;
	}

	/**
	 * This method creates populated dummy DateAttributeTypeInformation
	 *
	 * @return Manually created DateAttributeTypeInformation instance
	 * @throws DynamicExtensionsApplicationException On failure to create Date
	 */
	public AttributeInterface initializeDoubleAttribute()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface doubleAttribute = domainObjectFactory.createDoubleAttribute();

		doubleAttribute.setCreatedDate(new Date());
		doubleAttribute.setDescription("This is a dummy DoubleAttributeTypeInformation");
		//doubleAttribute.setIsCollection(new Boolean(false));
		doubleAttribute.setIsIdentified(new Boolean(true));
		doubleAttribute.setIsPrimaryKey(new Boolean(false));
		doubleAttribute.setLastUpdated(doubleAttribute.getCreatedDate());
		doubleAttribute.setName("Height");

		return doubleAttribute;
	}

	/**
	 * This method creates populated dummy DateAttributeTypeInformation
	 *
	 * @return Manually created DateAttributeTypeInformation instance
	 * @throws DynamicExtensionsApplicationException On failure to create Date
	 */
	public AttributeInterface initializeLongAttribute()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		AttributeInterface longAttribute = domainObjectFactory.createDoubleAttribute();

		longAttribute.setCreatedDate(new Date());
		longAttribute.setDescription("This is a dummy LongAttributeTypeInformation");
		//longAttribute.setIsCollection(new Boolean(false));
		longAttribute.setIsIdentified(new Boolean(true));
		longAttribute.setIsPrimaryKey(new Boolean(false));
		longAttribute.setLastUpdated(longAttribute.getCreatedDate());
		longAttribute.setName("Age");

		return longAttribute;
	}

	/**
	 * @param abstractAttributeInterface Assosiated Attribute of the Entity
	 * @return Manually created TextField control instance
	 */
	public ControlInterface initializeTextField(
			AbstractAttributeInterface abstractAttributeInterface)
	{
		final int COLUMNS = 50;
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		TextFieldInterface textFieldInterface = domainObjectFactory.createTextField();

		textFieldInterface.setBaseAbstractAttribute(abstractAttributeInterface);

		textFieldInterface.setColumns(new Integer(COLUMNS));
		textFieldInterface.setIsPassword(new Boolean(false));

		textFieldInterface.setCaption("Employee Name");
		textFieldInterface.setSequenceNumber(new Integer(sequence++));
		textFieldInterface.setIsHidden(new Boolean(false));
		textFieldInterface.setCssClass("formField");
		textFieldInterface.setTooltip("This is Name of the Employee.");

		return textFieldInterface;
	}

	/**
	 * @param abstractAttributeInterface Assosiated Attribute of the Entity
	 * @return Manually created TextArea control instance
	 */
	public ControlInterface initializeTextArea(AbstractAttributeInterface abstractAttributeInterface)
	{
		final int COLUMNS = 50;
		final int ROWS = 6;
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		TextAreaInterface textAreaInterface = domainObjectFactory.createTextArea();

		textAreaInterface.setBaseAbstractAttribute(abstractAttributeInterface);

		textAreaInterface.setColumns(new Integer(COLUMNS));
		textAreaInterface.setRows(new Integer(ROWS));

		textAreaInterface.setCaption("Description");
		textAreaInterface.setSequenceNumber(new Integer(sequence++));
		textAreaInterface.setIsHidden(new Boolean(false));
		textAreaInterface.setCssClass("formField");
		textAreaInterface.setTooltip("This is Description of the Employee.");

		return textAreaInterface;
	}

	/**
	 * @param abstractAttributeInterface Assosiated Attribute of the Entity
	 * @return Manually created TextArea control instance
	 */
	public ControlInterface initializeDatePicker(
			AbstractAttributeInterface abstractAttributeInterface)
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		DatePickerInterface datePickerInterface = domainObjectFactory.createDatePicker();

		datePickerInterface.setBaseAbstractAttribute(abstractAttributeInterface);

		datePickerInterface.setCaption("Date of Joining");
		datePickerInterface.setSequenceNumber(new Integer(sequence++));
		datePickerInterface.setIsHidden(new Boolean(false));
		datePickerInterface.setCssClass("formField");
		datePickerInterface.setTooltip("This is Date of Joining of the Employee.");

		return datePickerInterface;
	}

	/**
	 * @param abstractAttributeInterface Assosiated Attribute of the Entity
	 * @return Manually created TextArea control instance
	 */
	public ControlInterface initializeComboBox(AbstractAttributeInterface abstractAttributeInterface)
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		ComboBoxInterface comboBoxInterface = domainObjectFactory.createComboBox();

		comboBoxInterface.setBaseAbstractAttribute(abstractAttributeInterface);

		comboBoxInterface.setCaption("Gender");
		comboBoxInterface.setSequenceNumber(new Integer(sequence));
		comboBoxInterface.setIsHidden(new Boolean(false));
		comboBoxInterface.setCssClass("formField");
		comboBoxInterface.setTooltip("This is Gender of the Employee.");

		return comboBoxInterface;
	}

	/**
	 * @return Manually created DataElement instance
	 */
	public DataElementInterface initializeDataElement()
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		UserDefinedDEInterface userDefinedDEInterface = domainObjectFactory.createUserDefinedDE();
		//new UserDefinedDE();

		StringValueInterface stringValueInterface = null;

		/* First Value */
		stringValueInterface = domainObjectFactory.createStringValue();
		stringValueInterface.setValue("Male");
		userDefinedDEInterface.addPermissibleValue(stringValueInterface);

		/* Second Value */
		stringValueInterface = domainObjectFactory.createStringValue();
		stringValueInterface.setValue("Female");
		userDefinedDEInterface.addPermissibleValue(stringValueInterface);

		return userDefinedDEInterface;
	}

	/**
	 * This method creates the populated dummy Entity instance
	 *
	 * @return Manually created dummy Entity along with its attributes
	 * @throws DynamicExtensionsApplicationException On failure to create Entity
	 */
	public EntityGroupInterface initializeEntityGroup()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroupInterface = domainObjectFactory.createEntityGroup();
		//entityGroupInterface.setName("Test Group");
		entityGroupInterface.setName("test_" + new Double(Math.random()).toString());
		entityGroupInterface.setDescription("Test description1");
		entityGroupInterface.setVersion("1");

		EntityInterface entity1 = initializeEntity(entityGroupInterface);

		entityGroupInterface.addEntity(entity1);

		return entityGroupInterface;
	}

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 */
	public SemanticPropertyInterface initializeSemanticProperty()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		SemanticPropertyInterface semanticPropertyInterface = domainObjectFactory
				.createSemanticProperty();
		semanticPropertyInterface.setConceptCode("c1");
		semanticPropertyInterface.setTerm("t1");
		semanticPropertyInterface.setThesaurasName("th1");

		return semanticPropertyInterface;
	}

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException 
	 */
	public EntityGroupInterface initializeEntityGroupForInheritance()
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroupInterface = domainObjectFactory.createEntityGroup();
		entityGroupInterface.setName("Test Group");

		EntityInterface person = initializeEntity1();

		EntityInterface bioInformation = initializeEntity2();
		bioInformation.setParentEntity(person);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(bioInformation, false);

		EntityInterface personal = initializeEntity3();
		personal.setParentEntity(bioInformation);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(personal, false);

		entityGroupInterface.addEntity(person);
		entityGroupInterface.addEntity(bioInformation);
		entityGroupInterface.addEntity(personal);

		return entityGroupInterface;
	}

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException 
	 */
	public EntityGroupInterface initializeEntityGroupForInheritanceAndAssociation()
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroupInterface = domainObjectFactory.createEntityGroup();
		entityGroupInterface.setName("Test Group");

		EntityInterface person = initializeEntity1();

		EntityInterface bioInformation = initializeEntity2();
		bioInformation.setParentEntity(person);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(bioInformation, false);

		EntityInterface personal = initializeEntity3();

		AssociationInterface association = domainObjectFactory.createAssociation();
		association.setTargetEntity(personal);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("PersonalInformation");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "person", Cardinality.ONE,
				Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "personalInformation",
				Cardinality.ONE, Cardinality.MANY));

		bioInformation.addAssociation(association);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

		entityGroupInterface.addEntity(person);
		entityGroupInterface.addEntity(bioInformation);
		entityGroupInterface.addEntity(personal);

		return entityGroupInterface;
	}

	public EntityGroupInterface initializeEntityGroupForDependency()
			throws DynamicExtensionsApplicationException
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroupInterface = domainObjectFactory.createEntityGroup();
		entityGroupInterface.setName("Test Group");
		EntityInterface person = initializeEntity4();
		entityGroupInterface.addEntity(person);

		return entityGroupInterface;
	}

}
