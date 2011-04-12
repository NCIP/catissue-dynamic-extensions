package edu.common.dynamicextensions.upgrade;


/**
 * The Class UpgradeConstants.
 */
public class UpgradeConstants
{

	/** The Constant ENTITY_GROUP_NAME. */
	public static final String ENTITY_GROUP_NAME = "test";

	/** The Constant ENTITY_GROUP_PACKAGE_NAME. */
	public static final String ENTITY_GROUP_PACKAGE_NAME = "testpackage";

	/** The Constant PACKAGE_NAME_KEY. */
	public static final String PACKAGE_NAME_KEY = "PackageName";

	/** The Constant ENTITY_NAME. */
	public static final String ENTITY_NAME = "testEntity" ;

	/** The Constant ENTITY_NAME. */
	public static final String ATTRIBUTE_NAME = "testAttribute" ;

	/** The Constant ENTITY_NAME. */
	public static final String ASSOCIATION_NAME = "testAssociation" ;

	/** The Constant ENTITY_NAME. */
	public static final String SOURCE_ROLE_NAME = "sourceRole" ;

	/** The Constant ENTITY_NAME. */
	public static final String TARGET_ROLE_NAME = "targetRole" ;

	//Regular Expressions used in Import XMI

	/** The Constant START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX. */
	public static final String START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX="^[a-z][a-z0-9][\\w\\W]*";

	/** The Constant START_WITH_UPPER_CASE_REG_EX. */
	public static final String START_WITH_UPPER_CASE_REG_EX="^[A-Z][\\w\\W]*";

	/** The Constant SPECIAL_CHARACTERS_REG_EX. */
	public static final String SPECIAL_CHARACTERS_REG_EX="[\\w]*[\\W]+[\\w\\W]*";

	/** The Constant START_WITH_NUMBER_REG_EX. */
	public static final String START_WITH_NUMBER_REG_EX="^[0-9][\\w\\W]*";


}
