
package edu.wustl.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.beans.NameValueBean;

/**
 * @author Gaurav_mehta
 * This class generates names of Entity Groups and writes it to a property file
 */
public class EntityGroupName
{

	private static final Logger LOGGER = Logger.getLogger(EntityGroupName.class);

	private static EntityGroupManagerInterface entityManager = EntityGroupManager.getInstance();

	public final void getAllEntityGroupBean(final String directoryPath, final String tobeRemovedEntityGroups,
			final String toBeIncludedEntityGroups)
	{
		try
		{
			final Collection<NameValueBean> allEntityNames = entityManager.getAllEntityGroupBeans();
			final List<String> allEntGrpNames = filterEntityGroups(tobeRemovedEntityGroups,
					toBeIncludedEntityGroups, allEntityNames);
			writeToFile(allEntGrpNames, directoryPath);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new RuntimeException("Error while retriving Entity Group Names", e);
		}
	}

	/**
	 * @param tobeRemovedEntityGroups
	 * @param toBeIncludedEntityGroups
	 * @param allEntityNames
	 */
	private List<String> filterEntityGroups(final String tobeRemovedEntityGroups,
			final String toBeIncludedEntityGroups, final Collection<NameValueBean> allEntityNames)
	{
		List<String> allEntGrpNames = new ArrayList<String>();

		for (NameValueBean nameValueBean : allEntityNames)
		{
			allEntGrpNames.add(nameValueBean.getName());
		}

		filterEntityGroupsToBeRemoved(tobeRemovedEntityGroups, allEntGrpNames);
		allEntGrpNames = addOnlySpecificEntityGroups(toBeIncludedEntityGroups, allEntGrpNames);

		return allEntGrpNames;
	}

	/**
	 * @param toBeIncludedEntityGroups
	 * @param allEntGrpNames
	 * @return
	 */
	private List<String> addOnlySpecificEntityGroups(final String toBeIncludedEntityGroups,
			final List<String> allEntGrpNames)
	{
		validateEntityGroupNames(toBeIncludedEntityGroups, allEntGrpNames);
		StringTokenizer tokens = new StringTokenizer(toBeIncludedEntityGroups, ",");
		if (tokens.countTokens() != 0)
		{
			allEntGrpNames.clear();
		}
		while (tokens.hasMoreTokens())
		{
			String entityGroup = tokens.nextToken();
			allEntGrpNames.add(entityGroup);
		}

		return allEntGrpNames;
	}

	/**
	 * @param tobeRemovedEntityGroups
	 * @param allEntGrpNames
	 */
	private void filterEntityGroupsToBeRemoved(final String tobeRemovedEntityGroups,
			final List<String> allEntGrpNames)
	{
		validateEntityGroupNames(tobeRemovedEntityGroups, allEntGrpNames);
		StringTokenizer tokens = new StringTokenizer(tobeRemovedEntityGroups, ",");
		while (tokens.hasMoreTokens())
		{
			String entityGroup = tokens.nextToken();
			allEntGrpNames.remove(entityGroup);
		}
	}

	/**
	 * @param listOfEntityGroups
	 * @param allEntGrpNames
	 */
	private void validateEntityGroupNames(final String listOfEntityGroups, final List<String> allEntGrpNames)
	{
		StringTokenizer tokens = new StringTokenizer(listOfEntityGroups, ",");
		while (tokens.hasMoreTokens())
		{
			String entityGroup = tokens.nextToken();
			if (!allEntGrpNames.contains(entityGroup))
			{
				LOGGER.info("The Entity Group " + entityGroup + " is not present in Database.");
				LOGGER.info("Hence it cannot be included / excluded for caCore generation");
			}
		}
	}

	/**
	 * @param allEntGrpNames
	 * @param directoryPath
	 */
	private void writeToFile(final List<String> allEntGrpNames, final String directoryPath)
	{
		final File newFile = new File(directoryPath + File.separator + "EntityGroups.txt");
		try
		{
			final BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
			writer.write("entity.group.name=");
			for (String entity : allEntGrpNames)
			{
				writer.write(entity);
				writer.write(',');
			}
			writer.close();
			newFile.renameTo(new File(directoryPath + File.separator + "EntityGroup.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error while writing Entity Groups Name to file", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final EntityGroupName entityGroupName = new EntityGroupName();
		entityGroupName.getAllEntityGroupBean(args[0], args[1], args[2]);
	}

}
