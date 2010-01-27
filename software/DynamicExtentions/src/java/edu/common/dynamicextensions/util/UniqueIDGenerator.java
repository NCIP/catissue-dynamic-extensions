/**
 *
 */

package edu.common.dynamicextensions.util;

/**
 * @author chetan_patil
 *
 */
public class UniqueIDGenerator
{

	private static long identifier = 0;

	public static Long getId()
	{
		return ++identifier;
	}
}
