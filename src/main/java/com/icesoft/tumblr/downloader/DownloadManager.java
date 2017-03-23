package com.icesoft.tumblr.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.icesoft.tumblr.downloader.workers.HttpGetVideoWorker;
import com.icesoft.tumblr.downloader.workers.IHttpGetWorker;


public class DownloadManager {
	private static DownloadManager instance = new DownloadManager();
	private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());	
	private List<IHttpGetWorker> workers = new ArrayList<IHttpGetWorker>();
	private List<String> duplicates = new ArrayList<String>();
	private DownloadManager(){}
	public static DownloadManager getInstance(){
		return instance;
	}
	public void addVideoTask(String url,String filepath){
		if(duplicates.contains(url)){
			System.out.println("Already added:" + url);
		}else{
			duplicates.add(url);
			System.out.println("addVideoTask" + url + "," + filepath);
			HttpGetVideoWorker worker = new HttpGetVideoWorker(url,filepath);
			workers.add(worker);
			pool.submit(worker);
		}
	}
	public List<IHttpGetWorker> getTasks(){
		return workers;
	}
	public void stopAll(){
		if(workers != null && workers.size() > 0){
			for(IHttpGetWorker worker : workers){
				worker.stop();
			}
		}
	}
}
