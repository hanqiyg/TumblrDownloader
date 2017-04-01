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
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.workers.PoolingHttpClientDownloadWorker;

public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	private ExecutorService pool = Executors.newFixedThreadPool(Settings.getInstance().getWorkerCount());	
	
	private List<DownloadTask> tasks = Collections.synchronizedList(new ArrayList<DownloadTask>());
	
	private DownloadManager(){
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
	
	public void saveTask(DownloadTask task){
	}
	public void loadTasks(){
	}
	public void addTask(DownloadTask task){
		if(isAdded(task)){
			logger.info("Task:[" + task.getURL() +"] is already added.");
			return;
		}
		if(task.getState().equals(DownloadTask.STATE.QUERY_WAITING)){
			PoolingHttpClientDownloadWorker worker = new PoolingHttpClientDownloadWorker
					(
					HttpClientConnectionManager.getInstance().getHttpClient(),
					task
					);
			
			Future<Void> f = pool.submit(worker);
			task.setFuture(f);
			tasks.add(task);
		}
	}
	public boolean isAdded(DownloadTask task){
		if(tasks.contains(task)){
			return true;
		}
		return false;
	}
}
