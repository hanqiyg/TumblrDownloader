package com.icesoft.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import com.icesoft.tumblr.downloader.configure.Constants;

public class StringUtils {

	//private static Logger logger = Logger.getLogger(StringUtils.class);  
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
			//logger.debug("getFilename from disposition as " + filename);
		}else{
			filename = getFilenameFromFileUrl(url);
			//logger.debug("getFilename from file url as " + filename);
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
		String pathString = sub.replaceAll("[^a-zA-Z0-9.-]", Constants.FILENAME_PARTITION);
		//logger.info("Replace " + sub + " as " + pathString);
		return pathString;
	}
	public static String getFilenameExt(String filename){
		if(filename != null && filename.contains(".")){
			String[]  args = filename.split(Constants.SPLITE_REGX_DOT);
			return args[args.length-1];
		}else{
			return null;
		}		
	}
	public static String getAbsolutePath(String saveLocation,String filename,String blogId,String blogName,String ext){
		if(getFilenameExt(filename)!= null && ext != null && getFilenameExt(filename).equals(ext)){
			return saveLocation + File.separator + blogId + Constants.FILENAME_PARTITION + blogName + Constants.FILENAME_PARTITION + filename;	
		}else{
			return saveLocation + File.separator + blogId + Constants.FILENAME_PARTITION + blogName + Constants.FILENAME_PARTITION + filename + "." + ext;	
		}				
	}
	public static List<String> getURLsFromString(String add){
		List<String> urls = new ArrayList<String>();
		Pattern p = Pattern.compile("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",Pattern.CASE_INSENSITIVE );
		String[] ss = add.split("\\n");	
		for(String url : ss){
			Matcher m = p.matcher(url);    
	          if(m.find()){  
	              System.out.println(m.group()); 
	              urls.add(m.group());
	          }  
		}
		return urls;
	}
}
