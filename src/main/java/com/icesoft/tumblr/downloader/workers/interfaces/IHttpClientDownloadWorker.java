package com.icesoft.tumblr.downloader.workers.interfaces;

import java.util.concurrent.Callable;

import com.icesoft.tumblr.downloader.workers.DownloadTask;

public interface IHttpClientDownloadWorker extends Callable<DownloadTask> {
	public DownloadTask getTask();
	public void stop();
}
