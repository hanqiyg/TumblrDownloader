package com.icesoft.tumblr.downloader.managers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
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
import com.icesoft.tumblr.executors.PriorityThreadPoolExecutor;
import com.icesoft.tumblr.handlers.RejectedExecutionHandlerImpl;
import com.icesoft.tumblr.state.ContextExecutor;
import com.icesoft.tumblr.state.DownloadContext;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	
	final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
	private ThreadFactory threadFactory = Executors.defaultThreadFactory();
	private RejectedExecutionHandlerImpl rejecter = new RejectedExecutionHandlerImpl();
	ThreadPoolExecutor pool = new PriorityThreadPoolExecutor(
			4, 6, 10, TimeUnit.SECONDS, queue, threadFactory, rejecter);
	private List<IContext> contexts = Collections.synchronizedList(new ArrayList<IContext>());
	private DownloadManager(){
		//loadTasks();
	}
	public static DownloadManager getInstance(){
		return instance;
	}
	public void stopAll(){
		pool.shutdown();
		pool.getQueue().clear();
		Iterator<IContext> it = contexts.iterator();
		while(it.hasNext()){
			IContext context = it.next();
			it.remove();
			context.setRun(false);
			//saveTask(context);
		}
		try 
		{
			 System.err.println("waiting to termination in 60 s");
		     // Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS))
		   	{
		      pool.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		      if (!pool.awaitTermination(60, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		   	}
		} catch (InterruptedException ie) 
		{
		     // (Re-)Cancel if current thread also interrupted
		     pool.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
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
	
	public void saveTask(IContext context)
	{
		H2DBService.getInstance().updateTask(context);
	}
/*	public void loadTasks()
	{
		List<DownloadTask> loads = H2DBService.getInstance().loadTask();
		for(DownloadTask task : loads){
			if(task.isComplete())
			{
				task.setState(STATE.DOWNLOAD_COMPLETE);
			}else
			{
				task.setState(STATE.QUERY_WAITING);
			}
			tasks.add(task);
		}
	}*/

	public void downloadResumeTask(IContext context){
		if(!contexts.contains(context)){
			logger.info("Task[" + context.getURL() +"] is not in task list.");
			return;
		}
		if(context.isRun()){
			logger.info("Task[" + context.getURL() +"] is already running.");
			return;
		}
		ContextExecutor exec = new ContextExecutor(context);
		contexts.add(context);
		pool.submit(exec);	
	}
	public void downloadResumeAllTask(){
		Iterator<IContext> it = contexts.iterator();
		while(it.hasNext()){
			IContext task = it.next();
			downloadResumeTask(task);
		}
	}
	public List<IContext> getContexts(){
		return contexts;
	}
	public void addNewTask(DownloadContext context) {
		ContextExecutor exec = new ContextExecutor(context);
		contexts.add(context);
		pool.submit(exec);		
	}
}
