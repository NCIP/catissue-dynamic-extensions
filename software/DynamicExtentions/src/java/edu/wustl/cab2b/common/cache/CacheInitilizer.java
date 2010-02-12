
package edu.wustl.cab2b.common.cache;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import edu.wustl.cab2b.common.exception.RuntimeException;

public class CacheInitilizer extends ThreadPoolExecutor implements UncaughtExceptionHandler
{
	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
	.getLogger(CacheInitilizer.class);

	private static int threadCount = 0;
	private static Boolean executionOver = false;

	public CacheInitilizer(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	/**
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
	 */
	protected void afterExecute(Runnable r, Throwable t)
	{
		super.afterExecute(r, t);
		threadCount--;
	}

	public void execute(Runnable command)
	{
		super.execute(command);
	}

	/**
	 * @return the isProcessingFinished
	 */

	protected void terminated()
	{
		super.terminated();
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r)
	{
		threadCount++;
		super.beforeExecute(t, r);
	}

	public boolean allProcessCompleted()
	{
		if (threadCount == 1)
		{
			executionOver = true;
			AbstractEntityCache.isCacheReady = true;
		}
		return executionOver;
	}

	@Override
	public List<Runnable> shutdownNow()
	{
		threadCount = 0;
		return super.shutdownNow();
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		LOGGER.error("Error occured : " + e.getMessage());
		shutdownNow();
		throw new RuntimeException("Exception encountered while creating Cache!!", e);
	}
}
