package edu.wustl.metadata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.beans.NameValueBean;

public class EntityGroupName {

    private static EntityGroupManagerInterface entityManager = EntityGroupManager.getInstance();

    public void getAllEntityGroupBean(final String directoryPath) {
        final List<String> allEntGrpNames = new ArrayList<String>();
        try {
            final Collection<NameValueBean> allEntityNames = entityManager.getAllEntityGroupBeans();
            for (NameValueBean nameValueBean : allEntityNames) {
                allEntGrpNames.add(nameValueBean.getName());
            }
            writeToFile(allEntGrpNames, directoryPath);
        } catch (DynamicExtensionsSystemException e) {
            throw new RuntimeException("Error while retriving Entity Group Names", e);
        }
    }

    private void writeToFile(final List<String> allEntGrpNames, final String directoryPath) {
        final File newFile = new File(directoryPath + File.separator + "EntityGroups.txt");
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write("entity.group.name=");
            for (String entity : allEntGrpNames) {
                writer.write(entity);
                writer.write(',');
            }
            writer.close();
            newFile.renameTo(new File(directoryPath + File.separator + "EntityGroup.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error while writing Entity Groups Name to file", e);
        }
    }

    public static void main(final String[] args) {
        final EntityGroupName entityGroupName = new EntityGroupName();
        entityGroupName.getAllEntityGroupBean(args[0]);
    }

}
