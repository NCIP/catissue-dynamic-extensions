package edu.common.dynamicextensions.exception;

/**
 * @author kunal_kamble
 * This class is used for throwing all Dynamic Extensions cache related exceptions
 */
public class DynamicExtensionsCacheException extends DynamicExtensionsApplicationException
{

	/**
	 * @param message
	 */
	public DynamicExtensionsCacheException(String message)
	{
		super(message);
	}

	/**
	 * @param wrapException The wrapException to set.
	 */
	public DynamicExtensionsCacheException(String message, Exception wrapException)
	{
		super(message, wrapException);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
