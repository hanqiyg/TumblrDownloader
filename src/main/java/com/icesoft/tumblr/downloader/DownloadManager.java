package com.icesoft.tumblr.downloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.workers.HttpGetImageWorker;
import com.icesoft.tumblr.downloader.workers.HttpGetVideoWorker;
import com.icesoft.tumblr.downloader.workers.IHttpGetWorker;
import com.icesoft.tumblr.model.ImageInfo;
import com.icesoft.tumblr.model.VideoInfo;


public class DownloadManager {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static DownloadManager instance = new DownloadManager();
	private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());	
	private List<IHttpGetWorker> workers = new ArrayList<IHttpGetWorker>();
	private List<String> duplicates = new ArrayList<String>();
	private DownloadManager(){}
	public static DownloadManager getInstance(){
		return instance;
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
	public void addVideoTask(VideoInfo info) 
	{
		String videoURL = info.getURL();
		String posterURL = info.getPosterURL();
		if(videoURL != null && !duplicates.contains(videoURL))
		{
			duplicates.add(videoURL);
			HttpGetVideoWorker worker = new HttpGetVideoWorker(videoURL,Settings.save_location + File.separator + info.blogName + File.separator + info.id);
			workers.add(worker);
			pool.submit(worker);
		}else
		{
			logger.info("videoURL:" + videoURL + " Duplications:" + duplicates.contains(videoURL));
		}	
		addImageTask(new ImageInfo(posterURL,info.id,info.blogName));		
	}
	public void addImageTask(ImageInfo info){
		if(info.getURL() != null && !info.getURL().trim().equals("") && !duplicates.contains(info.getURL()))
		{
			duplicates.add(info.getURL());
			HttpGetImageWorker worker = new HttpGetImageWorker(info.getURL(),Settings.save_location + File.separator + info.blogName + File.separator + info.id);
			workers.add(worker);
			pool.submit(worker);
		}else
		{
			logger.info("imageURL:" + info.getURL() + " Duplications:" + duplicates.contains(info.getURL()));
		}
	}
}
