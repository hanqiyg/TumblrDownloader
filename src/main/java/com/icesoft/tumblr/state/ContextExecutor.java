package com.icesoft.tumblr.state;

import java.util.concurrent.Callable;

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
		while(context.isRun() && context.getState() != null){
			context.perform();
		}
		context.setRun(false);
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
}
