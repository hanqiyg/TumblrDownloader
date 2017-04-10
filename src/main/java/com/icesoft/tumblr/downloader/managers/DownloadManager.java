package com.icesoft.tumblr.downloader.managers;

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
import com.icesoft.tumblr.downloader.tablemodel.DownloadModel;
import com.icesoft.tumblr.executors.PriorityThreadPoolExecutor;
import com.icesoft.tumblr.handlers.RejectedExecutionHandlerImpl;
import com.icesoft.tumblr.state.ContextExecutor;
import com.icesoft.tumblr.state.interfaces.IContext;

public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	
	final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
	private ThreadFactory threadFactory = Executors.defaultThreadFactory();
	private RejectedExecutionHandlerImpl rejecter = new RejectedExecutionHandlerImpl();
	private ThreadPoolExecutor pool = new PriorityThreadPoolExecutor(
			4, 6, 10, TimeUnit.SECONDS, queue, threadFactory, rejecter);
	private List<IContext> runnings = Collections.synchronizedList(new ArrayList<IContext>());
	private List<IContext> waitings = Collections.synchronizedList(new ArrayList<IContext>());
	private List<IContext> completes = Collections.synchronizedList(new ArrayList<IContext>());
	private List<IContext> exceptions = Collections.synchronizedList(new ArrayList<IContext>());
	
	private DownloadModel model;
	private DownloadManager(){
		loadTasks();
	}
	public static DownloadManager getInstance(){
		return instance;
	}

	public void setDataModel(DownloadModel model){
		this.model = model;
	}
	
	public void stopAll(){
		pool.shutdown();
		queue.clear();
		saveTasks(runnings);
		saveTasks(waitings);
		saveTasks(completes);
		saveTasks(exceptions);
		try 
		{
			 System.err.println("waiting to termination in 60 s");
		     // Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(10, TimeUnit.SECONDS))
		   	{
		      pool.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		      if (!pool.awaitTermination(10, TimeUnit.SECONDS))
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
	public void saveTasks(List<IContext> contexts){
		H2DBService.getInstance().updateContexts(contexts);
	}
	public void saveTask(IContext context)
	{
		H2DBService.getInstance().updateTask(context);
	}
	public void loadTasks()
	{
		List<IContext> loads = H2DBService.getInstance().loadTask();
		for(IContext context : loads){
			switch(context.getState())
			{
				case COMPLETE:			complete(context);
					break;
				case CREATE:			waiting(context);
					break;
				case DOWNLOAD:			waiting(context);
					break;
				case EXCEPTION:			exception(context);
					break;
				case LOCAL_QUERY:		waiting(context);
					break;
				case NETWORK_QUERY:		waiting(context);
					break;
				case PAUSE:				waiting(context);
					break;
				case RECREATE:			waiting(context);
					break;
				case RESUME:			waiting(context);
					break;
				case WAIT:				waiting(context);
					break;
				default:				logger.debug("null");
					break;			
			}
		}
		logger.debug("waitings:" + waitings.size());
		logger.debug("completes:" + completes.size());
		logger.debug("exceptions:" + exceptions.size());
		if(model != null){
			model.updateRunning();
			model.updateWaiting();		
		}
	}


	public void downloadResumeAllTask(){
		Iterator<IContext> it = waitings.iterator();
		while(it.hasNext()){
			IContext context = it.next();
			downloadResumeTask(context);
		}
		if(model != null){
			model.updateRunning();
			model.updateWaiting();
		}
	}

	public List<IContext> getWaitingList(){
		return waitings;
	}
	public List<IContext> getRunningList(){
		return runnings;
	}
	public void addNewTask(IContext context) {
		if(waitings.contains(context))
		{
			logger.debug("Context:[" + context.getURL() +"] is already in waiting queue.");
			return;
		}
		if(runnings.contains(context))
		{
			logger.debug("Context:[" + context.getURL() +"] is already in running queue.");
			return;
		}
		H2DBService.getInstance().initTask(context);
		waitings.add(context);
		ContextExecutor exec = new ContextExecutor(context);
		pool.submit(exec);
		if(model != null){
			model.updateRunning();
			model.updateWaiting();
			model.fireTableDataChanged();
		}
	}
	public void downloadResumeTask(IContext context)
	{
		ContextExecutor exec = new ContextExecutor(context);
		pool.submit(exec);
	}
	public void run(IContext context) {
		if(waitings.contains(context)){
			waitings.remove(context);
		}
		if(!runnings.contains(context)){
			runnings.add(context);
		}
		if(model != null){
			model.updateRunning();
			model.updateWaiting();
		}
	}
	public void stop(IContext context) {
		if(runnings.contains(context))
		{
			runnings.remove(context);
		}
		if(!waitings.contains(context))
		{
			waitings.add(context);
		}
		if(model != null)
		{
			model.updateRunning();
			model.updateWaiting();
		}
	}
	public void waiting(IContext context)
	{
		if(!waitings.contains(context))
		{
			waitings.add(context);
		}
	}
	public void complete(IContext context)
	{
		if(!completes.contains(context))
		{
			completes.add(context);
		}		
	}
	public void exception(IContext context)
	{
		if(!exceptions.contains(context))
		{
			exceptions.add(context);
		}
	}
}
