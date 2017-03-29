package com.icesoft.tumblr.downloader.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.workers.AllLikedQueryWorker;
import com.icesoft.tumblr.downloader.workers.interfaces.IQueryWorker;

public class QueryManager {
	private static Logger logger = Logger.getLogger(QueryManager.class);  
	private static QueryManager instance = new QueryManager();
	private ExecutorService single = Executors.newSingleThreadExecutor();
	private IQueryWorker worker;

	private QueryManager(){}
	public static QueryManager getInstance(){
		return instance;
	}
	public IQueryWorker getWorker(){
		return worker;
	}
	
	public void ExecLikedQurey()
	{
		AllLikedQueryWorker likedWorker = new AllLikedQueryWorker();
		ExecWorker(likedWorker);		
	}
	public void ExecWorker(IQueryWorker add)
	{
		stopQuery();
		this.worker = add;
		single.submit(add);
	}
	public void stopQuery()
	{
		if(this.worker != null){
			this.worker.stop();
			this.worker = null;
		}
	}
	public void pauseQuery(){
		if(this.worker != null){
			this.worker.stop();
		}
	}
	public void resumeQuery(){
		if(this.worker != null){
			this.worker.start();
			single.submit(worker);
		}else{
			logger.error("Worker is already terminated.");
		}
	}
}
