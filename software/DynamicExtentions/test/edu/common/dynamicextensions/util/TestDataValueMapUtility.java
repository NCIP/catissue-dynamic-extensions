package edu.common.dynamicextensions.util;

import java.text.ParseException;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;


/**
 *
 * @author kunal_kamble
 *
 */
public class TestDataValueMapUtility extends DynamicExtensionsBaseTestCase
{

	private CategoryManagerInterface categoryManagerInterface;
	private DummyMapGenerator dummyMapGenerator;

	public TestDataValueMapUtility()
	{
		categoryManagerInterface = CategoryManager.getInstance();
		dummyMapGenerator = new DummyMapGenerator();
	}
	/**
	 * Purpose: UpdateDataValueMap method updates controls value in
	 * Entities used under same DisplayLabel
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void testUpdateDataValueMapForEntitiesUnderSameDisplayLabel() throws DynamicExtensionsSystemException, ParseException, DynamicExtensionsApplicationException
	{
		CategoryInterface liveValidationCat = categoryManagerInterface.getCategoryByName("Live Validation Form");
		Map<BaseAbstractAttributeInterface, Object> dataValueMap = dummyMapGenerator.createDataValueMapForCategory(liveValidationCat.getRootCategoryElement(), 0);

		CategoryEntityInterface rootCatEntity = liveValidationCat.getRootCategoryElement();
		ContainerInterface subForm1 = getContainerByName("Sub form 2",(ContainerInterface) rootCatEntity.getContainerCollection().iterator().next());
		ControlInterface control = getControlByCpation("Packs per day",subForm1);

		DataValueMapUtility.updateDataValueMapDataLoading(dataValueMap, (ContainerInterface) rootCatEntity.getContainerCollection().iterator().next());
		//Note that control value passed to this method is always a String array.
		DataValueMapUtility.updateDataValueMap(dataValueMap, 0, control, new String[]{"Changed Value"}, subForm1);
		Map<BaseAbstractAttributeInterface, Object> subForm1Map = getContainerDataValueMap(dataValueMap,control,0);
		assertEquals("Changed Value", subForm1Map.get(control.getAttibuteMetadataInterface()));
	}



	/**
	 * Purpose:
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void testupdateDataValueMap() throws DynamicExtensionsSystemException, ParseException, DynamicExtensionsApplicationException
	{
		CategoryInterface liveValidationCat = categoryManagerInterface.getCategoryByName("Live Validation Form");
		Map<BaseAbstractAttributeInterface, Object> dataValueMap = dummyMapGenerator.createDataValueMapForCategory(liveValidationCat.getRootCategoryElement(), 0);
		CategoryEntityInterface rootCatEntity = liveValidationCat.getRootCategoryElement();
		ContainerInterface subForm1 = getContainerByName("Sub form 1",(ContainerInterface) rootCatEntity.getContainerCollection().iterator().next());
		ControlInterface control = getControlByCpation("Duration",subForm1);
		//Note that control value passed to this method is always a String array.
		DataValueMapUtility.updateDataValueMap(dataValueMap, 0, control, new String[]{"Changed Value"}, subForm1);
		Map<BaseAbstractAttributeInterface, Object> subForm1Map = getContainerDataValueMap(dataValueMap,control,0);
		assertEquals("Changed Value", subForm1Map.get(control.getAttibuteMetadataInterface()));
	}

}
