
package edu.common.dynamicextensions.util.parser;

/**
 * @author kunal_kamble
 * This interface defines all the keywords used in the
 * csv file used for the category creation.
 */
public interface CategoryCSVConstants
{

	/**
	 * This keyword used to mark the beginning of the new category definition.
	 */
	String FORM_DEFINITION = "Form_Definition";

	/**
	 * This keyword is used before specifying the permissible values.
	 */
	String PERMISSIBLE_VALUES = "Permissible_Values";

	/**
	 * This keyword is used before specifying the permissible values
	 * file name, where permissible values are line separated.
	 */
	String PERMISSIBLE_VALUES_FILE = "Permissible_Values_File";

	/**
	 * This keyword is used before defining the options available for a control.
	 */
	String OPTIONS = "options";
	/**
	 * This keyword is used before defining the options available for a control.
	 */
	String PERMISSIBLE_VALUE_OPTIONS = "PermVal_Options";

	/**
	 * Defines rules for a particular category attribute.
	 */
	String RULES = "Rules";

	/**
	 * Defines range for a particular category attribute.
	 */
	String RANGE = "range";

	String DATE_RANGE = "dateRange";

	/**
	 * Defines minimum value of the range.
	 */
	String MIN = "min";

	/**
	 * Defines maximum value of the range.
	 */
	String MAX = "max";

	/**
	 * Defines Uniqueness for a particular category attribute value.
	 */
	String UNIQUE = "unique";

	/**
	 * Defines mandatory condition for a particular category attribute value.
	 */
	String REQUIRED = "required";

	/**
	 * Defines for turning off future date validation.
	 */
	String ALLOW_FUTURE_DATE = "allowfuturedate";

	/**
	 * Defines for date rule validation.
	 */
	String DATE = "date";

	/**
	 * This keyword used to define the label for the form
	 */
	String DISPLAY_LABLE = "Display_Label";

	/**
	 * This keyword used to define the label for the form
	 */
	String SINGLE = "single";

	/**
	 * This keyword used to define the label for the form
	 */
	String MULTILINE = "multiline";

	/**
	 * This keyword is used to specify whether to override permissible values
	 */
	String OVERRIDE_PV = "override_pv";

	/**
	 * This keyword used to define the label for the form
	 */
	String RELATED_ATTIBUTE = "RelatedAttribute:";
	/**
	 * This keyword used to define the label for the form
	 */
	String SKIP_LOGIC_ATTIBUTE = "SkipLogicAttribute:";

	String INSTANCE = "instance:";

	/**
	 * default value of the attribute
	 */
	String DEFAULT_VALUE = "defaultValue";

	/**
	 * DE Error messages file
	 */
	String DYEXTN_ERROR_MESSAGES_FILE = "DynamicExtensionsErrorsMessages";

	String TEXT_AREA = "textArea";

	String SEPARATOR = "separator";

	String COMMON_OPTIONS = "commonOptions";
}

