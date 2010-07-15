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
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;

public class PackageName {

    private static EntityGroupManagerInterface entityManager = EntityGroupManager.getInstance();
    private static final String LOGICAL_VIEW = "Logical View";
    private static final String LOGICAL_MODEL = "Logical Model";

    public static void main(final String[] args) {
        final PackageName packageName = new PackageName();
        packageName.getPackageName(args[0], args[1]);
    }

    private void getPackageName(final String directoryPath, final String xmiName) {

        String packageName = null;
        try {
            String entityName = xmiName;
            final File file = new File(entityName);
            final int indexOfExtension = file.getName().lastIndexOf('.');
            if (indexOfExtension != -1) {
                entityName = file.getName().substring(0, indexOfExtension);
            }

            final EntityGroupInterface entityGroups = entityManager.getEntityGroupByName(entityName);
            if(!entityGroups.getEntityCollection().isEmpty()){
	            final EntityInterface entity = entityGroups.getEntityCollection().iterator().next();
	            final  Set<TaggedValueInterface> taggedValues = (Set<TaggedValueInterface>) entity.getEntityGroup().getTaggedValueCollection();

	            final Iterator<TaggedValueInterface> taggedValuesIter = taggedValues.iterator();
	            while (taggedValuesIter.hasNext()) {
	                final TaggedValueInterface taggedValue = taggedValuesIter.next();
	                if (taggedValue.getKey().equals("PackageName")) {
	                    packageName = taggedValue.getValue();
	                    break;
	                }
	            }
	            final int start = packageName.lastIndexOf('.');
	            String packageEntityName = packageName;
	            if (start != -1) {
	                packageEntityName = packageName.substring(packageName.lastIndexOf('.') + 1, packageName.length());

	                final StringBuffer tempPackageName = new StringBuffer(
	                        packageName.substring(0, packageName.indexOf('.') + 1));
	                packageName = packageName.substring(packageName.indexOf('.') + 1, packageName.length());
	                if (packageName.indexOf('.') != -1) {
	                    tempPackageName.append(packageName.substring(0, packageName.indexOf('.')));
	                }
	                packageName = tempPackageName.toString().replace('.', '/');
	            }

	            writeToFile(directoryPath, packageName, entityName, packageEntityName);
            }
        } catch (DynamicExtensionsSystemException e) {
            throw new RuntimeException("Error while retriving Entity Group", e);
        } catch (DynamicExtensionsApplicationException e) {
            throw new RuntimeException("Error while retriving Entity Group", e);
        }
    }

    private void writeToFile(final String directoryPath, final String packageName, final String entityName,
                             final String packageEntityName) {
        final File newFile = new File(directoryPath + File.separator + "Package.txt");
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write("entity.name=" + entityName);
            writer.newLine();
            writer.write("de.package.name=" + getLogicalPackageName(packageName, "/"));
            writer.newLine();
            writer.write("cacore.package.name=" + packageEntityName);
            writer.close();
            newFile.renameTo(new File(directoryPath + File.separator + "Package.properties"));
        } catch (IOException e) {
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
	 * @param packageName
	 * @return
	 */
	public static String getLogicalPackageName(String packageName, String delimiter)
	{
		String logicalPackageName = LOGICAL_VIEW + delimiter + LOGICAL_MODEL;
		if(packageName!=null && packageName.contains(logicalPackageName) && packageName.length()>(logicalPackageName).length())
		{
			packageName = packageName.substring((logicalPackageName).length()+1);
		}
		else if(packageName!=null && packageName.contains(logicalPackageName) && packageName.length()==(logicalPackageName).length())
		{
			packageName = "";
		}
		return packageName;
	}
}