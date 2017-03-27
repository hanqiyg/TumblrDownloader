package com.icesoft.tumblr.downloader.workers;

import java.util.concurrent.Callable;

public interface IWorker extends Callable<Void>{
	public void stop();
	public void start();
}
