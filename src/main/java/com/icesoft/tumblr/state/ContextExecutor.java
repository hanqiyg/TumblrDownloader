package com.icesoft.tumblr.state;

import java.util.concurrent.Callable;

public class ContextExecutor implements Callable<Void>
{
	private DownloadContext context;
	private volatile boolean run;
	public ContextExecutor(DownloadContext context){
		this.context = context;
	}
	@Override
	public Void call() throws Exception {
		while(run){
			context.perform();
		}
		run = false;
		return null;
	}
}
