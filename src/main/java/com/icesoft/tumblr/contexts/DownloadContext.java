package com.icesoft.tumblr.contexts;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;
import com.icesoft.tumblr.state.interfaces.IContext;
import com.icesoft.utils.StringUtils;


public class DownloadContext implements IContext{
	private static Logger logger = Logger.getLogger(DownloadContext.class); 

	private String 	URL;
	private String 	savePath;
	private String  blogId;
	private String  blogName;
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

	public DownloadContext(String URL, DownloadState state, String savePath, String blogId,String blogName) {
		this.URL = URL;
		this.state = state;
		this.savePath = savePath;
		this.priority = DownloadPriority.NORMAL;
		this.blogId = blogId;
		this.blogName = blogName;
	}
	public DownloadContext(String url, String blogId,String blogName,String filename,long filesize, long time, int state,
			String ext, String savepath, long totalTime, int priority) {
		this.URL = url;
		this.blogId = blogId;
		this.blogName = blogName;
		this.filename = filename;
		this.remoteFilesize.set(filesize);
		this.createTime.set(time);
		this.state = 	DownloadState.valueOf(state);
		this.priority = DownloadPriority.valueOf(priority);
		this.ext = ext;
		this.savePath = savepath;
		this.totalTime.set(totalTime);
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
		String path = null;
		if(this.savePath==null || this.filename == null || this.blogId == null){
			//System.err.println("savePath" + savePath + "filename" + filename + "blogId" + blogId);
			path = null;
		}else{
			path = StringUtils.getAbsolutePath(this.savePath, this.filename, this.blogId, this.blogName, this.ext);
		}		
		//logger.debug("getAbsolutePath(" + savePath + "," + filename + "," + blogId + "," + blogName + "," + ext + ") = " + path);
		return path;
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
	public int hashCode() {
		return URL.hashCode();
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
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Context START: [" + URL + "]\n");
		sb.append(" savePath:" + savePath);
		sb.append(" filename:" + filename);
		sb.append(" blogId:" + blogId);
		sb.append(" blogName:" + blogName);
		sb.append(" ext:" + ext);
		sb.append(" remoteFilesize:" + remoteFilesize);
		sb.append(" localFilesize:" + localFilesize);
		sb.append(" createTime:" + createTime);
		sb.append(" totalTime" + totalTime);
		sb.append(" currentSpeed:" + currentSpeed);
		sb.append(" complete:" + complete);	
		sb.append(" message:" + message);
		sb.append(" state:" + state);
		sb.append(" priority:" + priority);
		sb.append(" run:" + run);
		sb.append("\n");
		sb.append("Context END  : [" + URL + "]");
		return sb.toString();
	}
	public synchronized String getBlogName() {
		return blogName;
	}
	public synchronized void setBlogName(String blogName) {
		this.blogName = blogName;
	}
	public synchronized String getBlogId() {
		return blogId;
	}
	public synchronized void setBlogId(String blogId) {
		this.blogId = blogId;
	}	
}
