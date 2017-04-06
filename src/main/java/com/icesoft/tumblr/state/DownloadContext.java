package com.icesoft.tumblr.state;

import java.io.File;

import org.apache.log4j.Logger;

public class DownloadContext {
	private static Logger logger = Logger.getLogger(DownloadContext.class); 

	private String 	URL;
	private String 	savePath;
	private String 	filename;
	private String 	ext;
	private long   	remoteFilesize;
	private long 	localFilesize;
	private long 	createTime;
	private long 	totalTime;	
	private long 	currentSpeed;	
	private boolean complete;	
	private String  message;
	
	private volatile boolean run;
	private DownloadState state;
	public DownloadContext(String URL, DownloadState state) {
		this.URL = URL;
		this.state = state;
	}
	public void perform(){
		if(state != null)
		{
			state = state.execute(this);
		}
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
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
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	public long getCurrentSpeed() {
		return currentSpeed;
	}
	public void setCurrentSpeed(long currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
		logger.info(message);
	}
	public String getAbsolutePath(){
		StringBuffer sb = new StringBuffer();
		if(getSavePath() != null && !getSavePath().trim().equals(""))
		{
			sb.append(getSavePath().trim());
		}else{
			sb.append("./");
		}
		if(getFilename() != null && !getFilename().trim().equals(""))
		{
			sb.append(File.separator + getFilename().trim());
		}
		if(getExt() != null && !getExt().trim().equals(""))
		{
			sb.append("." + getExt().trim());
		}
		return sb.toString();
	}
	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}
	public void addTotalTime(long t) {
		this.totalTime += t;
	}
	public void setCurrentSpeed(long time, long delta) {
		if(time > 0){
			this.currentSpeed = delta/time;
		}else{
			this.currentSpeed = 0;
		}
	}
}
