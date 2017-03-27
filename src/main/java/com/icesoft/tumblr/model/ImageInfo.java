package com.icesoft.tumblr.model;

import com.icesoft.utils.MineType;

public class ImageInfo{
	public String baseURL;
	public String mineType;
	public String id;
	public String blogName;
	
	public ImageInfo(String baseURL,String id,String blogName){
		this.baseURL = baseURL;
		this.id = id;
		this.blogName = blogName;
	}
	@Override
	public String toString() {
		return 	  "BlogName:" + this.blogName
				+ "\nId:" + this.id
				+ "\nVideo:" + this.baseURL
				+ "\nType:" + this.mineType;
	}
	@Override
	public boolean equals(Object obj) 
	{
		if(obj != null && obj instanceof ImageInfo)
		{
			ImageInfo vi = (ImageInfo) obj;
			if(vi.baseURL.equals(this.baseURL))
			{
				return true;
			}
		}
		return false;
	}
	public String getExt(){
		return MineType.getInstance().getExtensionFromMineType(this.mineType);
	}
	public String getURL(){
		return baseURL;
	}
}
