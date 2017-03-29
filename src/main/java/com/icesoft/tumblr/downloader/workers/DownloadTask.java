package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.util.Date;

public class DownloadTask 
{
	private String 	url;
	private String 	savePath;
	private STATE 	state;
	private String 	filename;
	private String 	ext;
	private long   	filesize = 0;
	private long 	createTime;
	private long 	totalTime;
	
	private long 	currentSize;
	
	private boolean run;
	private long 	currentSpeed;
	
	private String  message;
	
	public enum STATE
	{
		QUERY_WAITING,QUERY_RUNNING,QUERY_COMPLETE,QUERY_EXCEPTION,DOWNLOAD_WAITING,DOWNLOAD_RUNNING,DOWNLOAD_PAUSING,DOWNLOAD_COMPLETE,DOWNLOAD_EXCEPTION
	}	
	public DownloadTask(String url, String savePath)
	{
		this.url = url;
		this.savePath = savePath;
		this.createTime = new Date().getTime();
		this.state = STATE.QUERY_WAITING;
		this.run = true;
	}
	public String getURL(){
		return this.url;
	}
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	public void setState(STATE state)
	{
		this.state = state;
	}
	public STATE getState()
	{
		return this.state;
	}
	public void setExt(String ext)
	{
		this.ext = ext;
	}
	public void setFilesize(long filesize)
	{
		this.filesize = filesize;
	}
	public long getFilesize()
	{
		return this.filesize;
	}
	public long getCreateTime(){
		return this.createTime;
	}
	public void addTotalTime(long a){
		this.totalTime += a;
	}
	public long getTotalTime(){
		return this.totalTime;
	}
	public File getFile(){
		String f = null;
		if(ext != null && !ext.isEmpty()){
			f = savePath + File.separator + File.separator + filename + "." + ext;
		}else{
			f = savePath + File.separator + File.separator + filename;
		}
		return new File(f);
	}
	public long getCurrentSize(){
		return currentSize;
	}
	public void setCurrentSize(long size){
		this.currentSize = size;
	}
	public String getMessage() 
	{
		return message;
	}
	public void setMessage(String message) 
	{
		this.message = message;
	}
	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}
	public long getCurrentSpeed() {
		return currentSpeed;
	}
	public void setCurrentSpeed(long time,long delta) {
		if(time > 0){
			this.currentSpeed = delta/time;
		}else{
			this.currentSpeed = 0;
		}
	}
}
