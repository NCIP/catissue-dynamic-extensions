package edu.wustl.cab2b.common.util;

import java.util.Comparator;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;

/**
 * This comparator class is called while sorting the entityList retrieved from metadata search.
 * It compares entity names.
 * @author deepti_shelar
 *
 */
public class AlphabeticalComparator implements Comparator<MatchedClassEntry> {

	/*
	 *  @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(MatchedClassEntry entry1, MatchedClassEntry entry2) {
		EntityInterface entity1 = entry1.getMatchedEntity();
		EntityInterface entity2 = entry2.getMatchedEntity();

		String className1 = edu.wustl.common.util.Utility.parseClassName(entity1.getName());
		String className2 = edu.wustl.common.util.Utility.parseClassName(entity2.getName());
	
		return className1.compareTo(className2);
	}
}
