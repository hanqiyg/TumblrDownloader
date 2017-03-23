package com.icesoft.tumblr.downloader.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

public class UrlService {

	public static String getUrlFromPost(Post p) {
		if(p instanceof VideoPost){
			VideoPost v = (VideoPost) p;
			List<Video> videos = v.getVideos();
			for(Video vi:videos){
				String urlString = findURL(vi.getEmbedCode());
				String typeString = findType(vi.getEmbedCode());
				if(urlString!= null && typeString!= null){
					return urlString + "." + typeString;
				}						
			}
		}
		if(p instanceof PhotoPost){
			PhotoPost photoPost = (PhotoPost) p;
			List<Photo> photos = photoPost.getPhotos();
			for(Photo photo:photos){
				return photo.getOriginalSize().getUrl();
			}
		}
		return null;
	}

	public static String getFileNameFromPost(Post post) {		
		return "name";
	}

	public static String getSavePathFromPost(Post post) {
		return "d:/";
	}
	public static String findURL(String url){
		String URL = null;
		String pattern1 = "\\<source src=\"";
		String pattern2 = "\" type";
		Pattern r1 = Pattern.compile(pattern1);
		Pattern r2 = Pattern.compile(pattern2);
	    Matcher m1 = r1.matcher(url);
	    Matcher m2 = r2.matcher(url);
	    if (m1.find( ) && m2.find()) {
	    	  int start = m1.end();
	    	  int end = m2.start();
	    	  URL = url.substring(start, end);
	    }
	    return URL;
	}
	public static String findType(String url){
		String type = null;
		String pattern1 = "type=\"video/";
		String pattern2 = "\">";
		Pattern r1 = Pattern.compile(pattern1);
		Pattern r2 = Pattern.compile(pattern2);
	    Matcher m1 = r1.matcher(url);
	    Matcher m2 = r2.matcher(url);
	    if (m1.find( ) && m2.find()) {
	    	  int start = m1.end();
	    	  int end = m2.start();
	    	  type = url.substring(start, end);
	    }
	    return type;
	}

}
