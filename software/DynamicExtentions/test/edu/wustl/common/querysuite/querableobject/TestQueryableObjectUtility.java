
package edu.wustl.common.querysuite.querableobject;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

public class TestQueryableObjectUtility extends DynamicExtensionsBaseTestCase
{

	public void testGetAllCategoryEntityFromCategory()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			List<CategoryEntityInterface> catEntityColl = QueryableObjectUtility
					.getAllCategoryEntityFromCategory(category);
			if (catEntityColl == null || catEntityColl.isEmpty())
			{
				System.out
						.println("testGetAllCategoryEntityFromCategory --> No category entity fetched from the given category "
								+ category.getName());
				fail("testGetAllCategoryEntityFromCategory --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testGetAllCategoryEntityFromCategory --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testGetAllCategoryEntityFromCategory -- > Exception occured");
			e.printStackTrace();
			fail();
		}
	}

	public void testGetAllEntityFromCategory()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			Collection<EntityInterface> entityColl = QueryableObjectUtility
					.getAllEntityFromCategory(category);
			if (entityColl == null || entityColl.isEmpty())
			{
				System.out
						.println("testGetAllEntityFromCategory --> No category entity fetched from the given category "
								+ category.getName());
				fail("testGetAllEntityFromCategory --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testGetAllEntityFromCategory --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testGetAllEntityFromCategory -- > Exception occured");
			e.printStackTrace();
			fail();
		}
	}

	public void testGetAbstractAttributeCollFromCategory()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			Collection<AbstractAttributeInterface> entityColl = QueryableObjectUtility
					.getAbstractAttributesCollectionFromCategory(category);
			if (entityColl == null || entityColl.isEmpty())
			{
				System.out
						.println("testGetAbstractAttributeCollFromCategory --> No category entity fetched from the given category "
								+ category.getName());
				fail("testGetAbstractAttributeCollFromCategory --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testGetAbstractAttributeCollFromCategory --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testGetAbstractAttributeCollFromCategory -- > Exception occured");
			e.printStackTrace();
			fail();
		}
	}

	public void testGetAttributeCollFromCategory()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			Collection<AttributeInterface> entityColl = QueryableObjectUtility
					.getAttributeCollectionFromCategory(category);
			if (entityColl == null || entityColl.isEmpty())
			{
				System.out
						.println("testGetAttributeCollFromCategory --> No category entity fetched from the given category "
								+ category.getName());
				fail("testGetAttributeCollFromCategory --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testGetAttributeCollFromCategory --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testGetAttributeCollFromCategory -- > Exception occured");
			e.printStackTrace();
			fail();
		}
	}

	public void testGetAttributeCollFromCategoryEntity()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryEntityInterface catEntity = null;
			for (CategoryAssociationInterface catAssociation : category.getRootCategoryElement()
					.getCategoryAssociationCollection())
			{
				if (!catAssociation.getTargetCategoryEntity().getCategoryAttributeCollection()
						.isEmpty())
				{
					catEntity = catAssociation.getTargetCategoryEntity();
				}
			}
			Collection<AttributeInterface> entityColl = QueryableObjectUtility
					.getAttributeCollectionFromCategoryEntity(catEntity);
			if (entityColl == null || entityColl.isEmpty())
			{
				System.out
						.println("testGetAttributeCollFromCategoryEntity --> No category entity fetched from the given category "
								+ category.getName());
				fail("testGetAttributeCollFromCategoryEntity --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testGetAttributeCollFromCategoryEntity --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testGetAttributeCollFromCategoryEntity -- > Exception occured");
			e.printStackTrace();
			fail();
		}
	}

	public void testCreateQueryableAttribute()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryByName(
					"Test Category_Lab Information");
			CategoryAttributeInterface categoryAttribute = null;
			EntityInterface entity = null;
			AttributeInterface attribute = null;
			QueryableAttributeInterface queryableAttribute = null;
			QueryableAttributeInterface queryableEntityAtt = null;

			for (CategoryAssociationInterface catAssociation : category.getRootCategoryElement()
					.getCategoryAssociationCollection())
			{
				boolean isAttributeFound = false;
				for (CategoryAttributeInterface catAttribute : catAssociation
						.getTargetCategoryEntity().getCategoryAttributeCollection())
				{
					if (catAttribute.getAbstractAttribute() instanceof AttributeInterface)
					{
						entity = catAssociation.getTargetCategoryEntity().getEntity();
						categoryAttribute = catAttribute;
						isAttributeFound = true;
						attribute = (AttributeInterface) catAttribute.getAbstractAttribute();
						break;
					}
				}
				if (isAttributeFound)
				{
					queryableAttribute = QueryableObjectUtility.createQueryableAttribute(
							categoryAttribute, category);
					queryableEntityAtt = QueryableObjectUtility.createQueryableAttribute(attribute,
							entity);
					break;
				}
			}

			if (queryableAttribute == null)
			{
				System.out
						.println("testCreateQueryableAttribute --> No category entity fetched from the given category "
								+ category.getName());
				fail("testCreateQueryableAttribute --> No category entity fetched from the given category "
						+ category.getName());
			}

			if (queryableEntityAtt == null)
			{
				System.out
						.println("testCreateQueryableAttribute --> No category entity fetched from the given category "
								+ category.getName());
				fail("testCreateQueryableAttribute --> No category entity fetched from the given category "
						+ category.getName());
			}
			System.out.println("testCreateQueryableAttribute --> successFull");
		}
		catch (Exception e)
		{
			System.out.println("testCreateQueryableAttribute -- > Exception occured");
			e.printStackTrace();
			fail("testCreateQueryableAttribute -- > Exception occured" + e.getMessage());
		}
	}

	public void testGetQueryableObjectFromCache()
	{
		try
		{
			QueryableObjectInterface queryableCat = QueryableObjectUtility
					.getQueryableObjectFromCache(getCategoryIdentifier());
			if (queryableCat == null)
			{
				System.out
						.println("testGetQueryableObjectFromCache --> Failed to create the queryable Object for category.");
				fail("testGetQueryableObjectFromCache --> failed to create the queryable object for category.");
			}
			System.out.println("testGetQueryableObjectFromCache --> Successfull");
		}
		catch (Exception e)
		{
			System.out
					.println("testGetQueryableObjectFromCache --> Failed to create the queryable Object for category.");
		}
	}

	public void testGetQueryableAttributeFromCache()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			boolean isAttributeFound = false;
			CategoryAttributeInterface categoryAttribute = null;
			for (CategoryAssociationInterface catAsso : category.getRootCategoryElement()
					.getCategoryAssociationCollection())
			{
				for (CategoryAttributeInterface catAttribute : catAsso.getTargetCategoryEntity()
						.getCategoryAttributeCollection())
				{
					categoryAttribute = catAttribute;
					break;
				}
				if (isAttributeFound)
				{
					break;
				}
			}
			QueryableAttributeInterface queryableCat = QueryableObjectUtility
					.getQueryableAttributeFromCache(categoryAttribute.getId());
			if (queryableCat == null)
			{
				System.out
						.println("testGetQueryableAttributeFromCache --> Failed to create the queryable Object for category.");
				fail("testGetQueryableAttributeFromCache --> failed to create the queryable object for category.");
			}
			System.out.println("testGetQueryableAttributeFromCache --> Successfull");
		}
		catch (Exception e)
		{
			System.out
					.println("testGetQueryableAttributeFromCache --> Failed to create the queryable Object for category.");
		}
	}

	public void testGetControlCaption()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			CategoryAttributeInterface categoryAttribute = null;
			ContainerInterface container = null;
			for (CategoryAssociationInterface catAsso : category.getRootCategoryElement()
					.getCategoryAssociationCollection())
			{
				for (CategoryAttributeInterface catAttribute : catAsso.getTargetCategoryEntity()
						.getCategoryAttributeCollection())
				{
					if (!catAttribute.getIsRelatedAttribute())
					{
						categoryAttribute = catAttribute;
						container = (ContainerInterface) catAsso.getTargetCategoryEntity()
								.getContainerCollection().iterator().next();
						break;
					}
				}
			}
			String caption = QueryableObjectUtility.getControlCaption(container, categoryAttribute);
			if (caption == null || "".equals(caption))
			{
				System.out
						.println("testGetControlCaption --> Failed to create the queryable Object for category.");
				fail("testGetControlCaption --> failed to create the queryable object for category.");
			}
			System.out.println("testGetControlCaption --> Successfull");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out
					.println("testGetControlCaption --> Failed to create the queryable Object for category.");
			fail();
		}
	}

	public void testGetAttributeFromCategoryAttribute()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryByName(
					"Test Category_Pathological Annotation");

			for (CategoryAttributeInterface catAttribute : category.getRootCategoryElement()
					.getCategoryAttributeCollection())
			{
				AttributeInterface attribute = QueryableObjectUtility
						.getAttributeFromCategoryAttribute(catAttribute);
				if (attribute == null)
				{
					System.out
							.println("testGetAttributeFromCategoryAttribute--> failed. Attribute not found for category attribute :"
									+ attribute.getName());
					fail();
				}
				System.out.println("testGetAttributeFromCategoryAttribute -->successfull");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testGetAttributeFromCategoryAttribute--> failed.");
		}
		// for multiselect attribute
	}

	public void testGetQueryableObjectName()
	{
		try
		{

			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			QueryableCategory queryCat = new QueryableCategory(category);
			if (!category.getName().equals(QueryableObjectUtility.getQueryableObjectName(queryCat)))
			{
				fail("testGetQueryableObjectName--> failed, Name of the queryable object does not match with the name of category "
						+ category.getName());
			}
			System.out.println("testGetQueryableObjectName--> success");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("testGetQueryableObjectName --> failed.");
		}
	}
}
