
package edu.wustl.common.querysuite.querableobject;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

public class TestQueryableCategoryAttribute extends DynamicExtensionsBaseTestCase
{

	public void testInvalidConstructor()
	{
		try
		{
			new QueryableCategoryAttribute(null, null);
			fail("testInvalidConstructor--> failed, exception should have to be thrown but not.");
		}
		catch (Exception e)
		{
			System.out.println("testInvalidConstructor--> success.");
		}
	}

	public void testGetColumnProperties()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);

			if (!queryCatAtt.getColumnProperties().equals(
					QueryableObjectUtility.getAttributeFromCategoryAttribute(catAttribute)
							.getColumnProperties()))
			{
				fail("testGetColumnProperties--> failed, column properties does not match for category attribtue "
						+ catAttribute.getName() + " in category " + category.getName());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testGetColumnProperties-->failed, exception occured.");
		}
	}

	public void testGetDataType()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);
			if (!queryCatAtt.getDataType().equals(
					QueryableObjectUtility.getAttributeFromCategoryAttribute(catAttribute)
							.getDataType()))
			{
				fail("testGetDataType-->failed, Data type does not match for category attribute "
						+ queryCatAtt.getName());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testGetDataType-->failed, exception occured.");
		}
	}

	public void testQueryableCategoryAttOtherProperties()
	{
		try
		{
			//FIXME Testing by getting category by name.
			CategoryInterface category = CategoryManager.getInstance().getCategoryByName(
					"Test Category_Lab Information");
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);
			AttributeInterface entityAttribute = QueryableObjectUtility
					.getAttributeFromCategoryAttribute(catAttribute);
			if (!queryCatAtt.getIsIdentified().equals(entityAttribute.getIsIdentified()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, isIdentified does not match.");
			}
			if (!queryCatAtt.getIsNullable().equals(entityAttribute.getIsNullable()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, isNullable does not match.");
			}
			if (!queryCatAtt.getIsPrimaryKey().equals(entityAttribute.getIsPrimaryKey()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, isPrimaryKey does not match.");
			}
			if (!queryCatAtt.getActualEntity().getId().equals(entityAttribute.getEntity().getId()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, actual entity does not match.");
			}
			if (entityAttribute.getDescription() != null
					&& !queryCatAtt.getDescription().equals(entityAttribute.getDescription()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, description does not match.");
			}
			if (queryCatAtt.getTaggedValueCollection().size() != entityAttribute
					.getTaggedValueCollection().size())
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, tagged value collection size does not match.");
			}
			if (!queryCatAtt.getCategoryEntity().equals(catAttribute.getCategoryEntity()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, category entity does not match.");
			}
			if (!queryCatAtt.getIsRelatedAttribute().equals(catAttribute.getIsRelatedAttribute()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, is related attribute  does not match.");
			}
			if (!queryCatAtt.getIsVisible().equals(catAttribute.getIsVisible()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, is visible  does not match.");
			}
			if (queryCatAtt.isInheritedAttribute() != (DynamicExtensionsUtility
					.isInheritedTaggPresent(entityAttribute)))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, is inherited tag present  does not match.");
			}
			if (!queryCatAtt.getAttributeTypeInformation().equals(
					entityAttribute.getAttributeTypeInformation()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, Attribute type information does not match.");
			}
			if (!queryCatAtt.getRootQueryableAttribte().getId().equals(entityAttribute.getId()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, getRootQueryableAttribte does not match.");
			}
			if (!queryCatAtt.getQueryEntity().getId().equals(
					catAttribute.getCategoryEntity().getEntity().getId()))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, getQueryEntity does not match.");
			}
			String displayName = queryCatAtt.getDisplayName();
			if (displayName == null || "".equals(displayName))
			{
				fail("testQueryableCategoryAttOtherProperties--> failed, displayname not found.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testOtherProperties-->failed, exception occured.");
		}
	}

	private QueryableCategoryAttribute getQueryableCategoryAttribute(
			CategoryAttributeInterface catAttribute, CategoryInterface category)
			throws DynamicExtensionsSystemException
	{

		QueryableCategory queryCategory = new QueryableCategory(category);
		QueryableCategoryAttribute queryCatAtt = new QueryableCategoryAttribute(catAttribute,
				queryCategory);
		return queryCatAtt;
	}

	private CategoryAttributeInterface getCategoryAttribute(CategoryInterface category)
			throws DynamicExtensionsSystemException
	{

		boolean isAttributeFound = false;
		CategoryAttributeInterface categoryAttribute = null;
		for (CategoryAssociationInterface catAsso : category.getRootCategoryElement()
				.getCategoryAssociationCollection())
		{
			for (CategoryAttributeInterface catAttribute : catAsso.getTargetCategoryEntity()
					.getCategoryAttributeCollection())
			{
				if (!catAttribute.getIsRelatedAttribute())
				{
					categoryAttribute = catAttribute;
					break;
				}
			}
			if (isAttributeFound)
			{
				break;
			}
		}
		return categoryAttribute;
	}

	public void testGetDescription()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);
			if (!queryCatAtt.getDescription().equals(
					QueryableObjectUtility.getAttributeFromCategoryAttribute(catAttribute)
							.getDescription()))
			{
				fail("testGetDescription-->failed, Data Description does not match for category attribute "
						+ queryCatAtt.getName());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//FIXME uncomment this once the bug# 13431 Description tag for attributes not imported into DE tables.
			//fail("testGetDescription-->failed, exception occured.");
		}
	}

	public void testIsTagPresent()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);
			AttributeInterface atribute = QueryableObjectUtility
					.getAttributeFromCategoryAttribute(catAttribute);
			Collection<TaggedValueInterface> taggedValueColl = ((AbstractMetadataInterface) atribute)
					.getTaggedValueCollection();
			for (TaggedValueInterface tag : taggedValueColl)
			{
				assertTrue(queryCatAtt.isTagPresent(tag.getKey()));
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testGetDescription-->failed, exception occured.");
		}
	}

	public void testGetTaggedValue()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface catAttribute = getCategoryAttribute(category);
			QueryableCategoryAttribute queryCatAtt = getQueryableCategoryAttribute(catAttribute,
					category);
			AttributeInterface atribute = QueryableObjectUtility
					.getAttributeFromCategoryAttribute(catAttribute);
			Collection<TaggedValueInterface> taggedValueColl = ((AbstractMetadataInterface) atribute)
					.getTaggedValueCollection();
			for (TaggedValueInterface tag : taggedValueColl)
			{
				assertEquals(queryCatAtt.getTaggedValue(tag.getKey()), tag.getValue());
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testGetTaggedValue-->failed, exception occured.");
		}
	}
}
