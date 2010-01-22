package edu.common.dynamicextensions.domain;

/**
 *
 * @author mandar_shidhore
 *
 */
public class CategoryEntityRecord extends BaseAbstractAttribute
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4L;

	/**
	 *
	 */
	public CategoryEntityRecord(Long identifier, String entName)
	{
		id = identifier;
		name = entName;
	}

}
