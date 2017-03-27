package com.icesoft.tumblr.model;

import com.icesoft.utils.MineType;

public class VideoInfo{
	public String baseUrl;
	public String posterUrl;
	public String hdUrl;
	public String hdPosterUrl;
	public String mineType;
	public String id;
	public String blogName;
	@Override
	public String toString() {
		return 	  "BlogName:" + this.blogName
				+ "\nId:" + this.id
				+ "\nVideo:" + this.baseUrl
				+ "\nPoster:" + this.posterUrl 
				+ "\nType:" + this.mineType
				+"\nVideo HD:" + this.hdUrl 
				+ "\nPoster HD:" + this.hdPosterUrl;  
	}
	@Override
	public boolean equals(Object obj) 
	{
		if(obj != null && obj instanceof VideoInfo)
		{
			VideoInfo vi = (VideoInfo) obj;
			if(vi.baseUrl.equals(this.baseUrl) || vi.hdUrl.equals(this.hdUrl))
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
		if(this.hdUrl != null){			
			return this.hdUrl;
		}else if(this.baseUrl != null){
			return this.baseUrl;
		}else{
			return null;
		}
	}
	public String getPosterURL() {
		if(this.hdPosterUrl != null){			
			return this.hdPosterUrl;
		}else if(this.posterUrl != null){
			return this.posterUrl;
		}else{
			return null;
		}
	}
}
