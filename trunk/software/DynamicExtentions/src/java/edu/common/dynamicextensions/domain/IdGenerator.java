
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.IdGeneratorInterface;

/**
 * @author kunal_kamble
 * @hibernate.class table="dyextn_id_generator"
 * @version 1.0
 */
public class IdGenerator extends DynamicExtensionBaseDomainObject implements IdGeneratorInterface
{

	/** Default serial version id */
	private static final long serialVersionUID = 1L;

	/** identifier field. */
	//private Long id;
	/** nullable persistent field. */
	private Long nextAvailableId;

	/**
	 * @hibernate.id name="id" column="id" type="java.lang.Long"
	 * length="11" generator-class="assigned"
	 */
	public Long getId()
	{
		return this.id;
	}

	/**
	 * @param identifier id
	 */
	public void setId(Long identifier)
	{
		this.id = identifier;
	}

	/**
	 * @return next available id
	 * @hibernate.property name="nextAvailableId" type="java.lang.Long" column="next_available_id" 
	 */
	public Long getNextAvailableId()
	{
		return this.nextAvailableId;
	}

	/**
	 * @param nextAvailableId
	 *            next available id
	 */
	public void setNextAvailableId(Long nextAvailableId)
	{
		this.nextAvailableId = nextAvailableId;
	}

}
