package com.icesoft.tumblr.downloader.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icesoft.tumblr.downloader.Settings;
import com.icesoft.tumblr.downloader.datamodel.LikesPostModel;
import com.icesoft.tumblr.downloader.panel.LikesPanel;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

public class TumblrServices {
	private User user;
	private JumblrClient client;
	
	private static TumblrServices instance = new TumblrServices();
	private TumblrServices(){}
	
	public static TumblrServices getInstance(){
		return instance;
	}
	public String testConnect(String CONSUMER_KEY,String CONSUMER_SECRET, String OAUTH_TOKEN, String OAUTH_TOKEN_SECRET){
		 String result = null;
		try{
			JumblrClient client = new JumblrClient(CONSUMER_KEY,CONSUMER_SECRET);
			client.setToken(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);
			User user = client.user();
			if(user != null){
				result = "User:" + user.getName();
			}else{
				result = "User:Null";
			}
		}catch(Exception e){
			result = "Error:" + e.getMessage();
		}
		return result;
	}
	public static final String consumer_key = "MfA6BDjf9VUaGZhk0Qzc9mQxMoqrGAGbYNsLBM6i8ZZQDTQYaQ";
	public static final String consumer_secret = "zXRjmPNWZ4lNtZ9TK5gvuQ0qsGEzB5IpGRdt3XyVkf9o910apy";
	public static final String oauth_token = "qcTky7QPyOiTsmFQTfJCQbblSgcg8JNhKJg7pKEXr1BlBuAKWB";
	public static final String oauth_token_secret = "uvtpgLFPIq3dGTTSxqG1pQSoSKgV6GXR4ZgsYeKBzl6Bq2by1q";

	public boolean connectService(){
		System.out.println("connectService");
		if(Settings.consumer_key == null || Settings.consumer_secret == null || Settings.oauth_token == null || Settings.oauth_token_secret == null){
			client = new JumblrClient(consumer_key,consumer_secret);
			client.setToken(oauth_token, oauth_token_secret);
		}else{
			client = new JumblrClient(Settings.consumer_key,Settings.consumer_secret);
			client.setToken(Settings.oauth_token, Settings.oauth_token_secret);	
		}

		user = client.user();
		if(user != null){
			return true;		
		}else{
			return false;
		}		
	}
	public int getLikesCount(){
		System.out.println("getLikesCount");
		if(user == null){
			connectService();
		}
		return user.getLikeCount();
	}
	public Post getLikeById(int index){
		System.out.println("getLikeById" + index);
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
			//System.out.println(p.get(0).getBlogName());
			return p.get(0);
		}else{
			//System.out.println("null");
			return null;
		}		
	}
}
