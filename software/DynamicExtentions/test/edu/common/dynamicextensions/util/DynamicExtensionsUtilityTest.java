
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;

public class DynamicExtensionsUtilityTest extends DynamicExtensionsBaseTestCase
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(DynamicExtensionsUtilityTest.class);

	private CategoryInterface getCategory() throws DynamicExtensionsSystemException
	{
		Long categoryIdentifier = getCategoryIdentifier();
		System.out.println("getCategory()-fetching category with id - " + categoryIdentifier);
		return CategoryManager.getInstance().getCategoryById(categoryIdentifier);
	}

	private CategoryInterface getCategoryByName(String catName) throws DynamicExtensionsSystemException
	{
		System.out.println("getCategory()-fetching category with id " + catName);
		return CategoryManager.getInstance().getCategoryByName(catName);
	}



	private Long getContainerIdentifier() throws DynamicExtensionsSystemException
	{
		ContainerInterface container = (ContainerInterface) getCategory().getRootCategoryElement()
				.getContainerCollection().iterator().next();
		return container.getId();
	}

	private Long getControlIdentifier() throws DynamicExtensionsSystemException
	{
		ContainerInterface container = (ContainerInterface) getCategory().getRootCategoryElement()
				.getContainerCollection().iterator().next();
		ControlInterface control = container.getControlCollection().iterator().next();
		return control.getId();
	}

	public void testGetClonedContainerFromCache()
	{

		try
		{
			LOGGER
					.info("-----------------Test For getting cloned Container from cache started--------------------");
			String containerId = getContainerIdentifier().toString();
			ContainerInterface patientInformationContainer = DynamicExtensionsUtility
					.getClonedContainerFromCache(containerId);
			long id = patientInformationContainer.getId();
			assertTrue(Long.valueOf(containerId).equals(id));
			LOGGER
					.info("-----------------Test For getting cloned Container from cache successful--------------------");
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER
					.error("-----------------Test For getting cloned Container failed while fecting category--------------------");
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsCacheException e)
		{
			LOGGER
					.error("-----------------Test For getting cloned Container failed while fecting category--------------------");
			e.printStackTrace();
			fail();
		}
		catch (NumberFormatException e)
		{
			LOGGER
					.error("-----------------Test For getting cloned Container failed while fecting category--------------------");
			e.printStackTrace();
			fail();
		}
	}

	public void testGetContainerByIdentifier()
	{
		try
		{
			LOGGER
					.info("-----------------Test For getting Container from cache by Identifier started--------------------");
			long containerIdentifier = getContainerIdentifier();
			ContainerInterface mainContainer = (ContainerInterface) getCategory()
					.getRootCategoryElement().getContainerCollection().iterator().next();
			ContainerInterface container = DynamicExtensionsUtility.getContainerByIdentifier(String
					.valueOf(containerIdentifier), mainContainer);
			assertTrue(mainContainer.getId().equals(container.getId()));
			LOGGER
					.info("-----------------Test For getting Container from cache by Identifier successful--------------------");
		}
		catch (Exception e)
		{
			LOGGER
					.error("-----------------Test For getting Container from cache by Identifier failed--------------------");
			e.printStackTrace();
		}
	}

	public void testGetContainerByIdentifierForChildContainer()
	{
		try
		{
			LOGGER
					.info("-----------------Test For getting Child Container from cache by Identifier and Main container started--------------------");
			long containerIdentifier = getContainerIdentifier();
			ContainerInterface mainContainer = (ContainerInterface) getCategoryByName("Test Category_Lab Information")
					.getRootCategoryElement().getContainerCollection().iterator().next();
			Collection<ControlInterface> controls = mainContainer.getAllControls();
			int i = 0;
			for (ControlInterface controlInterface : controls)
			{
				if (controlInterface instanceof AbstractContainmentControlInterface && i >= 3)
				{
					containerIdentifier = ((AbstractContainmentControl) controlInterface)
							.getContainer().getId();
					break;
				}
				i++;
			}
			ContainerInterface container = DynamicExtensionsUtility.getContainerByIdentifier(String
					.valueOf(containerIdentifier), mainContainer);
			assertFalse(mainContainer.getId().equals(container.getId()));
			LOGGER
					.info("-----------------Test For getting Child Container from cache by Identifier and Main container successful--------------------");
		}
		catch (Exception e)
		{
			LOGGER
					.error("-----------------Test For getting Child Container from cache by Identifier and Main container failed--------------------");
			e.printStackTrace();
		}
	}

	public void testGetAttributeByIdentifier()
	{
		try
		{
			LOGGER
					.info("-----------------Test For getting Attribute from cache by Identifier started--------------------");
			getCategory();
			LOGGER
					.info("-----------------Test For getting Attribute from cache by Identifier successful--------------------");
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER
					.error("-----------------Test For getting Attribute from cache by Identifier failed--------------------");
			e.printStackTrace();
		}

	}

	public void testCleanContainerControlsValue()
	{
		try
		{
			LOGGER
					.info("-----------------Test For clearing Container controls value started--------------------");
			Collection<ContainerInterface> rootContainerCollection = getCategory()
					.getRootCategoryElement().getContainerCollection();
			ContainerInterface rootContainer = rootContainerCollection.iterator().next();
			DynamicExtensionsUtility.cleanContainerControlsValue(rootContainer);
			assertTrue(checkForControlValue(rootContainer));
			LOGGER
					.info("-----------------Test For clearing Container controls value successful--------------------");
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testGetCOntainerByCaption()
	{
		try
		{
			/*FIXME There can be 2 containers with same caption. And the container of the Category fetched, might not be
			 * the container which is fetched using HQL. Hence this test case might fail.*/
			LOGGER
					.info("-----------------Test For getting container by caption started--------------------");
			System.out.println("-----------------Test For getting container by caption started--------------------");
			ContainerInterface container = (ContainerInterface) getCategoryByName("Test Category_Lab Information")
					.getRootCategoryElement().getContainerCollection().iterator().next();
			String caption = container.getCaption();
			ContainerInterface fetchedContainer = DynamicExtensionsUtility
					.getContainerByCaption(caption);
			assertTrue(container.getAbstractEntity().getName().equalsIgnoreCase(
					fetchedContainer.getAbstractEntity().getName()));
			LOGGER
					.info("-----------------Test For getting container by caption successful--------------------");
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testSortNameValueBeanList()
	{
		LOGGER
				.info("-----------------Test For sorting Name Value Bean List started--------------------");
		List<NameValueBean> nameValueBeanList = new ArrayList<NameValueBean>();
		nameValueBeanList.add(new NameValueBean("Gaurav", ""));
		nameValueBeanList.add(new NameValueBean("Pathik", ""));
		nameValueBeanList.add(new NameValueBean("Kunal", ""));
		nameValueBeanList.add(new NameValueBean("Pavan", ""));
		nameValueBeanList.add(new NameValueBean("Mosin", ""));
		nameValueBeanList.add(new NameValueBean("Mandar", ""));
		nameValueBeanList.add(new NameValueBean("Yogesh", ""));
		nameValueBeanList.add(new NameValueBean("Nitesh", ""));
		nameValueBeanList.add(new NameValueBean("Sagar", ""));
		DynamicExtensionsUtility.sortNameValueBeanListByName(nameValueBeanList);
		LOGGER
				.info("-----------------Test For sorting Name Value Bean List successful--------------------");
	}

	public void testConvertTOIntegerArray()
	{
		LOGGER
				.info("-----------------Test For converting to integer array started--------------------");
		String controlSequenceNumbers = "1,2,6,7,12,45,89,65,67,34,91,44,10,5";
		Integer[] sequenceNumbers = DynamicExtensionsUtility.convertToIntegerArray(
				controlSequenceNumbers, ",");
		assertTrue(sequenceNumbers.length == 14);
		LOGGER
				.info("-----------------Test For converting to integer array successful--------------------");
	}

	public void testGetEntityGroupByIdentifier()
	{
		LOGGER
				.info("-----------------Test For getting Entity Group By Identifier started--------------------");
		String identifier = "1";
		EntityGroupInterface testEntityGroup = DynamicExtensionsUtility
				.getEntityGroupByIdentifier(identifier);
		String entityGroupName = testEntityGroup.getShortName();
		assertTrue("TestStaticModel".equalsIgnoreCase(entityGroupName));
		LOGGER
				.info("-----------------Test For getting Entity Group By Identifier successful--------------------");
	}

	/*public void testGetControlIdentifier()
	{
		try
		{
			LOGGER.info("-----------------Test For getting Control By Identifier started--------------------");
			String identifier = getControlIdentifier().toString();
			ControlInterface pulseControl = DynamicExtensionsUtility.getControlByIdentifier(identifier);
			long id = pulseControl.getId();
			assertTrue(Long.valueOf(identifier).equals(id));
			LOGGER.info("-----------------Test For getting Control By Identifier successful--------------------");
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER
					.error("-----------------Test For getting Control By Identifier failed while fecting category--------------------");
			e.printStackTrace();
			fail();
		}
	}*/

	/**
	 * Test case for checking whether number is natural number or not.
	 */
	public void testIsNaturalNumber()
	{
		LOGGER
				.info("-----------------Test For checking whether number is natural number or not started--------------------");
		assertTrue(DynamicExtensionsUtility.isNaturalNumber("5789324"));
		LOGGER
				.info("-----------------Test For checking whether number is natural number or not successful--------------------");
	}

	/**
	 * Test case for checking whether negative number is natural number or not.
	 */
	public void testIsNaturalNumberWithNegativeNumber()
	{
		LOGGER
				.info("-----------------Test For checking whether negative number is natural number or not started--------------------");
		assertFalse(DynamicExtensionsUtility.isNaturalNumber("-5789324"));
		LOGGER
				.info("-----------------Test For checking whether negative number is natural number or not successful--------------------");
	}

	/**
	 * Test case for checking whether decimal number is natural number or not.
	 */
	public void testIsNaturalNumberWithDecimalNumber()
	{
		LOGGER
				.info("-----------------Test For checking whether decimal number is natural number or not started--------------------");
		assertTrue(DynamicExtensionsUtility.isNaturalNumber("57893.24"));
		LOGGER
				.info("-----------------Test For checking whether decimal number is natural number or not successful--------------------");
	}

	/**
	 * Test case for checking whether incorrect number is natural number or not.
	 */
	public void testIsNaturalNumberWithNotANumber()
	{
		LOGGER
				.info("-----------------Test For checking whether incorrect number is natural number or not started--------------------");
		assertFalse(DynamicExtensionsUtility.isNaturalNumber("57893.24fafasd"));
		LOGGER
				.info("-----------------Test For checking whether incorrect number is natural number or not successful--------------------");
	}

	/**
	 * Test case for checking whether String is contained in the list of Strings.
	 */
	public void testIsStringInList()
	{
		LOGGER
				.info("-----------------Test For checking whether String is present in List or not started--------------------");
		List<String> listOfStrings = new ArrayList<String>();
		listOfStrings.add("Entity");
		listOfStrings.add("EntityGroup");
		listOfStrings.add("Category");
		listOfStrings.add("CategoryEntity");
		listOfStrings.add("Attribute");
		listOfStrings.add("CategoryAttribute");
		assertTrue(DynamicExtensionsUtility.isStringInList("Entity", listOfStrings));
		LOGGER
				.info("-----------------Test For checking whether String is present in List or not successful--------------------");
	}

	/**
	 * Test case for Retrieving Entity Group by Name.
	 */
	public void testRetrieveEntityGroupAndValidateEntity()
	{
		LOGGER
				.info("-----------------Test For retrieving Entity Group By Short Name started--------------------");
		EntityGroupInterface entityGroup = DynamicExtensionsUtility
				.retrieveEntityGroup("TestStaticModel");
		assertTrue("TestStaticModel".equalsIgnoreCase(entityGroup.getShortName()));
		LOGGER
				.info("-----------------Test For retrieving Entity Group By Short Name successful--------------------");

		try
		{
			LOGGER.info("-----------------Test For validating Entity started--------------------");
			EntityInterface entity = entityGroup.getEntityCollection().iterator().next();
			DynamicExtensionsUtility.validateEntity(entity);
			LOGGER
					.info("-----------------Test For validating Entity successful--------------------");
		}
		catch (DynamicExtensionsApplicationException e)
		{
			LOGGER.error("-----------------Test For validating Entity failed--------------------");
			e.printStackTrace();
			fail();
		}
	}

	public void testIsDateValid()
	{
		LOGGER.info("-----------------Test For validating Date started--------------------");
		boolean dateValid = DynamicExtensionsUtility.isDateValid("MM-yyyy", "09-2010");
		assertTrue(dateValid);

		dateValid = DynamicExtensionsUtility.isDateValid("yyyy", "2050");
		assertTrue(dateValid);

		dateValid = DynamicExtensionsUtility.isDateValid("DD-yyyy", "9999999999999");
		assertFalse(dateValid);
		LOGGER.info("-----------------Test For validating Date successful--------------------");
	}

	public void testIsCheckBoxChecked()
	{
		LOGGER
				.info("-----------------Test For validating whether check box is checked or not started--------------------");
		boolean checkBoxChecked = DynamicExtensionsUtility.isCheckBoxChecked("1");
		assertTrue(checkBoxChecked);

		boolean checkBoxNotChecked = DynamicExtensionsUtility.isCheckBoxChecked("fads");
		assertFalse(checkBoxNotChecked);

		String checkedHTMLValue = DynamicExtensionsUtility.getCheckboxSelectionValue("1");
		assertTrue("checked".equalsIgnoreCase(checkedHTMLValue));
		LOGGER
				.info("-----------------Test For validating whether check box is checked or not successful--------------------");
	}

	private boolean checkForControlValue(ContainerInterface rootContainer)
	{
		for (ControlInterface control : rootContainer.getAllControls())
		{
			if (control instanceof AbstractContainmentControl)
			{
				ContainerInterface subContainer = ((AbstractContainmentControl) control)
						.getContainer();
				if (subContainer != null)
				{
					if (!checkForControlValue(subContainer))
					{
						return false;
					}
				}
			}
			else
			{
				if (control.getValue() != null)
				{
					return false;
				}
			}

		}
		return true;
	}

}
