
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;
import java.util.Date;

import edu.common.dynamicextensions.domain.SemanticAnnotatableInterface;

/**
 * This is an interface extended by EntityInterface,EntityGroupInterface,AttributeInterface.This interface contains
 * basic information needed for each metadata objects.
 * @author sujay_narkar
 *
 */
public interface AbstractMetadataInterface extends SemanticAnnotatableInterface
{

	/**
	 * This method returns the Created Date of the AbstractMetadata.
	 * @return the createdDate of the AbstractMetadata.
	 */
	Date getCreatedDate();

	/**
	 * This method sets the Created Date of the AbstractMetadata.
	 * @param createdDate The createdDate to set.
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * This method returns the description of the AbstractMetadata.
	 * @return the description of the AbstractMetadata.
	 */
	String getDescription();

	/**
	 * This method sets the description of the AbstractMetadata.
	 * @param description The description to set.
	 */
	void setDescription(String description);

	/**
	 * This method returns the unique identifier of the AbstractMetadata.
	 * @return the identifier of the AbstractMetadata.
	 */
	Long getId();

	/**
	 * This method sets the unique identifier of the AbstractMetadata.
	 * @param identifier The identifier to set.
	 */
	void setId(Long identifier);

	/**
	 * The last updated date of metadata object.
	 * @return Returns the lastUpdated.
	 */
	Date getLastUpdated();

	/**
	 * The method sets the date of last updation of the meta data to the given date.
	 * @param lastUpdated the date to be set as last updation date.
	 */
	void setLastUpdated(Date lastUpdated);

	/**
	 * This method returns the name of the AbstractMetadata.
	 * @return the name of the AbstractMetadata.
	 */
	String getName();

	/**
	 * This method sets the name of the AbstractMetadata to the given name.
	 * @param name the name to be set.
	 */
	void setName(String name);

	/**
	 *
	 * @return
	 */
	Collection<TaggedValueInterface> getTaggedValueCollection();

	/**
	 * Setter method for taggedValueCollection
	 * @param taggedValueCollection Collection of tagged values.
	 */
	void setTaggedValueCollection(Collection<TaggedValueInterface> taggedValueCollection);

	/**
	 *
	 * @param taggedValueInterface
	 */
	void addTaggedValue(TaggedValueInterface taggedValueInterface);

	/**
	 * @return the publicId
	 */
	String getPublicId();

	/**
	 * This method stores public id of metadata object
	 * @param publicId the publicId to set
	 */
	void setPublicId(String publicId);

	/**
	 * @param semanticPropertyCollection
	 */
	void setSemanticPropertyCollection(
			Collection<SemanticPropertyInterface> semanticPropertyCollection);

	/**
	 *It will remove all the taggedValues in the given object
	 */
	void removeAllTaggedValues();

	/**
	 * This method will return the tagged value for the given key
	 * if not found will return the null.
	 * @param key key of the tag which is to be found.
	 * @return tagged value with the given key.
	 */
	String getTaggedValue(String key);
	/**
	 * Sets the activity status.
	 *
	 * @param activityStatus the new activity status
	 */
	void setActivityStatus(String activityStatus);

	/**
	 * Gets the activity status.
	 *
	 * @return the activity status
	 */
	String getActivityStatus();
}
