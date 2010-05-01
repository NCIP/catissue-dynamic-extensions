
package edu.wustl.cab2b.common.util;

import java.awt.Dimension;

/**
 * Constants class for the common
 * @author gautam_shetty
 */
public interface Constants
{

	/** Metadata search configuration parameter : Used when a class name is to be included in search*/
	int CLASS = 1;

	/** Metadata search configuration parameter : Used when a attribute name is to be included in search*/
	int ATTRIBUTE = 2;

	/** Metadata search configuration parameter : Used when a class's description is to be included in search*/
	int CLASS_WITH_DESCRIPTION = 4;

	/** Metadata search configuration parameter : Used when a attribute's description is to be included in search*/
	int ATTRIBUTE_WITH_DESCRIPTION = 5;

	/** Metadata search configuration parameter : Used when a permissible value is to be included in search*/
	int PERMISSIBLEVALUE = 3;

	/** Metadata search configuration parameter : Used when a text based search is desired*/
	int BASED_ON_TEXT = 0;

	/** Metadata search configuration parameter : Used when a code based search is desired*/
	int BASED_ON_CONCEPT_CODE = 1;

	String OLD_ENTITY_ID_TAG_NAME = "original_entity_id";

	/** Tagged value key to store the name of original entity*/
	String ENTITY_DISPLAY_NAME = "original_entity_display_name";

	/**Tagged value to indicate a data category is a filtered data category*/
	String FILTERED = "filtered";

	/** Tagged value for non-search-able entities or attributes*/
	String TAGGED_VALUE_NOT_SEARCHABLE = "NOT_SEARCHABLE";
	/**
	 * Constant which represents String that will be used as URL KEY in tagged value
	 */
	String URL_KEY = "urlKey";

	/**
	 * Name of Category Entity Group
	 */
	String CATEGORY_ENTITY_GROUP_NAME = "CategoryEntityGroup";

	/**
	 * Name of Data List Entity Group to exclude from Metadata search.
	 */
	String DATALIST_ENTITY_GROUP_NAME = "DataListEntityGroup";

	/**
	 * tagged key constant to identify a category entity.
	 */
	String TYPE_CATEGORY = "Category";

	/**
	 * tagged key constant to identify whether a attribute OR an association is a derived one
	 */
	String TYPE_DERIVED = "derived";

	/**
	 * tagged key constant to identify whether a attribute OR an association is a derived one
	 */
	String ORIGINAL_ASSOCIATION_POINTER = "actualAssociationPointer";

	/**
	 * Represents the String used as field connector to connect intermediate path Ids, while generating the file.
	 */
	String CONNECTOR = "_";

	/**
	 * tagged key constant to identify cab2b entity group
	 */
	String CAB2B_ENTITY_GROUP = "caB2BEntityGroup";

	String SELECT = "-- Select --";

	//String SELECT_COLUMN = "-- Select Column --";

	String SELECTED_PATH = "Selected Path";

	String CURATED_PATH = "Curated Path";

	String GENERAL_PATH = "General Path";

	Dimension WIZARD_SIZE2_DIMENSION = new Dimension(752, 580);

	Dimension WIZARD_NAVIGATION_PANEL_DIMENSION = new Dimension(752, 36);

	String APPLY_FILTER_PANEL_NAME = "applyFilterPanel";

	public enum ChartOrientation 
	{
		ROW_AS_CATEGORY, COLUMN_AS_CATEGORY
	}
}