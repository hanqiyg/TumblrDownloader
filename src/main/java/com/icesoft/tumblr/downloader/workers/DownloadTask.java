package com.icesoft.tumblr.downloader.workers;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;

public class DownloadTask 
{
	private String 	url;
	private String 	savePath;
	private STATE 	state;
	private String 	filename;
	private String 	ext;
	private long   	remoteFilesize;
	private long 	localFilesize;
	private long 	createTime;
	private long 	totalTime;
	
	private long 	currentSpeed;
	
	private String  message;
	private Future<Void> future;
	
	public enum STATE
	{
		QUERY_WAITING(1),QUERY_RUNNING(2),QUERY_COMPLETE(3),QUERY_EXCEPTION(4),
		DOWNLOAD_WAITING(5),DOWNLOAD_RUNNING(6),DOWNLOAD_PAUSING(7),DOWNLOAD_COMPLETE(8),DOWNLOAD_EXCEPTION(9);
		private int index;
		private STATE(int i){
			this.index = i;
		}
		public int intValue(){
			return index;
		}		
	}
	public DownloadTask(String url, String savePath)
	{
		this.url = url;
		this.savePath = savePath;
		this.createTime = new Date().getTime();
		this.state = STATE.QUERY_WAITING;
	}
	public DownloadTask initTask(){
		return new 	DownloadTask(this.url,this.savePath);	
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
	public String getMessage() 
	{
		return message;
	}
	public void setMessage(String message) 
	{
		this.message = message;
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
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof DownloadTask &&((DownloadTask)obj).getURL().equals(this.url)){
			return true;
		}
		return false;
	}
	public long getRemoteFilesize() {
		return remoteFilesize;
	}
	public void setRemoteFilesize(long remoteFilesize) {
		this.remoteFilesize = remoteFilesize;
	}
	public long getLocalFilesize() {
		return localFilesize;
	}
	public void setLocalFilesize(long localFilesize) {
		this.localFilesize = localFilesize;
	}
	public Future<Void> getFuture() {
		return future;
	}
	public void setFuture(Future<Void> future) {
		this.future = future;
	}
	public void stop(){
		if(this.future == null){
			return;
		}
		if(this.future.isDone()){
			return;
		}
		if(this.future.isCancelled()){
			return;
		}
		this.future.cancel(true);
	}
}
