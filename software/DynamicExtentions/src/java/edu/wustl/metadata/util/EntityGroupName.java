
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
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.beans.NameValueBean;

/**
 * @author Gaurav_mehta
 * This class fetches names of Entity Groups from database and writes it to a property file
 */
public class EntityGroupName
{

	/** The Constant $_EXCLUDE_ENTITYGROUP. */
	private static final String _EXCLUDE_ENTITYGROUP = "${exclude.entitygroup},"; // NOPMD

	/** The Constant $_INCLUDE_ENTITYGROUP. */
	private static final String _INCLUDE_ENTITYGROUP = "${include.entitygroup},"; // NOPMD

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(EntityGroupName.class);

	/** The entity manager. */
	private static EntityGroupManagerInterface entityManager = EntityGroupManager.getInstance();

    /**
     * Gets the all entity group bean.
     *
     * @param directoryPath
     *            the directory path
     * @param tobeRemovedEntityGroups
     *            the tobe removed entity groups
     * @param includeEntityGroups
     *            the to be included entity groups
     * @return the all entity group bean
     */
    public final void getAllEntityGroupBean(final String directoryPath,
            final String tobeRemovedEntityGroups, // NOPMD
            final String includeEntityGroups) // NOPMD
    {
        try
        {
            final Collection<NameValueBean> allEntityNames = entityManager
                    .getAllEntityGroupBeans();
            final String removeEntityGroups = tobeRemovedEntityGroups.trim(); // NOPMD
            final String includeEntityGrps = includeEntityGroups.trim();
            final List<String> allEntGrpNames = filterEntityGroups(
                    removeEntityGroups, includeEntityGrps, allEntityNames);
            writeToFile(allEntGrpNames, directoryPath);
        } catch (DynamicExtensionsSystemException e)
        {
            throw new RuntimeException(
                    "Error while retriving Entity Group Names", e);
        }
    }

	/**
	 * Filter entity groups.
	 *
	 * @param tobeRemovedEntityGroups the tobe removed entity groups
	 * @param includeEntityGroups the include entity groups
	 * @param allEntityNames the all entity names
	 *
	 * @return the list< string>
	 */
	private List<String> filterEntityGroups(final String tobeRemovedEntityGroups, // NOPMD
			final String includeEntityGroups, final Collection<NameValueBean> allEntityNames) // NOPMD
	{
		List<String> allEntGrpNames = new ArrayList<String>();

		for (NameValueBean nameValueBean : allEntityNames)
		{
			allEntGrpNames.add(nameValueBean.getName());
		}

		filterEntityGroupsToBeRemoved(tobeRemovedEntityGroups, allEntGrpNames);
		allEntGrpNames = addOnlySpecificEntityGroups(includeEntityGroups, allEntGrpNames);

		return allEntGrpNames;
	}

   

	/**
     * Adds the only specific entity groups.
     *
     * @param includeEntityGroups
     *            the include entity groups
     * @param allEntGrpNames
     *            the all entity group names
     *
     * @return the list< string>
     */
    private List<String> addOnlySpecificEntityGroups(
            final String includeEntityGroups, final List<String> allEntGrpNames) // NOPMD
    {
        final String entityGroupsList = validateEntityGroupNames(
                includeEntityGroups, allEntGrpNames);
        if (!_INCLUDE_ENTITYGROUP.equalsIgnoreCase(entityGroupsList))
        {
            final StringTokenizer tokens = new StringTokenizer(
                    entityGroupsList, ",");
            if (tokens.countTokens() != 0)
            {
                allEntGrpNames.clear();
            }
            while (tokens.hasMoreTokens())
            {
                final String entityGroup = tokens.nextToken().trim();
                allEntGrpNames.add(entityGroup);
            }
        }
        return allEntGrpNames;
    }

    /**
     * Filter entity groups to be removed.
     *
     * @param tobeRemovedEntityGroups
     *            the tobe removed entity groups
     * @param allEntGrpNames
     *            the all ent grp names
     */
    private void filterEntityGroupsToBeRemoved(
            final String tobeRemovedEntityGroups, // NOPMD
            final List<String> allEntGrpNames)
    {
        if (!_EXCLUDE_ENTITYGROUP.equalsIgnoreCase(tobeRemovedEntityGroups))
        {
            final StringTokenizer tokens = new StringTokenizer(
                    tobeRemovedEntityGroups, ",");
            while (tokens.hasMoreTokens())
            {
                final String entityGroup = tokens.nextToken().trim();
                allEntGrpNames.remove(entityGroup);
            }
        }
    }

    /**
     * Validate entity group names.
     *
     * @param listOfEntityGroups
     *            the list of entity groups
     * @param allEntGrpNames
     *            the all ent grp names
     * @return the string
     */
    private String validateEntityGroupNames(final String listOfEntityGroups, // NOPMD
            final List<String> allEntGrpNames)
    {
        final StringTokenizer tokens = new StringTokenizer(listOfEntityGroups,
                ",");
        final StringBuffer filteredEntityGroup = new StringBuffer(); // NOPMD
        while (tokens.hasMoreTokens())
        {
            final String entityGroup = tokens.nextToken().trim();
            if (allEntGrpNames.contains(entityGroup))
            {
                filteredEntityGroup.append(entityGroup);
                filteredEntityGroup.append(',');
            } else
            {
                LOGGER.info("The Entity Group " + entityGroup
                        + " is not present in Database.");
                LOGGER
                        .info("Hence it cannot be included / excluded for caCore generation");
            }
        }
        return filteredEntityGroup.toString();
    }

    /**
     * Write to file.
     *
     * @param allEntGrpNames
     *            the all ent grp names
     * @param directoryPath
     *            the directory path
     */
    private void writeToFile(final List<String> allEntGrpNames,
            final String directoryPath)
    {
        final File newFile = new File(directoryPath + File.separator
                + "EntityGroups.txt");
        try
        {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(
                    newFile));
            writer.write("entity.group.name=");
            for (String entity : allEntGrpNames)
            {
                writer.write(entity);
                writer.write(',');
            }
            writer.close();
            final boolean fileSuccess = newFile.renameTo(new File(directoryPath
                    + File.separator + "EntityGroup.properties"));
            if (!fileSuccess)
            {
                throw new IOException(
                        "Error while writing Entity Groups Name to file");
            }
        } catch (IOException e)
        {
            throw new RuntimeException(
                    "Error while writing Entity Groups Name to file", e);
        }
    }

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws DynamicExtensionsApplicationException 
	 */
	public static void main(final String[] args) throws DynamicExtensionsApplicationException
	{
		final EntityGroupName entityGroupName = new EntityGroupName();
		entityGroupName.getAllEntityGroupBean(args[0], args[1], args[2]);
	}

	
}
