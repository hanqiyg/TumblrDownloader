package com.icesoft.tumblr.state;

import java.util.concurrent.Callable;

import com.icesoft.tumblr.downloader.managers.DownloadManager;
import com.icesoft.tumblr.state.interfaces.IContext;

public class ContextExecutor implements Callable<Void>, Comparable<ContextExecutor> 
{
	private int priority;
	private IContext context;
	public ContextExecutor(IContext context){
		this.context = context;
		this.priority = 5;
	}
	public ContextExecutor(IContext context,int priority){
		this.context = context;
		this.priority = priority;
	}
	
	@Override
	public Void call() throws Exception {
		context.setRun(true);
		DownloadManager.getInstance().run(context);
		while(context.isRun() 
				&& context.getState() != DownloadState.COMPLETE 
				&& context.getState() != DownloadState.EXCEPTION
				&& context.getState() != DownloadState.PAUSE){
			context.perform();
		}
		context.setRun(false);
		if(context.getState() != DownloadState.COMPLETE){
			DownloadManager.getInstance().complete(context);
		}
		if(context.getState() != DownloadState.EXCEPTION){
			DownloadManager.getInstance().exception(context);
		}
		if(context.getState() != DownloadState.PAUSE){
			DownloadManager.getInstance().stop(context);
		}
		return null;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public int compareTo(ContextExecutor o) {
		return this.priority - o.priority;
	}
	public IContext getContext() {
		return context;
	}
}
