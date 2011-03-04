
package edu.wustl.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.util.logger.Logger;

/**
 * The Class PackageName.
 * @author Gaurav_mehta
 */
public class PackageName
{

	/** The entity manager. */
	private static EntityGroupManagerInterface entityManager = EntityGroupManager.getInstance();

	/** The Constant LOGICAL_VIEW. */
	public static final String LOGICAL_VIEW = "Logical View";

	/** The Constant LOGICAL_MODEL. */
	public static final String LOGICAL_MODEL = "Logical Model";

	/**
	 * The main method.
	 * @param args the arguments
	 */
	public static void main(final String[] args)
	{
		final PackageName packageName = new PackageName();
		packageName.getPackageName(args[0], args[1]);
	}

	/**
	 * Gets the package name.
	 * @param directoryPath the directory path.
	 * @param xmiName the xmi name.
	 * @return the package name.
	 */
	private void getPackageName(final String directoryPath, final String xmiName)
	{

		try
		{
			String entityName = xmiName;
			final File file = new File(entityName);
			final int indexOfExtension = file.getName().lastIndexOf('.');
			if (indexOfExtension != -1)
			{
				entityName = file.getName().substring(0, indexOfExtension);
			}

			String packageName = getPackageNameFromTaggedValues(entityName);
			final int start = packageName.lastIndexOf('.');
			String packageEntityName = packageName; // NOPMD
			if (start != -1)
			{
				packageEntityName = packageName.substring(packageName.lastIndexOf('.') + 1, packageName.length());

				final StringBuffer tempPackageName = new StringBuffer(packageName.substring(0,
						packageName.indexOf('.')));
				packageName = packageName.substring(packageName.indexOf('.') + 1, packageName.length());
				if (packageName.indexOf('.') != -1)
				{
					tempPackageName.append('.');
					tempPackageName.append(packageName.substring(0, packageName.indexOf('.')));
				}
				packageName = tempPackageName.toString().replace('.', '/');
			}

			writeToFile(directoryPath, packageName, entityName, packageEntityName);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new RuntimeException("Error while retriving Entity Group", e);
		}
	}

	/**
	 * Gets the package name from tagged values.
	 * @param entityName the entity name.
	 * @return the package name from tagged values.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	private String getPackageNameFromTaggedValues(final String entityName)
			throws DynamicExtensionsSystemException
	{
		final EntityGroupInterface entityGroups = entityManager.getEntityGroupByName(entityName);
		final EntityInterface entity = entityGroups.getEntityCollection().iterator().next();
		final Set<TaggedValueInterface> taggedValues = (Set<TaggedValueInterface>) entity.getEntityGroup()
				.getTaggedValueCollection();

		String packageName = null; // NOPMD by gaurav_sawant on 9/16/10 1:20 PM
		final Iterator<TaggedValueInterface> taggedValuesIter = taggedValues.iterator();
		while (taggedValuesIter.hasNext())
		{
			final TaggedValueInterface taggedValue = taggedValuesIter.next();
			if (taggedValue.getKey().equals("PackageName"))
			{
				packageName = taggedValue.getValue();
				break;
			}
		}
		return packageName;
	}

	/**
	 * Write to file.
	 * @param directoryPath the directory path.
	 * @param packageName the package name.
	 * @param entityName the entity name.
	 * @param packageEntityName the package entity name.
	 */
	private void writeToFile(final String directoryPath, final String packageName, final String entityName,
			final String packageEntityName)
	{
		final File newFile = new File(directoryPath + File.separator + "Package.txt");
		try
		{
			final BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
			writer.write("entity.name=" + entityName);
			writer.newLine();
			writer.write("de.package.name=" + getLogicalPackageName(packageName, "/"));
			writer.newLine();
			writer.write("cacore.package.name=" + packageEntityName);
			writer.close();
            final boolean renameFlag = newFile.renameTo(new File(directoryPath
                    + File.separator + "Package.properties"));
            if (!renameFlag)
            {
                Logger.out.error("Unable to rename the file!");
            }
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error while writing Entity Groups Name to file", e);
		}
	}

	/**
	 * This method avoids duplicate logical package names added during the export XMI
	 * for e.g suppose we are importing a XMI having package as edu.annotations
	 * Then during exporting, the exporting task will append Logical View.Logical Model
	 * to edu.annotation package.
	 * After re importing and exporting it will append  Logical View.Logical Model package again
	 * to previously added package.
	 * So this method will remove this duplication.
	 *
	 * @param actualPackageName the package name.
	 * @param delimiter the delimiter.
	 * @return the logical package name.
	 */
	public static String getLogicalPackageName(final String actualPackageName, final String delimiter)
	{
		final String logicalPackageName = LOGICAL_VIEW + delimiter + LOGICAL_MODEL; // NOPMD
		String packageName = actualPackageName;

        if (packageName != null && packageName.contains(logicalPackageName))
        {
            if (packageName.length() > (logicalPackageName).length())
            {
                packageName = packageName.substring((logicalPackageName).length() + 1);
            } else if (packageName.length() == (logicalPackageName).length())
            {
                packageName = "";
            }
        }
		return packageName;
	}
}