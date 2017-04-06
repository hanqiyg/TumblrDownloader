package com.icesoft.tumblr.downloader.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.workers.DownloadTask.STATE;
import com.icesoft.tumblr.downloader.workers.PoolingHttpClientDownloadWorker;
import com.icesoft.tumblr.downloader.workers.PoolingHttpGetDownloadWorker;

public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	private ExecutorService pool = Executors.newFixedThreadPool(Settings.getInstance().getWorkerCount());	
	
	private List<DownloadTask> tasks = Collections.synchronizedList(new ArrayList<DownloadTask>());
	
	private DownloadManager(){
		loadTasks();
	}
	public static DownloadManager getInstance(){
		return instance;
	}
	public List<DownloadTask> getTasks(){
		return tasks;
	}
	public void stopAll(){
		pool.shutdown();
		Iterator<DownloadTask> it = tasks.iterator();
		while(it.hasNext()){
			DownloadTask task = it.next();
			it.remove();
			task.stop();
			saveTask(task);
		}
	}
	public void removeTask(DownloadTask task,boolean deleteFile,boolean saveTask){
		if(tasks.contains(task)){
			tasks.remove(task);
			Future<Void> f = task.getFuture();
			if(f != null){
				f.cancel(true);
			}
		}		
		if(deleteFile){
			if(task.getFile().exists()){
				task.getFile().delete();
			}
		}
		if(saveTask){
			saveTask(task);
		}
		task = null;
	}
	
	public void saveTask(DownloadTask task)
	{
		H2DBService.getInstance().updateTask(task);
	}
	public void loadTasks()
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
	}
	public void addNewTask(DownloadTask task){
		if(H2DBService.getInstance().isURLExist(task.getURL())){
			logger.info("Task:[" + task.getURL() +"] is already added.");
			return;
		}
		H2DBService.getInstance().initTask(task.getURL());
		if(task.getState().equals(DownloadTask.STATE.QUERY_WAITING)){
			PoolingHttpGetDownloadWorker worker = new PoolingHttpGetDownloadWorker(task);
			Future<Void> f = pool.submit(worker);
			task.setFuture(f);
			tasks.add(task);
		}
	}
	public void downloadResumeTask(DownloadTask task){
		if(!tasks.contains(task)){
			logger.info("Task[" + task.getURL() +"] is not in task list.");
			return;
		}
		if(task.getFuture() != null){
			logger.info("Task[" + task.getURL() +"] is already running.");
			return;
		}
		if(task.getState().equals(DownloadTask.STATE.QUERY_WAITING)){
			PoolingHttpGetDownloadWorker worker = new PoolingHttpGetDownloadWorker(task);
			Future<Void> f = pool.submit(worker);
			task.setFuture(f);
		}
	}
	public void downloadResumeAllTask(){
		Iterator<DownloadTask> it = tasks.iterator();
		while(it.hasNext()){
			DownloadTask task = it.next();
			downloadResumeTask(task);
		}
	}
}
