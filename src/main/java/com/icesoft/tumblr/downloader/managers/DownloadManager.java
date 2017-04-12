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
import com.icesoft.tumblr.executors.Contextable;
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
	public void stop(IContext context) {
		if(context.isRun())
		{
			context.setRun(false);
		}
		else if(queue.contains(context))
		{
			System.err.println(queue.remove(context));
		}else{
			logger.debug("not in queue");
		}
	}

	public void stopAll(){
		queue.clear();
		synchronized(contexts)
		{
			for(IContext c : contexts)
			{
				c.setRun(false);
			}
		}
		saveTasks(contexts);
	}
	public void terminate(){
		pool.shutdown();
		pool.getQueue().clear();

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
						context.setState(DownloadState.CREATE);
						contexts.add(context);
					}
				}
			}
		}
	}
	public void downloadResumeSingleTask(IContext context)
	{
		synchronized(contexts)
		{
			HttpGetWorker exec = null;
			if(!context.isRun() && contexts.contains(context) && !queue.contains(context)){
				switch(context.getState())
				{
					case CREATE:			{logger.debug("Context: " + context.getURL() + " put into run.");
											exec = new HttpGetWorker(context);
											pool.submit(exec);}
						break;
					case COMPLETE:			{logger.debug("Context: " + context.getURL() + " is already complete.");}
						break;
					case DOWNLOAD:			{logger.debug("Context: " + context.getURL() + " is downloading.");}
						break;
					case EXCEPTION:			{logger.debug("Context: " + context.getURL() + " is download exception, retry.");
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);}
						break;
					case LOCAL_QUERY:		{logger.debug("Context: " + context.getURL() + " is downloading.");}
						break;
					case NETWORK_QUERY:		{logger.debug("Context: " + context.getURL() + " is downloading.");}
						break;
					case PAUSE:				{logger.debug("Context: " + context.getURL() + " is pause, resuming.");
											context.setState(DownloadState.RESUME);
											exec = new HttpGetWorker(context);
											pool.submit(exec);}
						break;
					case RECREATE:			{logger.debug("Context: " + context.getURL() + " is pause, resuming.");
											context = recreate(context);
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);}
						break;
					case RESUME:			{logger.debug("Context: " + context.getURL() + " is pause, resuming.");
											context.setState(DownloadState.RESUME);
											exec = new HttpGetWorker(context);
											pool.submit(exec);}
						break;
					case WAIT:				{logger.debug("Context: " + context.getURL() + " is already complete.");}
						break;
					default:				{logger.debug("Context: " + context.getURL() + " is under unknow status.");}
						break;	
				}
			}			
		}
	}


	private IContext recreate(IContext context) {
		// TODO Auto-generated method stub
		return null;
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
