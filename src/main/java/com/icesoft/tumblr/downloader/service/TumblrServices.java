package com.icesoft.tumblr.downloader.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.Settings;
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
	public String testConnect(String CONSUMER_KEY,String CONSUMER_SECRET, String OAUTH_TOKEN, String OAUTH_TOKEN_SECRET){
		logger.info("Jumblr Service -> testConnect[consumer_key:" + CONSUMER_KEY + "]" + "[consumer_secret:" + CONSUMER_SECRET + "]"
												+ "[oauth_token:" + OAUTH_TOKEN + "]"  + "[oauth_token_secret:" + OAUTH_TOKEN_SECRET + "] connecting.");
		 String result = null;
		try{
			JumblrClient client = new JumblrClient(CONSUMER_KEY,CONSUMER_SECRET);
			client.setToken(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);
			User user = client.user();
			if(user != null){
				result = "User:" + user.getName();
				logger.info("Jumblr Service -> testConnect: success. [User:" + user + "]"); 
			}else{
				logger.info("Jumblr Service -> testConnect: success. [User: Null]"); 
				result = "User:Null";
			}
		}catch(Exception e){
			logger.error("Jumblr Service -> testConnect: failure. " + e.getMessage()); 
			result = "Error:" + e.getMessage();
		}		
		return result;
	}
	public static final String consumer_key = "MfA6BDjf9VUaGZhk0Qzc9mQxMoqrGAGbYNsLBM6i8ZZQDTQYaQ";
	public static final String consumer_secret = "zXRjmPNWZ4lNtZ9TK5gvuQ0qsGEzB5IpGRdt3XyVkf9o910apy";
	public static final String oauth_token = "qcTky7QPyOiTsmFQTfJCQbblSgcg8JNhKJg7pKEXr1BlBuAKWB";
	public static final String oauth_token_secret = "uvtpgLFPIq3dGTTSxqG1pQSoSKgV6GXR4ZgsYeKBzl6Bq2by1q";

	public boolean connectService(){
		logger.info("Connecting to jumblr service.");
		if(Settings.consumer_key == null || Settings.consumer_secret == null || Settings.oauth_token == null || Settings.oauth_token_secret == null){
			client = new JumblrClient(consumer_key,consumer_secret);
			client.setToken(oauth_token, oauth_token_secret);
		}else{
			client = new JumblrClient(Settings.consumer_key,Settings.consumer_secret);
			client.setToken(Settings.oauth_token, Settings.oauth_token_secret);	
		}

		user = client.user();
		if(user != null){
			logger.info("Jumblr Service connected.");
			return true;		
		}else{
			logger.error("Jumblr Service connect error.");
			return false;
		}		
	}
	public int getLikesCount(){
		if(user == null){
			connectService();
		}
		int count = user.getLikeCount();
		logger.info("Jumblr Service -> getLikesCount: " + count);
		return count;
	}
	public Post getLikeById(int index){
		logger.info("Jumblr Service -> getLikeById[" + index + "] connecting.");
		if(client == null){
			connectService();
		}
		if(index < 0){
			index = 0;
		}	
		Map<String, Integer> options = new HashMap<String, Integer>();
		options.put("limit", 1);
		options.put("offset",index);
		
		List<Post> p = client.userLikes(options);
		if(p!= null && p.size()>0){
			Post post = p.get(0);
			logger.info("Jumblr Service -> getLikeById[" + index + "]: " + post.getBlogName());
			return post;
		}else{
			//System.out.println("null");
			return null;
		}
	}
	public String getBlogName(Post post){
		String blogName = post.getBlogName();
		if(blogName != null && !blogName.trim().equals("")){
			return blogName;
		}else{
			return Settings.UNNAMEDBLOG;
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
