/**
 *
 */
package edu.wustl.cab2b.common.cache;

/**
 * @author gaurav_mehta
 * This is the interface which implements Runnable. This interface is being
 * implemented by CategoryFetcher and EntityGroupfetcher.
 */
public interface ThreadPoolFactory extends Runnable
{

	/** (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	void run();
}
