package edu.common.dynamicextensions.domain;

/**
 * The Class EntityRecord.
 *
 * @author mandar_shidhore
 */
public class EntityRecord extends AbstractAttribute
{

	/**
     * The Constant DEFAULT_ID.
     */
	private static final long DEFAULT_ID = -2L;

    /**
     * The Constant serialVersionUID.
     */
	private static final long serialVersionUID = 2L;

	/**
     * Instantiates a new entity record.
     */
	public EntityRecord()
	{
	    super();
		id = DEFAULT_ID;
	}
}
