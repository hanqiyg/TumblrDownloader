package com.icesoft.tumblr.downloader.managers;

import java.io.File;
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

import com.icesoft.tumblr.contexts.DownloadContext;
import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.downloader.service.SettingService;
import com.icesoft.tumblr.downloader.workers.HttpGetWorker;
import com.icesoft.tumblr.executors.Contextful;
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
			SettingService.getInstance().getWorkerCount(), SettingService.getInstance().getWorkerCount(), 10, TimeUnit.SECONDS, queue, threadFactory, rejecter);
	private List<IContext> contexts = new ArrayList<IContext>();

	
	private DownloadManager(){}
	public static DownloadManager getInstance(){
		return instance;
	}
	public void stopSingleTask(IContext context) {
		if(context.isRun())
		{
			context.setRun(false);
			logger.debug("stop [" + context.getURL() + "] From Running.");
		}
		else if(removeContextFromQueue(context))
		{
			context.setState(DownloadState.PAUSE);
			logger.debug("Remove [" + context.getURL() + "] From Queue.");
		}else
		{
			logger.debug("Context [" + context.getURL() + "] is not in Queue.");
		}
	}

	public boolean removeContextFromQueue(IContext context)
	{
		Object[] objs = pool.getQueue().toArray();
		System.out.println(objs.length);
		for(Object o : objs)
		{
			if(o instanceof Contextful)
			{
				System.out.println("Contextful");
				Contextful w = (Contextful) o;
				if(w.getContext().equals(context))
				{
					System.out.println("Contextful equals");
					boolean a = pool.getQueue().remove(o);
					logger.debug("Context [" + context.getURL() + "] remove from queue." + a);
					return a;
				}
			}
		}
		return false;
	}
	public boolean isContextInQueue(IContext context)
	{
		Object[] objs = pool.getQueue().toArray();
		for(Object o : objs)
		{
			if(o instanceof Contextful)
			{
				Contextful w = (Contextful) o;
				if(w.getContext().equals(context))
				{
					return true;
				}
			}
		}
		return false;
	}

	public void stopAllTask(){
		pool.getQueue().clear();
		synchronized(contexts)
		{
			for(IContext c : contexts)
			{
				c.setRun(false);
				if(
						!c.getState().equals(DownloadState.COMPLETE)
					&&	!c.getState().equals(DownloadState.EXCEPTION)
				)
				{
					c.setState(DownloadState.PAUSE);
				}
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
					context.setState(DownloadState.CREATE);
					contexts.add(context);
				}
			}
		}
	}
	
	public void startSingleTask(IContext context)
	{
		HttpGetWorker exec = null;
		if(!context.isRun() && !isContextInQueue(context)){
			switch(context.getState())
			{
				case CREATE:			{	
											logger.debug("Context: " + context.getURL() + " put into run.");
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);
										}
					break;
				case COMPLETE:			{
											logger.debug("Context: " + context.getURL() + " is already complete.");
										}
					break;
				case DOWNLOAD:			{
											logger.debug("Context: " + context.getURL() + " is downloading.");
										}
					break;
				case EXCEPTION:			{
											logger.debug("Context: " + context.getURL() + " is download exception, retry.");
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);
										}
					break;
				case LOCAL_QUERY:		{
											logger.debug("Context: " + context.getURL() + " is downloading.");
										}
					break;
				case NETWORK_QUERY:		{
											logger.debug("Context: " + context.getURL() + " is downloading.");
										}
					break;
				case PAUSE:				{
											logger.debug("Context: " + context.getURL() + " is pause, resuming.");
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);
										}
					break;
				case RESUME:			{
											logger.debug("Context: " + context.getURL() + " is pause, resuming.");
											context.setState(DownloadState.WAIT);
											exec = new HttpGetWorker(context);
											pool.submit(exec);
										}
					break;
				case WAIT:				{
											logger.debug("Context: " + context.getURL() + " is already in waiting.");
										}
					break;
				default:				{
											logger.debug("Context: " + context.getURL() + " is under unknow status.");
										}
					break;	
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
				if(!context.getState().equals(DownloadState.COMPLETE))
				{
					startSingleTask(context);
				}
			}
		}
	}
	public void addNewTask(String url,String savePath,String blogId,String blogName) {
		DownloadContext c = new DownloadContext(url,DownloadState.CREATE, savePath,blogId,blogName);
		addNewTask(c);
	}
	public void addNewTask(IContext context) {
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
	public void removeTask(IContext context,boolean removeFile){
		synchronized(contexts)
		{
			if(contexts.contains(context))
			{
				if(context.isRun()){
					context.setRun(false);
				}
				contexts.remove(context);
				H2DBService.getInstance().delete(context);
			}
		}
		if(removeFile && context.getAbsolutePath() != null){
			deleteFile(context.getAbsolutePath());
		}
	}
	public void removeAllTask(boolean removeFile){
		pool.shutdown();
		pool.getQueue().clear();

		synchronized(contexts)
		{
			for(IContext c : contexts)
			{
				c.setRun(false);
			}
		}
		try {
			pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}	
		synchronized(contexts)
		{
			Iterator<IContext> it = contexts.iterator();
			while(it.hasNext())
			{
				IContext context = it.next();
				it.remove();
				H2DBService.getInstance().delete(context);
				if(removeFile && context.getAbsolutePath() != null){
					deleteFile(context.getAbsolutePath());
				}
				H2DBService.getInstance().deleteAll();
			}
		}
		pool = new PriorityThreadPoolExecutor(
				4, 6, 10, TimeUnit.SECONDS, queue, threadFactory, rejecter);
	}
	private void deleteFile(String absolutePath) {
		File file = new File(absolutePath);
		if(file.exists() && file.isFile()){
			file.delete();
		}		
	}
	public List<IContext> getContexts() {
		return contexts;
	}
}
