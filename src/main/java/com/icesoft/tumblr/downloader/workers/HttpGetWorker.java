package com.icesoft.tumblr.downloader.workers;

import java.util.concurrent.Callable;

import com.icesoft.tumblr.executors.Contextful;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;

public class HttpGetWorker implements Callable<Void>, Comparable<HttpGetWorker>,Contextful
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
				&& !context.getState().equals(DownloadState.PAUSE)
				&& !context.getState().equals(DownloadState.EXCEPTION)
			)
		{
			DownloadState s = context.getState().execute(context);
			if(s != null){
				context.setState(s);
			}else{
				break;
			}
		}
		context.setRun(false);
		return null;
	}
	
	@Override
	public int hashCode() {
		System.err.println("hashCode equals");
		if(this.context != null && this.context.getURL() != null)
		{
			return this.context.getURL().hashCode();
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		System.err.println("HttpGetWorker equals");
		if(obj != null){
			if(obj instanceof HttpGetWorker)
			{
				HttpGetWorker w = (HttpGetWorker) obj;
				if(w.getContext() != null && w.getContext().getURL() != null && this.context.getURL() != null)
				{
					if(this.context.getURL().equals(w.getContext().getURL()))
					{
						return true;
					}
				}
			}
			else if(obj instanceof IContext)
			{
				IContext c = (IContext) obj;
				if(c.getURL() != null && this.context.getURL() != null)
				{
					if(c.getURL().equals(this.getContext().getURL()))
					{
						return true;
					}
				}
			}
		}
		return false;
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
