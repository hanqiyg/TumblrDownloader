package com.icesoft.utils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public class StringUtils {
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
		}else{
			filename = getFilenameFromFileUrl(url);
		}
		return filename;
	}
}
