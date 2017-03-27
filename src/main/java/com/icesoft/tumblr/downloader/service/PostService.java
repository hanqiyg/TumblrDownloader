package com.icesoft.tumblr.downloader.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.DownloadManager;
import com.icesoft.tumblr.downloader.QueryManager;
import com.icesoft.tumblr.downloader.workers.IQueryWorker;
import com.tumblr.jumblr.types.Post;

public class PostService {
	List<Post> posts = Collections.synchronizedList(new ArrayList<Post>());
	
	private static Logger logger = Logger.getLogger(DownloadManager.class);  
	private static PostService instance = new PostService();
	private PostService(){}
	public static PostService getInstance(){
		return instance;
	}
	public List<Post> getPosts(){
		logger.info("load" + posts.size());
		return posts;
	}	
	public void AddPosts(List<Post> postList){
		for(Post p : postList){
			posts.add(p);
		}
	}
}
