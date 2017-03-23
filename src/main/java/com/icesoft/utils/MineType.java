package com.icesoft.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MineType {
	private static final Map<String,String> MineTypeMap = new HashMap<String,String>();
	private static final String UNKNOW = "unknow";
	private static MineType instance;
	
	public static MineType getInstance(){
		if(instance == null){
			instance = new MineType();
		}
		return instance;
	}
	
	private MineType(){
		init();
	}
	public void init(){
		MineTypeMap.put("video/mp4", "mp4");
		MineTypeMap.put("video/3gpp", "3gp");
		MineTypeMap.put("video/mpeg", "mpg");
		MineTypeMap.put("video/quicktime", "mov");
		MineTypeMap.put("video/x-flv", "flv");
		MineTypeMap.put("video/x-ms-wmv", "wmv");
		MineTypeMap.put("video/x-m4v", "m4v");
		MineTypeMap.put("video/x-msvideo", "avi");
		
		MineTypeMap.put("image/gif", "gif");
		MineTypeMap.put("image/jpeg", "jpg");
		MineTypeMap.put("image/png", "png");
		MineTypeMap.put("image/tiff", "tiff");
		MineTypeMap.put("image/x-ms-bmp", "bmp");
		MineTypeMap.put("text/plain", "txt");
		MineTypeMap.put("text/xml", "xml");
	}
	public String getExtensionFromMineType(String mineType){
		Set<String> set = MineTypeMap.keySet();		  
		for(Iterator<String> it = set.iterator(); it.hasNext();){
			String key = it.next();
			if(key.equals(mineType)){
				return MineTypeMap.get(key);
			}
		}
		return UNKNOW;
	}
}
