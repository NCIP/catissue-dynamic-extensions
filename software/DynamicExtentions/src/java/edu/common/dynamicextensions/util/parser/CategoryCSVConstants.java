
package edu.common.dynamicextensions.util.parser;

/**
 * @author kunal_kamble
 * This interface defines all the keywords used in the
 * csv file used for the category creation.
 */
public class CategoryCSVConstants
{

	/**
	 * This keyword used to mark the beginning of the new category definition.
	 */
	public static final String FORM_DEFINITION = "Form_Definition";

	/**
	 * This keyword is used before specifying the permissible values.
	 */
	public static final String PERMISSIBLE_VALUES = "Permissible_Values";

	/**
	 * This keyword is used before specifying the permissible values
	 * file name, where permissible values are line separated.
	 */
	public static final String PERMISSIBLE_VALUES_FILE = "Permissible_Values_File";

	/**
	 * This keyword is used before defining the options available for a control.
	 */
	public static final String OPTIONS = "options";
	/**
	 * This keyword is used before defining the options available for a control.
	 */
	public static final String PERMISSIBLE_VALUE_OPTIONS = "PermVal_Options";

	/**
	 * Defines rules for a particular category attribute.
	 */
	public static final String RULES = "Rules";

	/**
	 * Defines range for a particular category attribute.
	 */
	public static final String RANGE = "range";

	public static final String DATE_RANGE = "dateRange";

	/**
	 * Defines minimum value of the range.
	 */
	public static final String MIN = "min";

	/**
	 * Defines maximum value of the range.
	 */
	public static final String MAX = "max";

	/**
	 * Defines Uniqueness for a particular category attribute value.
	 */
	public static final String UNIQUE = "unique";

	/**
	 * Defines mandatory condition for a particular category attribute value.
	 */
	public static final String REQUIRED = "required";

	/**
	 * Defines for turning off future date validation.
	 */
	public static final String ALLOW_FUTURE_DATE = "allowfuturedate";

	/**
	 * Defines for date rule validation.
	 */
	public static final String DATE = "date";

	/**
	 * This keyword used to define the label for the form
	 */
	public static final String DISPLAY_LABLE = "Display_Label";

	/**
	 * This keyword used to define the label for the form
	 */
	public static final String SINGLE = "single";

	/**
	 * This keyword used to define the label for the form
	 */
	public static final String MULTILINE = "multiline";

	/**
	 * This keyword is used to specify whether to override permissible values
	 */
	public static final String OVERRIDE_PV = "override_pv";

	/**
	 * This keyword used to define the label for the form
	 */
	public static final String RELATED_ATTIBUTE = "RelatedAttribute:";
	/**
	 * This keyword used to define the label for the form
	 */
	public static final String SKIP_LOGIC_ATTIBUTE = "SkipLogicAttribute:";

	public static final String INSTANCE = "instance:";

	/**
	 * default value of the attribute
	 */
	public static final String DEFAULT_VALUE = "defaultValue";

	/**
	 * DE Error messages file
	 */
	public static final String DYEXTN_ERROR_MESSAGES_FILE = "DynamicExtensionsErrorsMessages";

	public static final String TEXT_AREA = "textArea";

	public static final String SEPARATOR = "separator";

	public static final String COMMON_OPTIONS = "commonOptions";
}

