package com.icesoft.tumblr.downloader.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.downloader.workers.HttpGetWorker;
import com.icesoft.tumblr.executors.PriorityThreadPoolExecutor;
import com.icesoft.tumblr.handlers.RejectedExecutionHandlerImpl;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	
	final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
	private ThreadFactory threadFactory = Executors.defaultThreadFactory();
	private RejectedExecutionHandlerImpl rejecter = new RejectedExecutionHandlerImpl();
	private ThreadPoolExecutor pool = new PriorityThreadPoolExecutor(
			4, 6, 10, TimeUnit.SECONDS, queue, threadFactory, rejecter);
	private List<IContext> contexts = new ArrayList<IContext>();

	
	private DownloadManager(){
		loadTasks();
	}
	public static DownloadManager getInstance(){
		return instance;
	}
	public void stopAll(){
		pool.shutdown();
		queue.clear();
		synchronized(contexts)
		{
			for(IContext c : contexts)
			{
				c.setRun(false);
			}
		}
		saveTasks(contexts);		
		try {
			pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}		
	}
	public void stopNow(){
		pool.shutdownNow();
	}
	public ThreadPoolExecutor getPool(){
		return pool;
	}
/*	public ThreadInfo[] getThreadsInfo(){
		ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
		long[] threadIds = mxBean.getAllThreadIds();
		return mxBean.getThreadInfo(threadIds);
	}*/
	public void saveTasks(List<IContext> contexts)
	{
		H2DBService.getInstance().updateContexts(contexts);
	}
	public void saveTask(IContext context)
	{
		H2DBService.getInstance().updateContext(context);
	}
	public void loadTasks()
	{
		List<IContext> loads = H2DBService.getInstance().loadTask();
		for(IContext context : loads)
		{
			synchronized(contexts)
			{
				if(!contexts.contains(context))
				{
					if(
							!context.getState().equals(DownloadState.EXCEPTION)
						&&	!context.getState().equals(DownloadState.COMPLETE)
					)
					{
						context.setState(DownloadState.WAIT);
						contexts.add(context);
					}
				}
			}
		}
	}


	public void downloadResumeAllTasks()
	{
		synchronized(contexts)
		{
			Iterator<IContext> it = contexts.iterator();
			while(it.hasNext())
			{
				IContext context = it.next();
				DownloadState state = context.getState();
				if
				(
						!state.equals(DownloadState.COMPLETE) 
					&&	!state.equals(DownloadState.EXCEPTION)
				)
				{
					HttpGetWorker exec = new HttpGetWorker(context);
					pool.submit(exec);	
				}
			}	
		}
	}
	public void downloadResumeSingleTask(IContext context)
	{
		synchronized(contexts)
		{
			if(contexts.contains(context))
			{
				HttpGetWorker exec = new HttpGetWorker(context);
				pool.submit(exec);
			}
		}
	}
	public void addNewTask(IContext context) {
		if(context.isRun()){
			logger.debug("Context:[" + context.getURL() +"] is already in running.");
			return;
		}
		synchronized(contexts)
		{
			if(contexts.contains(context))
			{
				logger.debug("Context:[" + context.getURL() +"] is already in waiting queue.");
				return;
			}else
			{
				contexts.add(context);
			}
		}
		H2DBService.getInstance().initTask(context);

		HttpGetWorker exec = new HttpGetWorker(context);
		pool.submit(exec);
	}
	public List<IContext> getContexts() {
		return contexts;
	}
}
