
package edu.common.dynamicextensions.domain;

import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.ASSOCIATION_COLUMN_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.ASSOCIATION_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.CATEGORY_TABLE_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.COLUMN_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.CONSTRAINT;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.TABLE_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.UNDERSCORE;

import java.sql.Timestamp;
import java.util.Date;

import edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintKeyProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintProperties;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domain.userinterface.CategoryAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.CheckBox;
import edu.common.dynamicextensions.domain.userinterface.ComboBox;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.DataGrid;
import edu.common.dynamicextensions.domain.userinterface.DatePicker;
import edu.common.dynamicextensions.domain.userinterface.FileUploadControl;
import edu.common.dynamicextensions.domain.userinterface.Label;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domain.userinterface.MultiSelectCheckBox;
import edu.common.dynamicextensions.domain.userinterface.RadioButton;
import edu.common.dynamicextensions.domain.userinterface.TextArea;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domain.userinterface.View;
import edu.common.dynamicextensions.domain.validationrules.Rule;
import edu.common.dynamicextensions.domain.validationrules.RuleParameter;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.ByteArrayValueInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRDEInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRValueDomainInfoInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DESQLAuditInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.FormulaInterface;
import edu.common.dynamicextensions.domaininterface.IdGeneratorInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.ObjectAttributeRecordValueInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.ShortValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DataGridInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectCheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ViewInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.util.IdGeneratorUtil;

/**
 * This is a singleton class which provides methods for generating domain
 * objects. For each domain object a create method is provided.
 *
 * @author sujay_narkar
 *
 */
public class DomainObjectFactory
{

	/**
	 * Domain Object Factory Instance
	 */
	private static DomainObjectFactory domainObjectFactory;

	/**
	 * Empty Constructor
	 */
	protected DomainObjectFactory()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method returns the instance of SegmentationDomainElementFactory.
	 *
	 * @return the instance of SegmentationDomainElementFactory.
	 */
	public static synchronized DomainObjectFactory getInstance()
	{
		if (domainObjectFactory == null)
		{
			domainObjectFactory = new DomainObjectFactory();
		}
		return domainObjectFactory;
	}

	/**
	 * Mock entity manager can be placed in the entity manager using this
	 * method.
	 *
	 * @param entityManager
	 */
	public void setInstance(DomainObjectFactory domainObjectFactory)
	{
		DomainObjectFactory.domainObjectFactory = domainObjectFactory;

	}

	/**
	 * This method creates an object of Entity.
	 *
	 * @return an instance of Entity.
	 */
	public EntityInterface createEntity()
	{
		Entity entity = new Entity();
		entity.setCreatedDate(new Date());
		entity.setLastUpdated(entity.getCreatedDate());
		entity.setTableProperties(createTableProperties());
		entity.setConsraintProperties(createConstraintPropertiesForInheritance());
		return entity;
	}

	/**
	 * This method creates an object of EntityGroup
	 *
	 * @return an instance of EntityGroup.
	 */
	public EntityGroupInterface createEntityGroup()
	{
		return new EntityGroup();
	}

	/**
	 * This method creates an object of Association
	 *
	 * @return an instance of Association.
	 */
	public AssociationInterface createAssociation()
	{
		Association association = new Association();
		association.setConstraintProperties(createConstraintProperties());
		return association;
	}

	/**
	 * This method creates an object of Role
	 *
	 * @return an instance of Role.
	 */
	public RoleInterface createRole()
	{
		return new Role();
	}

	/**
	 * This method creates an object of SemanticProperty.
	 *
	 * @return an instance of SemanticProperty.
	 */
	public SemanticPropertyInterface createSemanticProperty()
	{
		return new SemanticProperty();
	}

	/**
	 * This method creates an object of ColumnProperties.
	 *
	 * @return an instance of ColumnProperties.
	 */
	public ColumnPropertiesInterface createColumnProperties()
	{
		ColumnProperties columnProperties = new ColumnProperties();
		columnProperties
				.setName(COLUMN_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());
		return columnProperties;
	}

	/**
	 * This method creates an object of TableProperties.
	 *
	 * @return an instance of TableProperties.
	 */
	public TablePropertiesInterface createTableProperties()
	{
		TableProperties tableProperties = new TableProperties();

		tableProperties.setName(TABLE_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		return tableProperties;
	}

	/**
	 * This method creates an object of constraintKeyProperties
	 * @return an instance of constraintKeyProperties
	 */
	public ConstraintKeyPropertiesInterface createConstraintKeyProperties()
	{
		ConstraintKeyProperties cnstrKeyProp = new ConstraintKeyProperties();
		cnstrKeyProp.setTgtForiegnKeyColumnProperties(createColumnProperties());
		return cnstrKeyProp;
	}

	/**
	 * This method creates an object of constraintKeyProperties set column name to the given parameter
	 * @param columnName 
	 * @return an instance of constraintKeyProperties
	 */
	public ConstraintKeyPropertiesInterface createConstraintKeyProperties(String columnName)
	{
		ConstraintKeyProperties cnstrKeyProp = new ConstraintKeyProperties();
		cnstrKeyProp.setTgtForiegnKeyColumnProperties(createColumnProperties(columnName));
		return cnstrKeyProp;
	}

	/**
	 * This method creates constraintKeyProperties for inheritance, creates only the constraint name 
	 * @return an instance of constraintProperties
	 */
	public ConstraintPropertiesInterface createConstraintPropertiesForInheritance()
	{
		ConstraintProperties cnstrProp = new ConstraintProperties();
		cnstrProp.setConstraintName(CONSTRAINT + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());
		return cnstrProp;
	}

	/**
	 * This method creates an object of ConstraintProperties
	 *
	 * @return an instance of ConstraintProperties.
	 */
	public ConstraintPropertiesInterface createConstraintProperties()
	{
		ConstraintProperties cnstrProp = new ConstraintProperties();

		cnstrProp.setName(ASSOCIATION_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		cnstrProp
				.setSrcEntityConstraintKeyProp(createConstraintKeyProperties(ASSOCIATION_COLUMN_PREFIX
						+ UNDERSCORE + "S" + UNDERSCORE + IdGeneratorUtil.getNextUniqeId()));

		cnstrProp
				.setTgtEntityConstraintKeyProp(createConstraintKeyProperties(ASSOCIATION_COLUMN_PREFIX
						+ UNDERSCORE + "T" + UNDERSCORE + IdGeneratorUtil.getNextUniqeId()));

		cnstrProp.setConstraintName(CONSTRAINT + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		return cnstrProp;
	}

	/**
	 *
	 * @return instance of BooleanAttributeTypeInformation.
	 */
	public AttributeInterface createBooleanAttribute()
	{
		Attribute booleanAttribute = new Attribute();
		booleanAttribute.setAttributeTypeInformation(new BooleanAttributeTypeInformation());
		booleanAttribute.setColumnProperties(createColumnProperties());
		return booleanAttribute;
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public AttributeInterface createByteArrayAttribute()
	{
		Attribute byteArrayAttribute = new Attribute();
		byteArrayAttribute.setAttributeTypeInformation(new ByteArrayAttributeTypeInformation());
		byteArrayAttribute.setColumnProperties(createColumnProperties());
		return byteArrayAttribute;
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public ByteArrayValueInterface createByteArrayValue()
	{
		return new ByteArrayValue();
	}

	/**
	 *
	 * @return instance of DateAttributeTypeInformation.
	 */
	public AttributeInterface createDateAttribute()
	{
		Attribute dateAttribute = new Attribute();
		dateAttribute.setAttributeTypeInformation(new DateAttributeTypeInformation());
		dateAttribute.setColumnProperties(createColumnProperties());
		return dateAttribute;
	}

	/**
	 *
	 * @return instance of DoubleAttributeTypeInformation.
	 */
	public AttributeInterface createDoubleAttribute()
	{
		Attribute doubleAttribute = new Attribute();
		doubleAttribute.setAttributeTypeInformation(new DoubleAttributeTypeInformation());
		doubleAttribute.setColumnProperties(createColumnProperties());
		return doubleAttribute;
	}

	/**
	 *
	 * @return instance of FloatAttributeTypeInformation.
	 */
	public AttributeInterface createFloatAttribute()
	{
		Attribute floatAttribute = new Attribute();
		floatAttribute.setAttributeTypeInformation(new FloatAttributeTypeInformation());
		floatAttribute.setColumnProperties(createColumnProperties());
		return floatAttribute;
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeInterface createIntegerAttribute()
	{
		Attribute integerAttribute = new Attribute();
		integerAttribute.setAttributeTypeInformation(new IntegerAttributeTypeInformation());
		integerAttribute.setColumnProperties(createColumnProperties());
		return integerAttribute;
	}

	/**
	 *
	 * @return instance of LongAttributeTypeInformation.
	 */
	public AttributeInterface createLongAttribute()
	{
		Attribute longAttribute = new Attribute();
		longAttribute.setAttributeTypeInformation(new LongAttributeTypeInformation());
		longAttribute.setColumnProperties(createColumnProperties());
		return longAttribute;
	}

	/**
	 *
	 * @return instance of ShortAttributeTypeInformation.
	 */
	public AttributeInterface createShortAttribute()
	{
		Attribute shortAttribute = new Attribute();
		shortAttribute.setAttributeTypeInformation(new ShortAttributeTypeInformation());
		shortAttribute.setColumnProperties(createColumnProperties());
		return shortAttribute;
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeInterface createStringAttribute()
	{
		Attribute stringAttribute = new Attribute();
		stringAttribute.setAttributeTypeInformation(new StringAttributeTypeInformation());
		stringAttribute.setColumnProperties(createColumnProperties());
		return stringAttribute;
	}

	/**
	 *
	 * @return instance of BooleanAttributeTypeInformation.
	 */
	public AttributeTypeInformation createBooleanAttributeTypeInformation()
	{
		return new BooleanAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public AttributeTypeInformation createByteArrayAttributeTypeInformation()
	{
		return new ByteArrayAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of DateAttributeTypeInformation.
	 */
	public AttributeTypeInformation createDateAttributeTypeInformation()
	{
		return new DateAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of DoubleAttributeTypeInformation.
	 */
	public AttributeTypeInformation createDoubleAttributeTypeInformation()
	{
		return new DoubleAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of FloatAttributeTypeInformation.
	 */
	public AttributeTypeInformation createFloatAttributeTypeInformation()
	{
		return new FloatAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeTypeInformation createIntegerAttributeTypeInformation()
	{
		return new IntegerAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of LongAttributeTypeInformation.
	 */
	public AttributeTypeInformation createLongAttributeTypeInformation()
	{
		return new LongAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of ShortAttributeTypeInformation.
	 */
	public AttributeTypeInformation createShortAttributeTypeInformation()
	{
		return new ShortAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeTypeInformation createStringAttributeTypeInformation()
	{
		return new StringAttributeTypeInformation();
	}

	/**
	 * This method creates an object of BooleanValue.
	 *
	 * @return an instance of BooleanValue.
	 */
	public BooleanValueInterface createBooleanValue()
	{
		return new BooleanValue();
	}

	/**
	 * This method creates an object of DateValue.
	 *
	 * @return an instance of DateValue.
	 */
	public DateValueInterface createDateValue()
	{
		return new DateValue();
	}

	/**
	 * This method creates an object of DoubleValue.
	 *
	 * @return an instance of DoubleValue.
	 */
	public DoubleValueInterface createDoubleValue()
	{
		return new DoubleValue();
	}

	/**
	 * This This method creates an object of FloatValue.
	 *
	 * @return an instance of FloatValue.
	 */
	public FloatValueInterface createFloatValue()
	{
		return new FloatValue();
	}

	/**
	 * This method creates an object of IntegerValue.
	 *
	 * @return an instance of IntegerValue.
	 */
	public IntegerValueInterface createIntegerValue()
	{
		return new IntegerValue();
	}

	/**
	 * This method creates an object of LongValue.
	 *
	 * @return an instance of LongValue.
	 */
	public LongValueInterface createLongValue()
	{
		return new LongValue();
	}

	/**
	 * This method creates an object of ShortValue.
	 *
	 * @return an instance of ShortValue.
	 */
	public ShortValueInterface createShortValue()
	{
		return new ShortValue();
	}

	/**
	 * This method creates an object of StringValue.
	 *
	 * @return an instance of StringValue.
	 */
	public StringValueInterface createStringValue()
	{
		return new StringValue();
	}

	/**
	 * This method creates an object of CheckBox.
	 *
	 * @return an instance of CheckBox.
	 */
	public CheckBoxInterface createCheckBox()
	{
		return new CheckBox();
	}

	/**
	 * This method creates an object of ComboBox
	 *
	 * @return an instance of ComboBox.
	 */
	public ComboBoxInterface createComboBox()
	{
		return new ComboBox();
	}

	/**
	 * This method creates an object of Container.
	 *
	 * @return an instance of Container.
	 */
	public ContainerInterface createContainer()
	{
		return new Container();
	}

	/**
	 * This method creates an object of DataGrid.
	 *
	 * @return an instance of DataGrid.
	 */
	public DataGridInterface createDataGrid()
	{
		return new DataGrid();
	}

	/**
	 * This method creates an object of DatePicker.
	 *
	 * @return an instance of DatePicker.
	 */
	public DatePickerInterface createDatePicker()
	{
		return new DatePicker();
	}

	/**
	 * This method creates an object of ListBox.
	 *
	 * @return an instance of ListBox.
	 */
	public ListBoxInterface createListBox()
	{
		return new ListBox();
	}

	/**
	 * This method creates an object of MultiSelectCheckBox.
	 *
	 * @return an instance of ListBox.
	 */
	public MultiSelectCheckBoxInterface createMultiSelectCheckBox()
	{
		return new MultiSelectCheckBox();
	}

	
	/**
	 * This method creates an object of RadioButton.
	 *
	 * @return an instance of RadioButton.
	 */
	public RadioButtonInterface createRadioButton()
	{
		return new RadioButton();
	}

	/**
	 * This method creates an object of TextArea.
	 *
	 * @return an instance of TextArea.
	 */
	public TextAreaInterface createTextArea()
	{
		return new TextArea();
	}

	/**
	 * This method creates an object of TextField.
	 *
	 * @return an instance of TextField.
	 */
	public TextFieldInterface createTextField()
	{
		return new TextField();
	}

	/**
	 * This method creates an object of File Upload Control.
	 *
	 * @return an instance of TextField.
	 */
	public FileUploadInterface createFileUploadControl()
	{
		return new FileUploadControl();
	}

	/**
	 * This method creates an object of View.
	 *
	 * @return an instance of View.
	 */
	public ViewInterface createView()
	{
		return new View();
	}

	/**
	 * This method creates an object of Rule.
	 *
	 * @return an instance of Rule.
	 */
	public RuleInterface createRule()
	{
		return new Rule();
	}

	/**
	 * This method creates an object of RuleParameter.
	 *
	 * @return an instance of RuleParameter.
	 */
	public RuleParameterInterface createRuleParameter()
	{

		return new RuleParameter();
	}

	/**
	 * This method creates an object of CaDSRDE.
	 *
	 * @return an instance of CaDSRDE.
	 */
	public CaDSRDEInterface createCaDSRDE()
	{
		return new CaDSRDE();
	}

	/**
	 * This method creates an object of UserDefinedDE.
	 *
	 * @return an instance of UserDefinedDE.
	 */
	public UserDefinedDE createUserDefinedDE()
	{
		return new UserDefinedDE();
	}

	/**
	 * This method creates an object of UserDefinedDE.
	 *
	 * @return an instance of UserDefinedDE.
	 */
	public TaggedValueInterface createTaggedValue()
	{
		return new TaggedValue();
	}

	/**
	 *
	 * @return fileAttribute.
	 */
	public AttributeInterface createFileAttribute()
	{
		Attribute fileAttribute = new Attribute();
		fileAttribute.setAttributeTypeInformation(new FileAttributeTypeInformation());
		fileAttribute.setColumnProperties(createColumnProperties());
		return fileAttribute;
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeTypeInformation createFileAttributeTypeInformation()
	{
		return new FileAttributeTypeInformation();
	}

	/**
	 *
	 * @return
	 */
	public FileAttributeRecordValue createFileAttributeRecordValue()
	{
		return new FileAttributeRecordValue();
	}

	/**
	 * @return
	 */
	public AssociationDisplayAttributeInterface createAssociationDisplayAttribute()
	{
		return new AssociationDisplayAttribute();
	}

	/**
	 * This method creates an object of ColumnProperties.
	 *
	 * @return an instance of ColumnProperties.
	 */
	public ColumnPropertiesInterface createColumnProperties(String columnName)
	{
		ColumnPropertiesInterface columnProperties = new ColumnProperties();
		columnProperties.setName(columnName);
		return columnProperties;
	}

	/**
	 * This method creates an object of TableProperties.
	 *
	 * @return an instance of TableProperties.
	 */
	public TablePropertiesInterface createTableProperties(String tableName)
	{
		TablePropertiesInterface tableProperties = createTableProperties();
		tableProperties.setName(tableName);
		return tableProperties;
	}

	/**
	 * This method creates an object of ConstraintProperties
	 *
	 * @return an instance of ConstraintProperties.
	 */
	public ConstraintPropertiesInterface createConstraintProperties(String middleTableName)
	{
		ConstraintPropertiesInterface constraintProperties = createConstraintProperties();
		constraintProperties.setName(middleTableName);
		return constraintProperties;
	}

	/**
	 * @return
	 */
	public ContainmentAssociationControlInterface createContainmentAssociationControl()
	{

		return new ContainmentAssociationControl();
	}

	/**
	 *
	 * @return
	 */
	public CaDSRValueDomainInfoInterface createCaDSRValueDomainInfo()
	{

		return new CaDSRValueDomainInfo();
	}

	/**
	 * @return
	 */
	public AttributeInterface createObjectAttribute()
	{
		Attribute ObjectAttribute = new Attribute();
		ObjectAttribute.setAttributeTypeInformation(new ObjectAttributeTypeInformation());
		ObjectAttribute.setColumnProperties(createColumnProperties());
		return ObjectAttribute;
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeTypeInformation createObjectAttributeTypeInformation()
	{
		return new ObjectAttributeTypeInformation();
	}

	public ObjectAttributeRecordValueInterface createObjectAttributeRecordValue()
	{
		return new ObjectAttributeRecordValue();
	}

	public CategoryInterface createCategory()
	{
		return new Category();
	}

	public CategoryEntityInterface createCategoryEntity()
	{
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCreatedDate(new Date());
		categoryEntity.setLastUpdated(categoryEntity.getCreatedDate());
		categoryEntity.setTableProperties(createTableProperties(CATEGORY_TABLE_NAME_PREFIX
				+ UNDERSCORE + IdGeneratorUtil.getNextUniqeId()));
		categoryEntity.setConsraintProperties(createConstraintPropertiesForInheritance());
		return categoryEntity;
	}

	public CategoryAttributeInterface createCategoryAttribute()
	{
		CategoryAttribute categoryAttribute = new CategoryAttribute();
		categoryAttribute.setColumnProperties(createColumnProperties());
		return categoryAttribute;
	}
	public SkipLogicAttributeInterface createSkipLogicAttribute()
	{
		SkipLogicAttribute skipLogicAttribute = new SkipLogicAttribute();
		return skipLogicAttribute;
	}
	/**
	 * @return IdGeneratorInterface
	 */
	public IdGeneratorInterface createIdGenerator()
	{
		return new IdGenerator();
	}

	public PathInterface createPath()
	{
		return new Path();
	}

	public PathAssociationRelationInterface createPathAssociationRelation()
	{
		return new PathAssociationRelation();
	}

	public CategoryAssociationInterface createCategoryAssociation()
	{
		CategoryAssociation association = new CategoryAssociation();
		association.setConstraintProperties(createConstraintProperties());
		return association;
	}

	/**
	 * @return IdGeneratorInterface
	 */
	public CategoryAssociationControlInterface createCategoryAssociationControl()
	{
		return new CategoryAssociationControl();
	}

	/**
	 * @param userId
	 * @param queryExecuted
	 * @return
	 */
	public DESQLAuditInterface createDESQLAudit(Long userId, String queryExecuted)
	{
		DESQLAuditInterface audit = new DESQLAudit();
		audit.setAuditDate(new Timestamp(System.currentTimeMillis()));
		audit.setUserId(userId);
		audit.setQueryExecuted(queryExecuted);
		return audit;

	}

	/**
	 * @return
	 */
	public Label createLabelControl()
	{
		return new Label();
	}
	/**
	 * @return
	 */
	public FormulaInterface createFormula(String expression)
	{
		Formula formula = new Formula();
		formula.setExpression(expression);
		return formula;
	}
}