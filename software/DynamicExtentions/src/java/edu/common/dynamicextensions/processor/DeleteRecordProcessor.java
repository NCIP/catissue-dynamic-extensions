
package edu.common.dynamicextensions.processor;



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

}
