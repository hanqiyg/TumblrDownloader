package com.icesoft.tumblr.downloader.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.managers.HttpClientConnectionManager;
import com.icesoft.tumblr.downloader.workers.DownloadTask;
import com.icesoft.tumblr.downloader.workers.PoolingHttpClientDownloadWorker;
import com.icesoft.tumblr.downloader.workers.interfaces.IHttpClientDownloadWorker;


public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	private ExecutorService pool = Executors.newFixedThreadPool(Settings.getInstance().getWorkerCount());	
	
	private List<IHttpClientDownloadWorker> workers = new ArrayList<IHttpClientDownloadWorker>();
	private DownloadManager(){
	}
	public static DownloadManager getInstance(){
		return instance;
	}
	public List<IHttpClientDownloadWorker> getTasks(){
		return workers;
	}
	public void stopAll(){
		if(workers != null && workers.size() > 0){
			for(IHttpClientDownloadWorker worker : workers){
				worker.stop();
				saveTask(worker.getTask());				
			}
		}
	}
	public void saveTask(DownloadTask task){
		
	}
	public List<DownloadTask> loadTasks(){
		return null;		
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
			workers.add(worker);
			pool.submit(worker);
		}
	}
	public boolean isAdded(DownloadTask task){
		for(IHttpClientDownloadWorker w : workers){
			if(w.getTask().equals(task)){
				return true;
			}
		}
		return false;
	}
}
