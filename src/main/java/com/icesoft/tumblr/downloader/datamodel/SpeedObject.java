package com.icesoft.tumblr.downloader.datamodel;

import com.icesoft.utils.UnitUtils;

public class SpeedObject implements Comparable<SpeedObject>
{
	private float speed;
	public SpeedObject(float speed)
	{
		this.speed = speed;
	}
	public float getValue()
	{
		return speed;
	}
	@Override
	public String toString() 
	{
		int bps = (int) (speed * 1000);
		return UnitUtils.getFormatSpeed(bps);
	}
	@Override
	public int compareTo(SpeedObject o) {
		return Float.compare(this.getValue(), o.getValue());
	}
}
