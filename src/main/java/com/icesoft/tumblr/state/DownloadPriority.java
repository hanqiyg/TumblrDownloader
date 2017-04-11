package com.icesoft.tumblr.state;

public enum DownloadPriority
{
	CRITICAL(5),HIGH(4),MEDIUM(3),NORMAL(2),LOW(1);
	private int priority;
	private DownloadPriority(int priority){
		this.priority = priority;
	}
	public int intValue(){
		return this.priority;
	}	
}
