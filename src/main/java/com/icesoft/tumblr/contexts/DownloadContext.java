package com.icesoft.tumblr.contexts;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;


public class DownloadContext implements IContext{
	private static Logger logger = Logger.getLogger(DownloadContext.class); 

	private String 	URL;
	private String 	savePath;
	private String 	filename;
	private String 	ext;
	private AtomicLong remoteFilesize = new AtomicLong(0);
	private AtomicLong 	localFilesize = new AtomicLong(0);
	private AtomicLong 	createTime = new AtomicLong(0);
	private AtomicLong 	totalTime = new AtomicLong(0);	
	private AtomicLong 	currentSpeed = new AtomicLong(0);
	private volatile boolean complete;	
	private String  message;
	
	private DownloadState state;
	private DownloadPriority priority;
	
	private volatile boolean run;

	public DownloadContext(String URL, DownloadState state, String savePath) {
		this.URL = URL;
		this.state = state;
		this.savePath = savePath;
		this.priority = DownloadPriority.NORMAL;
	}
	public DownloadContext(String url, String filename, long filesize, long time, int state,
			String ext, String savepath, long totalTime, int priority) {
		this.URL = url;
		this.filename = filename;
		this.remoteFilesize.set(filesize);
		this.createTime.set(time);
		this.state = DownloadState.valueOf(state);
		this.priority = DownloadPriority.valueOf(priority);
		this.ext = ext;
		this.savePath = savepath;
		this.totalTime.set(totalTime);

	}
	public void perform(){
		if(state != null)
		{
			state = state.execute(this);
		}
	}
	public synchronized String getURL() {
		return URL;
	}
	public synchronized void setURL(String uRL) {
		URL = uRL;
	}
	public synchronized String getSavePath() {
		return savePath;
	}
	public synchronized void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public synchronized String getFilename() {
		return filename;
	}
	public synchronized void setFilename(String filename) {
		this.filename = filename;
	}
	public synchronized String getExt() {
		return ext;
	}
	public synchronized void setExt(String ext) {
		this.ext = ext;
	}
	public long getRemoteFilesize() {
		return this.remoteFilesize.get();
	}
	public void setRemoteFilesize(long remoteFilesize) {
		this.remoteFilesize.set(remoteFilesize);
	}
	public long getLocalFilesize() {
		return this.localFilesize.get();
	}
	public void setLocalFilesize(long localFilesize) {
		this.localFilesize.set(localFilesize);
	}
	public long getCreateTime() {
		return this.createTime.get();
	}
	public void setCreateTime(long createTime) {
		this.createTime.set(createTime);
	}
	public long getTotalTime() {
		return this.totalTime.get();
	}
	public void setTotalTime(long totalTime) {
		this.totalTime.set(totalTime);
	}
	public long getCurrentSpeed() {
		return this.currentSpeed.get();
	}
	public void setCurrentSpeed(long currentSpeed) {
		this.currentSpeed.set(currentSpeed);
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public synchronized String getMessage() {
		return message;
	}
	public synchronized void setMessage(String message) {
		this.message = message;
		logger.info(message);
	}
	public synchronized String getAbsolutePath(){
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
		this.totalTime.getAndAdd(t);
	}
	public void setCurrentSpeed(long time, long delta) {
		if(time > 0){
			this.currentSpeed.set(delta/time);
		}else{
			this.currentSpeed.set(0);
		}
	}
	@Override
	public synchronized DownloadState getState() {
		return this.state;
	}
	@Override
	public synchronized void setState(DownloadState state) {
		this.state = state;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof IContext)
		{
			IContext context = (IContext) obj;
			if(context.getURL().equals(this.getURL()))
			{
				return true;
			}
		}
		return false;
	}
	@Override
	public synchronized void setPriority(DownloadPriority priority) {
		this.priority = priority;
	}
	@Override
	public synchronized DownloadPriority getPriority() {
		return this.priority;
	}
}
