package com.icesoft.tumblr.downloader.workers;

import java.util.concurrent.Callable;

import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class HttpGetWorker implements Callable<Void>, Comparable<HttpGetWorker> 
{
	private IContext context;
	public HttpGetWorker(IContext context)
	{
		this.context = context;
	}
	
	@Override
	public Void call() throws Exception
	{
		context.setRun(true);
		while(context.isRun()
				&& !context.getState().equals(DownloadState.COMPLETE)
				&& !context.getState().equals(DownloadState.EXCEPTION)
				&& !context.getState().equals(DownloadState.PAUSE)
			)
		{
			context.perform();
		}			
		context.setRun(false);
		return null;
	}
	@Override
	public int compareTo(HttpGetWorker o) 
	{
		return context.getPriority().ordinal() - o.getContext().getPriority().ordinal();
	}
	public IContext getContext() 
	{
		return context;
	}
}
