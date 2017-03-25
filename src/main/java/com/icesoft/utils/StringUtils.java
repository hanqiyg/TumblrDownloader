package com.icesoft.utils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.DownloadManager;

public class StringUtils {
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	public static String getFilenameFromDisposition(HttpResponse response){
		Header contentHeader = response.getFirstHeader("Content-Disposition");  
        String filename = null;  
        if (contentHeader != null) {  
            HeaderElement[] values = contentHeader.getElements();  
            if (values.length == 1) {  
                NameValuePair param = values[0].getParameterByName("filename");  
                if (param != null) {  
                    try {  
                        filename = param.getValue();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
        return filename; 
	}
	public static String getFilenameFromFileUrl(String url){
		String[]  args = url.split("/");
		for(String s : args){
			System.out.println(s);
		}
		return url.substring(url.lastIndexOf("/") + 1,
                 url.length());
	}

	public static String getFilename(HttpResponse response,String url){
		String filename = null;
		if(getFilenameFromDisposition(response) != null){
			filename = getFilenameFromDisposition(response);
			logger.debug("getFilename from disposition as " + filename);
		}else{
			filename = getFilenameFromFileUrl(url);
			logger.debug("getFilename from file url as " + filename);
		}
		return filename;
	}
}
