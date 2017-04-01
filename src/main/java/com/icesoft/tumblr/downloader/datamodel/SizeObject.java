package com.icesoft.tumblr.downloader.datamodel;

import com.icesoft.utils.UnitUtils;

public class SizeObject implements Comparable<SizeObject>{

	private long value;
	public SizeObject(long value)
	{
		this.value = value;
	}
	public long getValue() {
		return value;
	}
	@Override
	public String toString() 
	{
		return UnitUtils.getFormatSize(value);
	}
	@Override
	public int compareTo(SizeObject o) {
		return Long.compare(this.getValue(), o.getValue());
	}
}
