package com.icesoft.tumblr.state.interfaces;

import com.icesoft.tumblr.state.DownloadPriority;
import com.icesoft.tumblr.state.DownloadState;

public interface IContext{
	public void perform();
	public String getURL();
	public void setURL(String uRL);
	public String getSavePath();
	public void setSavePath(String savePath);
	public String getFilename();
	public void setFilename(String filename);
	public String getExt();
	public void setExt(String ext);
	public long getRemoteFilesize();
	public void setRemoteFilesize(long remoteFilesize);
	public long getLocalFilesize();
	public void setLocalFilesize(long localFilesize);
	public long getCreateTime();
	public void setCreateTime(long createTime);
	public long getTotalTime();
	public void setTotalTime(long totalTime);
	public long getCurrentSpeed();
	public void setCurrentSpeed(long currentSpeed);
	public boolean isComplete();
	public void setComplete(boolean complete);
	public String getMessage();
	public void setMessage(String message);
	public String getAbsolutePath();
	public boolean isRun();
	public void setRun(boolean run);
	public void addTotalTime(long t);
	public void setCurrentSpeed(long time, long delta);
	public DownloadState getState();
	public void setState(DownloadState state);
	public void setPriority(DownloadPriority priority);
	public DownloadPriority getPriority();
	public boolean equals(Object obj);
}
