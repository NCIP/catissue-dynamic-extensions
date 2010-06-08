
package edu.common.dynamicextensions.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.CategoryEntityRecord;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @author kunal_kamble
 * This class has utility methods on the data value map that are used for manipulating the map.
 */

/**
 * @author pathik_sheth
 *
 */
public final class DataValueMapUtility
{

	private static final String FOR_DATA_LOADING = "forDataLoading";
	private static final String FOR_DATA_STORING = "forDataStoring";

	/**
	 * Private constructor with no argument.
	 */
	private DataValueMapUtility()
	{
		//Empty constructor.
	}

	/**
	 * @param rootValueMap
	 * @param rootContainerInterface
	 * @param purpose
	 */

	private static void modifyDataValueMap(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface, String purpose)
	{
		for (ControlInterface control : rootContainerInterface.getControlCollection())
		{
			if (control instanceof AbstractContainmentControlInterface)
			{
				modifyMapForContainmentControl(rootValueMap, purpose, control);
			}
		}

		if (rootContainerInterface.getChildContainerCollection() != null
				&& !rootContainerInterface.getChildContainerCollection().isEmpty())
		{
			modifyValueMapForChildContainers(rootValueMap, rootContainerInterface, purpose);
		}
	}

	/**
	 * Modify data value map for child containers.
	 * @param rootValueMap data value map.
	 * @param rootContainerInterface root category.
	 * @param purpose purpose of populating the data value map.
	 */
	private static void modifyValueMapForChildContainers(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface, String purpose)
	{
		for (ContainerInterface containerInterface : rootContainerInterface
				.getChildContainerCollection())
		{
			if (rootValueMap != null)
			{

				if (FOR_DATA_LOADING.equals(purpose))
				{
					updateMap(rootContainerInterface.getAbstractEntity().getAssociation(
							containerInterface.getAbstractEntity()), rootValueMap);
				}
				else if (FOR_DATA_STORING.equals(purpose))
				{

					updateMap(rootContainerInterface.getAbstractEntity().getAssociation(
							containerInterface.getAbstractEntity()), rootValueMap,
							containerInterface);
				}
			}
		}
	}

	/**
	 * Modify data value map for containment control.
	 * @param rootValueMap root category.
	 * @param purpose purpose of populating the data value map.
	 * @param control control for which data value map is being populated.
	 */
	private static void modifyMapForContainmentControl(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap, String purpose,
			ControlInterface control)
	{
		AbstractContainmentControlInterface abstractContainmentControl = (AbstractContainmentControlInterface) control;
		if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) instanceof List)
		{
			List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
					.get(abstractContainmentControl.getBaseAbstractAttribute());
			for (Map<BaseAbstractAttributeInterface, Object> map : list)
			{
				modifyDataValueMap(map, abstractContainmentControl.getContainer(), purpose);
			}
		}
		else if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) != null)
		{
			modifyDataValueMap((Map<BaseAbstractAttributeInterface, Object>) rootValueMap
					.get(abstractContainmentControl.getBaseAbstractAttribute()),
					abstractContainmentControl.getContainer(), purpose);
		}
	}

	/**
	 * This method update the value map generated by the manager classes.
	 * This transformation of map is required for the display of controls of
	 * the different container that are under the same display label.
	 * @param rootValueMap
	 * @param rootContainerInterface
	 */
	public static void updateDataValueMapDataLoading(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface)
	{
		modifyDataValueMap(rootValueMap, rootContainerInterface, FOR_DATA_LOADING);

	}

	/**
	 * @param rootValueMap
	 * @param rootContainerInterface
	 */
	public static void updateDataValueMapForDataEntry(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface)
	{
		modifyDataValueMap(rootValueMap, rootContainerInterface, FOR_DATA_STORING);

	}

	/**
	 * This method updates map for the attribute present within same display label for data storing
	 * @param assocation
	 * @param rootValueMap
	 * @param containerInterface
	 */
	private static void updateMap(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface containerInterface)
	{
		Set<BaseAbstractAttributeInterface> set = rootValueMap.keySet();
		List<Map<BaseAbstractAttributeInterface, Object>> list = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		Map<BaseAbstractAttributeInterface, Object> map = new HashMap<BaseAbstractAttributeInterface, Object>();
		list.add(map);
		for (ControlInterface controlInterface : containerInterface.getControlCollection())
		{
			if (set.contains(controlInterface.getBaseAbstractAttribute()))
			{
				map.put(controlInterface.getBaseAbstractAttribute(), rootValueMap
						.get(controlInterface.getBaseAbstractAttribute()));
				rootValueMap.remove(controlInterface.getBaseAbstractAttribute());
			}

		}
		updateRecordIdInMap(set, containerInterface, rootValueMap, map);

		rootValueMap.put(assocation, list);
	}

	/**
	 * This method updates recordId for the attribute present within same display label in Map
	 * @param set
	 * @param containerInterface
	 * @param rootValueMap
	 * @param map
	 */
	private static void updateRecordIdInMap(Set<BaseAbstractAttributeInterface> set,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			Map<BaseAbstractAttributeInterface, Object> map)
	{
		for (Iterator iterator = set.iterator(); iterator.hasNext();)
		{
			BaseAbstractAttributeInterface baseAbstractAttrIntf = (BaseAbstractAttributeInterface) iterator
					.next();
			if (baseAbstractAttrIntf.getName().equalsIgnoreCase(
					containerInterface.getAbstractEntity().getName()))
			{
				map.put(baseAbstractAttrIntf, rootValueMap.get(baseAbstractAttrIntf));
				iterator.remove();
			}

		}
	}

	/**
	 * This method updates map for the attribute present within same display label for data loading
	 * @param assocation
	 * @param rootValueMap
	 */
	private static void updateMap(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap)
	{
		if (rootValueMap.get(assocation) != null)
		{
			if (rootValueMap.get(assocation) instanceof List)
			{
				setValueForAssosiation(assocation, rootValueMap);
			}
			else
			{
				Map<BaseAbstractAttributeInterface, Object> map = (Map<BaseAbstractAttributeInterface, Object>) rootValueMap
						.get(assocation);
				for (BaseAbstractAttributeInterface abstractAttribute : map.keySet())
				{
					rootValueMap.put(abstractAttribute, rootValueMap.get(abstractAttribute));
				}
			}
			cleanMap((List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap.get(assocation));
		}
	}

	private static void cleanMap(List<Map<BaseAbstractAttributeInterface, Object>> rootValueMap)
	{
		Iterator<Map<BaseAbstractAttributeInterface, Object>> valueMapIterator = rootValueMap.iterator();
		while(valueMapIterator.hasNext())
		{
			Map<BaseAbstractAttributeInterface, Object> valueMap = valueMapIterator.next();
			Iterator<BaseAbstractAttributeInterface> values = valueMap.keySet().iterator();
			while(values.hasNext())
			{
				BaseAbstractAttributeInterface attribute = values.next();
				if(!(attribute instanceof CategoryEntityRecord))
				{
					values.remove();
				}
			}
		}
	}

	private static void setValueForAssosiation(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap)
	{
		if (!((List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap.get(assocation))
				.isEmpty())
		{
			List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
					.get(assocation);
			Map<BaseAbstractAttributeInterface, Object> map = list.get(0);
			for (Entry<BaseAbstractAttributeInterface, Object> entryObject : map.entrySet())
			{
				rootValueMap.put(entryObject.getKey(), entryObject.getValue());
			}
		}
	}

	/**
	 *  Returned the attribute to value map for insertion.
	 * @param category Category for which attribute id map is required.
	 * @return id To Attribute Map
	 */
	public static Map<Long, BaseAbstractAttributeInterface> retriveIdToAttributeMap(
			CategoryInterface category)
	{
		CategoryEntityInterface categoryEntity = category.getRootCategoryElement();
		Map<Long, BaseAbstractAttributeInterface> categoryIdAttributeMap = new HashMap<Long, BaseAbstractAttributeInterface>();
		populateIdToAttrMapForCategory(categoryIdAttributeMap, categoryEntity);

		return categoryIdAttributeMap;

	}

	/**
	 * Generate flattered map for category.
	 * @param map id To Attribute Map.
	 * @param categoryEntity category for which flattered map is required.
	 */
	private static void populateIdToAttrMapForCategory(
			Map<Long, BaseAbstractAttributeInterface> map, CategoryEntityInterface categoryEntity)
	{
		Collection<CategoryAttributeInterface> categoryAttrCollection = categoryEntity
				.getAllCategoryAttributes();
		for (CategoryAttributeInterface catAttr : categoryAttrCollection)
		{
			if (!catAttr.getIsRelatedAttribute())
			{
				if (catAttr.getAbstractAttribute() instanceof Association)
				{
					AssociationInterface association = (AssociationInterface) catAttr
							.getAbstractAttribute();

					map.put(association.getId(), association);

					Collection<AbstractAttributeInterface> multiselectAttrColl = EntityManagerUtil
							.filterSystemAttributes(association.getTargetEntity()
									.getAllAbstractAttributes());
					Iterator<AbstractAttributeInterface> muultiSelectAttIterator = multiselectAttrColl
							.iterator();

					while (muultiSelectAttIterator.hasNext())
					{
						AttributeInterface attribute = (AttributeInterface) muultiSelectAttIterator
								.next();
						map.put(attribute.getId(), attribute);
					}
				}
				else
				{
					map.put(catAttr.getId(), catAttr);
				}
			}
		}
		processCatgeoryAssocitaionCollection(map, categoryEntity.getCategoryAssociationCollection());

	}

	/**
	 * Process category association to generate flattered map.
	 * @param map id to attribute map.
	 * @param catAssoCollection category association collection of category.
	 */
	private static void processCatgeoryAssocitaionCollection(
			Map<Long, BaseAbstractAttributeInterface> map,
			Collection<CategoryAssociationInterface> catAssoCollection)
	{
		for (CategoryAssociationInterface categoryAssociation : catAssoCollection)
		{

			CategoryEntityInterface targetCategoryEntity = categoryAssociation
					.getTargetCategoryEntity();
			if (targetCategoryEntity != null)
			{
				map.put(categoryAssociation.getId(), categoryAssociation);
				populateIdToAttrMapForCategory(map, targetCategoryEntity);
			}
		}
	}

	/**
	 * Returned the attribute to value map for insertion.
	 * @param dataValue id to value map.
	 * @param idToAttributeMap id to attribute map.
	 * @return map BaseAbstractAttributeInterface to value map.
	 * @throws DynamicExtensionsSystemException thrown if attribute not found
	 *  for any id of idToAttributeMap.
	 * @throws ParseException thrown if date could not be parsed.
	 */
	public static Map<BaseAbstractAttributeInterface, Object> getAttributeToValueMap(
			final Map<Long, Object> dataValue,
			Map<Long, BaseAbstractAttributeInterface> idToAttributeMap)
			throws DynamicExtensionsSystemException, ParseException
	{
		Map<BaseAbstractAttributeInterface, Object> attributeToValueMap = // NOPMD by gaurav_sawant
		new HashMap<BaseAbstractAttributeInterface, Object>();
		Set<java.util.Map.Entry<Long, Object>> dataValueEntrySet = dataValue.entrySet();

		for (Map.Entry<Long, Object> datavalueEntry : dataValueEntrySet)
		{
			BaseAbstractAttributeInterface attributeInterface = idToAttributeMap.get(datavalueEntry
					.getKey());
			if (attributeInterface == null)
			{
				throw new DynamicExtensionsSystemException("Invalid attribute identifier.");
			}
			else
			{
				setValuesToMap(idToAttributeMap, attributeToValueMap, attributeInterface,
						datavalueEntry);
			}
		}
		return attributeToValueMap;
	}

	/**
	 * Set values in data value map.
	 * @param idToAttributeMap id to attribute map.
	 * @param attributeToValueMap attribute to value map.
	 * @param attributeInterface attribute for which values to be set.
	 * @param datavalueEntry Entry set of id to value map.
	 * @throws DynamicExtensionsSystemException thrown if attribute not found
	 *  for any id of idToAttributeMap.
	 * @throws ParseException thrown if date could not be parsed.
	 */
	private static void setValuesToMap(Map<Long, BaseAbstractAttributeInterface> idToAttributeMap,
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			BaseAbstractAttributeInterface attributeInterface,
			Map.Entry<Long, Object> datavalueEntry) throws DynamicExtensionsSystemException,
			ParseException
	{
		if (datavalueEntry.getValue() instanceof List)
		{
			setValueForAssociation(idToAttributeMap, attributeToValueMap, attributeInterface,
					datavalueEntry);
		}
		else
		{
			setattributevalueToMap(attributeToValueMap, attributeInterface, datavalueEntry);
		}
	}

	/**
	 * Set value to associations.
	 * @param idToAttributeMap id to attribute map.
	 * @param attributeToValueMap attribute to value map.
	 * @param attributeInterface attribute for which values to be set.
	 * @param datavalueEntry Entry set of id to value map.
	 * @throws DynamicExtensionsSystemException thrown if attribute not found
	 *  for any id of idToAttributeMap.
	 * @throws ParseException thrown if date could not be parsed.
	 */
	private static void setValueForAssociation(
			Map<Long, BaseAbstractAttributeInterface> idToAttributeMap,
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			BaseAbstractAttributeInterface attributeInterface,
			Map.Entry<Long, Object> datavalueEntry) throws DynamicExtensionsSystemException,
			ParseException
	{
		List<Map<Long, Object>> dataValueList;
		List<Map<BaseAbstractAttributeInterface, Object>> newListValueList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		dataValueList = (List<Map<Long, Object>>) datavalueEntry.getValue();
		Iterator<Map<Long, Object>> dataValueListIterator = dataValueList.iterator();
		while (dataValueListIterator.hasNext())
		{
			Map<Long, Object> newDataValueMap = dataValueListIterator.next();
			Map<BaseAbstractAttributeInterface, Object> associationMap = getAttributeToValueMap(
					newDataValueMap, idToAttributeMap);
			newListValueList.add(associationMap);

		}
		attributeToValueMap.put(attributeInterface, newListValueList);
	}

	/**
	 * Set value to attribute interface.
	 * @param attributeToValueMap attribute to value map.
	 * @param attributeInterface attribute for which values to be set.
	 * @param datavalueEntry Entry set of id to value map.
	 */
	private static void setattributevalueToMap(
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			BaseAbstractAttributeInterface attributeInterface,
			Map.Entry<Long, Object> datavalueEntry)
	{
		if (datavalueEntry.getValue() instanceof java.util.Date)
		{

			String dateFormate = ((DateAttributeTypeInformation) ((AttributeInterface) ((CategoryAttributeInterface) attributeInterface)
					.getAbstractAttribute()).getAttributeTypeInformation()).getFormat();
			String format = DynamicExtensionsUtility.getDateFormat(dateFormate);
			SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
			String data = formatter.format((Date) datavalueEntry.getValue());

			attributeToValueMap.put(attributeInterface, data);
		}
		else
		{
			attributeToValueMap.put(attributeInterface, datavalueEntry.getValue());
		}
	}

	/**
	 * @param sourceContainer
	 * @param targetContainer
	 * @return
	 */
	/*
		private static CategoryAssociationInterface getAssocation(ContainerInterface sourceContainer,
				ContainerInterface targetContainer)
		{
			CategoryEntityInterface sourceCategory = (CategoryEntityInterface) sourceContainer
					.getAbstractEntity();
			CategoryAssociationInterface association = null;
			for (CategoryAssociationInterface associationInterface : sourceCategory
					.getCategoryAssociationCollection())
			{
				if (associationInterface.getTargetCategoryEntity().getName().equals(
						targetContainer.getAbstractEntity().getName()))
				{
					association = associationInterface;
				}
			}
			return association;

		}*/
}
