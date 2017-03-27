package com.icesoft.tumblr.downloader.workers;

import java.util.concurrent.Callable;

import com.icesoft.tumblr.downloader.workers.STATE;


public interface IHttpGetWorker extends Callable<Void>{
	public int getProgress();
	public String getUrl();
	public long getCurrent();
	public long getFilesize();
	public String getFilename();
	public STATE getState();
	public String getMessage();
	public void stop();
	public float getSpeed();
}
