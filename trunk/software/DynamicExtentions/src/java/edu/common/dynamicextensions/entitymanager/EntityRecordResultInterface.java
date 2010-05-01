
package edu.common.dynamicextensions.entitymanager;

import java.util.List;

/**
 * This is an interface to operate on the entity record result object.
 * @author rahul_ner
 * @author vishvesh_mulay
 *
 */
public interface EntityRecordResultInterface
{

	/**
	 * @return Returns the entityRecordList.
	 */
	List<EntityRecordInterface> getEntityRecordList();

	/**
	 * @param entityRecords The entityRecordList to set.
	 */
	void setEntityRecordList(List<EntityRecordInterface> entityRecords);

	/**
	 * @return Returns the entityRecordMetadata.
	 */
	EntityRecordMetadata getEntityRecordMetadata();

	/**
	 * @param entRecMetadata The entityRecordMetadata to set.
	 */
	void setEntityRecordMetadata(EntityRecordMetadata entRecMetadata);

}