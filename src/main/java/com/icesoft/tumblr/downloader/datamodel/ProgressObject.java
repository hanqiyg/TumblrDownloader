package com.icesoft.tumblr.downloader.datamodel;

import com.icesoft.tumblr.state.interfaces.IContext;
import com.icesoft.utils.UnitUtils;

public class ProgressObject implements Comparable<ProgressObject>
{
	private float value;
	public ProgressObject(IContext task)
	{
		long curr = task.getLocalFilesize();
		long full = task.getRemoteFilesize();
		if(full <= 0)
		{
			value =  0f;
		}else{
			value = curr * 100 / full;
		}
	}
	public float getValue()
	{
		return value;
	}
	public int intValue(){
		return (int) value;
	}
	@Override
	public String toString() 
	{
		return UnitUtils.getFormatSize(value);
	}
	@Override
	public int compareTo(ProgressObject  o) {
		return Float.compare(this.getValue(), o.getValue());
	}
}