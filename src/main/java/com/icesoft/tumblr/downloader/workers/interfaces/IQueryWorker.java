package com.icesoft.tumblr.downloader.workers.interfaces;

import java.util.concurrent.Callable;

public interface IQueryWorker extends Callable<Void>{
	public void stop();
	public void start();
}
