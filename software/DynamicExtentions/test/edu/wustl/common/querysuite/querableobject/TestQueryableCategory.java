
package edu.wustl.common.querysuite.querableobject;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

public class TestQueryableCategory extends DynamicExtensionsBaseTestCase
{

	public void testGetEntityCollection() throws DynamicExtensionsSystemException
	{
		Long catId = getCategoryIdentifier();
		CategoryInterface category = CategoryManager.getInstance().getCategoryById(catId);
		QueryableCategory queryCat = new QueryableCategory(category);
		if (queryCat.getEntityCollection().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No entity collection found for category "
					+ queryCat.getName());
		}
		if (queryCat.getAllAttributes().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No Attributes collection found for category "
					+ queryCat.getName());
		}
		if (queryCat.getAttributeCollection().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No Attributes collection found for category "
					+ queryCat.getName());
		}
		if (queryCat.getEntityAttributes().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No Attributes collection found for category "
					+ queryCat.getName());
		}
		if (queryCat.getEntityAttributesForQuery().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No Attributes collection found for category "
					+ queryCat.getName());
		}
		if (queryCat.getDescription() != null
				&& !queryCat.getDescription().equals(category.getDescription()))
		{
			fail("testGetEntityCollection --> failed. Discription does not match for the category "
					+ queryCat.getName());
		}
		if (!queryCat.getName().equals(category.getName()))
		{
			fail("testGetEntityCollection --> failed. Name does not match for the category "
					+ queryCat.getName());
		}
		if (!queryCat.getId().equals(category.getId()))
		{
			fail("testGetEntityCollection --> failed. identifier does not match for the category "
					+ queryCat.getName());
		}
		if (queryCat.getAllCategoryEntities().isEmpty())
		{
			fail("testGetEntityCollection --> failed. No Category Entities found for category "
					+ queryCat.getName());
		}
		if (!queryCat.isCategoryObject())
		{
			fail("testGetEntityCollection --> failed. QueryableObject is category still it returned"
					+ "isCategoryObject false  " + queryCat.getName());
		}
		queryCat.hashCode();
		System.out.println("testGetEntityCollection --> success");
		queryCat.getTaggedValueCollection();
	}

	public void testGetRelatedAttributeCatEntityColl() throws DynamicExtensionsSystemException
	{
		Long catId = getCategoryIdentifier();
		CategoryInterface category = CategoryManager.getInstance().getCategoryById(catId);
		QueryableCategory queryCat = new QueryableCategory(category);
		if (queryCat.getRelatedAttributeCategoryEntityCollection().size() != category
				.getRelatedAttributeCategoryEntityCollection().size()
				|| !queryCat.getRelatedAttributeCategoryEntityCollection().containsAll(
						category.getRelatedAttributeCategoryEntityCollection()))
		{
			System.out
					.println("testGetRelatedAttributeCatEntityColl --> failed, getRelatedAttributeCategoryEntityCollection of queryableCategory & category "
							+ category.getName() + " does not match");
			fail();
		}
		System.out.println("testGetRelatedAttributeCatEntityColl--> success");
	}

	public void testGetRootCategoryElement() throws DynamicExtensionsSystemException
	{
		Long catId = getCategoryIdentifier();
		CategoryInterface category = CategoryManager.getInstance().getCategoryById(catId);
		QueryableCategory queryCat = new QueryableCategory(category);
		if (!category.getRootCategoryElement().equals(queryCat.getRootCategoryElement()))
		{
			fail("testGetRootCategoryElement --> failed, rootCategoryElement of queryableCategory && category "
					+ category.getName() + " does not match.");
		}
		System.out.println("testGetRootCategoryElement--> success");
	}

	public void testGetRootQueryableObject() throws DynamicExtensionsSystemException
	{
		Long catId = getCategoryIdentifier();
		CategoryInterface category = CategoryManager.getInstance().getCategoryById(catId);
		QueryableCategory queryCat = new QueryableCategory(category);
		if (!queryCat.getRootQueryableObject().getId().equals(
				category.getRootCategoryElement().getEntity().getId()))
		{
			fail("testGetRootQueryableObject --> failed, rootCategoryElement of queryableCategory && category "
					+ category.getName() + " does not match.");
		}
		System.out.println("testGetRootQueryableObject--> success");
	}

	public void testIsTagPresent() throws DynamicExtensionsSystemException
	{
		//as there is not current way available to add the tag on category the method isTagPresent should always return false.
		Long catId = getCategoryIdentifier();
		CategoryInterface category = CategoryManager.getInstance().getCategoryById(catId);
		QueryableCategory queryCat = new QueryableCategory(category);
		if (queryCat.isTagPresent(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME))
		{
			fail("testIsTagPresent--> failed, Tag present on the category.");
		}
		if (!queryCat.getTaggedValue(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME).equals(""))
		{
			fail("testIsTagPresent--> failed, Tag present on the category.");
		}
		System.out.println("testIsTagPresent--> success");
	}

	public void testGetEntityAttributeByName() throws DynamicExtensionsSystemException
	{
		//FIXME Changed the fetching of category.
		CategoryInterface category = CategoryManager.getInstance().getCategoryByName(
				"Test Category_Lab Information");
		AttributeInterface attribute = null;
		for (CategoryAssociationInterface catAssociation : category.getRootCategoryElement()
				.getCategoryAssociationCollection())
		{
			boolean isAttributeFound = false;
			for (CategoryAttributeInterface catAttribute : catAssociation.getTargetCategoryEntity()
					.getCategoryAttributeCollection())
			{
				if (catAttribute.getAbstractAttribute() instanceof AttributeInterface)
				{
					catAssociation.getTargetCategoryEntity().getEntity();
					isAttributeFound = true;
					attribute = (AttributeInterface) catAttribute.getAbstractAttribute();
					break;
				}
			}
			if (isAttributeFound)
			{
				break;
			}
		}

		QueryableCategory queryCat = new QueryableCategory(category);
		if (queryCat.getEntityAttributeByName(attribute.getName()) == null)
		{
			fail("testGetEntityAttributeByName--> failed. Attribute with name "
					+ attribute.getName() + " Not found in the category " + queryCat.getName());
		}
		if (!queryCat.isAttributePresent(attribute.getName()))
		{
			fail("testGetEntityAttributeByName--> failed. Attribute with name "
					+ attribute.getName() + " Not found in the category " + queryCat.getName());
		}
		System.out.println("testGetEntityAttributeByName --> success");
	}

	public void testGetPath() throws DynamicExtensionsSystemException
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
		QueryableCategory queryCat = new QueryableCategory(category);
		if (queryCat.getPath(category.getRootCategoryElement(), catEntity) == null)
		{
			fail("testGetPath --> failed. No path found for the category entity "
					+ catEntity.getName() + " in category " + category.getName());
		}
		System.out.println("testGetPath --> success");
	}

	public void testQueryableCategoryInvalidConstructor()
	{
		try
		{
			new QueryableCategory(null);
			fail("testQueryableCategoryInvalidConstructor --> failed, exception should have been thrown.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out
					.println("testQueryableCategoryInvalidConstructor --> exception thrown , thus successfull.");
		}
	}

	public void testGetDiscriminatorValue()
	{
		try
		{
			CategoryInterface category = CategoryManager.getInstance().getCategoryById(
					getCategoryIdentifier());
			QueryableCategory queryCat = new QueryableCategory(category);
			try
			{
				queryCat.getDiscriminatorValue();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.getParentEntity();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.getDiscriminatorColumn();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.getEntity();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.isAbstract();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.getTableProperties();
			}
			catch (RuntimeException exception)
			{

			}
			try
			{
				queryCat.getInheritanceStrategy();
			}
			catch (RuntimeException exception)
			{

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
