
package edu.common.dynamicextensions.processor;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @author rahul_ner
 *
 */
public class DeleteRecordProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Default Constructor.
	 */
	protected DeleteRecordProcessor()
	{
		super();
	}

	/**
	 * @return
	 */
	public static DeleteRecordProcessor getInstance()
	{
		return new DeleteRecordProcessor();
	}

	/**
	 * @param container
	 * @param recordId
	 */
	public void deleteRecord(ContainerInterface container, Long recordIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityManager.getInstance().deleteRecord((Entity) container.getAbstractEntity(),
				recordIdentifier);
	}

}
