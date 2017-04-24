package com.icesoft.utils;

import java.io.File;

public class FileUtils {
	public static long getLocalFileSize(String absolutePath) {
		if(absolutePath == null)
		{
			return 0;
		}
		File file = new File(absolutePath);
		if(file.exists() && file.isFile()){
			return file.length();
		}
		return 0;
	}
}
