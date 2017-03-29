package com.icesoft.utils;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

public class StringUtils {
	private static Logger logger = Logger.getLogger(StringUtils.class);  
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
	public static String getSubPathFromUrl(String url)
	{
		String[]  args = url.split("/");
		StringBuffer sb = new StringBuffer();
		for(int i = 1; i < args.length -1; i++)
		{
			//logger.debug("path:" + i + "->" + args[i]);
			if(args[i] != null && !args[i].trim().equals(""))
			{
				sb.append(File.separator + getAsciiPathString(args[i]));
			}
		}
		//logger.info("Path:" + sb.toString());
		return sb.toString();		
	}
	public static String getAsciiPathString(String sub){
		String pathString = sub.replaceAll("[^a-zA-Z0-9.-]", "_");
		//logger.info("Replace " + sub + " as " + pathString);
		return pathString;
	}
}
