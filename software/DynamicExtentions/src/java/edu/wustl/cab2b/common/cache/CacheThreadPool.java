
package edu.wustl.cab2b.common.cache;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.wustl.cab2b.server.cache.EntityCache;

public class CacheThreadPool extends ThreadPoolExecutor
{
	private static int threadCount = 0;

	private static Boolean executionOver = false;

	private int numberOfEntityGroup;

	public CacheThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	/**
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
	 */
	protected void afterExecute(Runnable runnable, Throwable throwable)
	{
		super.afterExecute(runnable, throwable);
		threadCount--;
	}

	@Override
	protected void beforeExecute(Thread thread, Runnable runnable)
	{
		threadCount++;
		super.beforeExecute(thread, runnable);
	}

	public boolean allProcessCompleted()
	{
		Collection<EntityGroupInterface> entityGroupCollection = EntityCache.getInstance()
				.getEntityGroups();
		if (entityGroupCollection != null && numberOfEntityGroup == entityGroupCollection.size()
				&& threadCount == 0)
		{
			executionOver = true;
			AbstractEntityCache.isCacheReady = true;
		}
		return executionOver;
	}

	public int getNumberOfEntityGroup()
	{
		return numberOfEntityGroup;
	}

	public void setNumberOfEntityGroup(int numberOfEntityGroup)
	{
		this.numberOfEntityGroup = numberOfEntityGroup;
	}
}
