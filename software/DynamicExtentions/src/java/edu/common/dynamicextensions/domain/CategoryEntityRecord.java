package edu.common.dynamicextensions.domain;

/**
 * The Class CategoryEntityRecord.
 *
 * @author mandar_shidhore
 */
public class CategoryEntityRecord extends BaseAbstractAttribute
{

	/**
     * The Constant serialVersionUID.
     */
	private static final long serialVersionUID = 4L;

	/**
     * Instantiates a new category entity record.
     *
     * @param identifier
     *            the identifier
     * @param entName
     *            the ent name
     */
	public CategoryEntityRecord(Long identifier, String entName)
	{
	    super();
		id = identifier;
		name = entName;
	}

}
