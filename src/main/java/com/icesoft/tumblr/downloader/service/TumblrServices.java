package com.icesoft.tumblr.downloader.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Constants;
import com.icesoft.tumblr.downloader.configure.TumblrToken;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

public class TumblrServices {
	private static Logger logger = Logger.getLogger(TumblrServices.class);  
	private User user;
	private JumblrClient client;
	private static TumblrServices instance = new TumblrServices();
	private TumblrServices(){}
	
	public static TumblrServices getInstance(){
		return instance;
	}
	public String testConnect(TumblrToken token){
		logger.debug("Jumblr Service -> testConnect" + "\r\n"
					+ "      consumer_key:["	+ token.getConsumer_key() 		+ "]" 	+ "\r\n"
					+ "   consumer_secret:[" 	+ token.getConsumer_secret() 	+ "]"	+ "\r\n"
					+ "       oauth_token:[" 	+ token.getOauth_token() 		+ "]"  	+ "\r\n"
					+ "oauth_token_secret:[" 	+ token.getOauth_token_secret() + "]"	+ "\r\n"
					+ "connecting....");
		String result = null;
		try{
			JumblrClient client = new JumblrClient(token.getConsumer_key(),token.getConsumer_secret());
			client.setToken(token.getOauth_token(), token.getOauth_token_secret());
			User user = client.user();
			if(user != null){
				result = "Success User:" + user.getName();
				logger.debug("Jumblr Service -> testConnect: success. [User:" + (user==null?"null":user.getName()) + "]");
			}else{
				logger.debug("Jumblr Service -> testConnect: success. [User: Null]"); 
				result = "Success User:Null";
			}
		}catch(Exception e){
			logger.error("Jumblr Service -> testConnect: failure. " + e.getMessage()); 
			result = "Error:" + e.getMessage();
		}		
		return result;
	}
	public boolean connectService(){
		try{
			logger.debug("Jumblr Service -> connecting.");
				client = new JumblrClient(
						SettingService.getInstance().getToken().getConsumer_key(),
						SettingService.getInstance().getToken().getConsumer_secret()
						);
				client.setToken(
						SettingService.getInstance().getToken().getOauth_token(),
						SettingService.getInstance().getToken().getOauth_token_secret()
						);
			user = client.user();
			return true;
		}catch(Exception e){
			logger.error("Jumblr Service -> connect: failure. " + e.getMessage()); 
			return false;
		}	
	}
	public int getLikesCount(){
		if(user == null){
			connectService();
		}
		int count = user.getLikeCount();
		logger.debug("Jumblr Service -> getLikesCount: " + count);
		return count;
	}
	public Post getLikeById(int index){
		logger.debug("Jumblr Service -> getLikeById[" + index + "] connecting.");
		if(index < 0){
			index = 0;
		}	
		if(client == null){
			connectService();
		}

		Map<String, Integer> options = new HashMap<String, Integer>();
		options.put("limit", 1);
		options.put("offset",index);
		
		List<Post> p = client.userLikes(options);
		if(p!= null && p.size()>0){
			Post post = p.get(0);
			logger.debug("Jumblr Service -> getLikeById[" + index + "]: "+ post.getBlogName() + "[" + post.getId() + "]");
			return post;
		}else{
			//System.out.println("null");
			return null;
		}
	}
	public List<Post> getLikeById(int limit,int offset){
		logger.debug("Jumblr Service -> getLikeById[" + offset + "-" + (offset + limit) + "] connecting.");
		if(offset < 0 || limit <= 0){
			return null;
		}	
		if(client == null){
			connectService();
		}

		Map<String, Integer> options = new HashMap<String, Integer>();
		options.put("limit", limit);
		options.put("offset",offset);
		
		List<Post> posts = client.userLikes(options);
		if(posts!= null && posts.size()>0){
			int count = posts.size();
			logger.debug("Jumblr Service -> getLikeById[" + offset + "-" + (offset + limit) +  "]: get [" + count + "] Posts.");
			return posts;
		}
		return null;
	}
	public String getBlogName(Post post){
		String blogName = post.getBlogName();
		if(blogName != null && !blogName.trim().equals("")){
			return blogName;
		}else{
			return Constants.UNNAMEDBLOG;
		}
	}
	public String getBlogId(Post post){
		long id = post.getId();
		if(id > 0){
			return String.valueOf(id);
		}else{
			return "0";
		}
	}
}
