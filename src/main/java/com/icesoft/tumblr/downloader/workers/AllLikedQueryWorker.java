package com.icesoft.tumblr.downloader.workers;

import java.util.List;

import com.icesoft.tumblr.downloader.service.PostService;
import com.icesoft.tumblr.downloader.service.TumblrServices;
import com.tumblr.jumblr.types.Post;

public class AllLikedQueryWorker implements IQueryWorker{
	private boolean run = true;
	private int limit = 50;
	
	private int count = 0;
	private int offset = 0;
	
	public AllLikedQueryWorker()
	{
		
	}
	public AllLikedQueryWorker(int limit)
	{
		this.limit = limit;
	}
	
	@Override
	public Void call() throws Exception {
		count = TumblrServices.getInstance().getLikesCount();
		offset = 0;
		while(run)
		{	
			if(offset < count){
				if(offset + limit >= count){
					List<Post> posts = TumblrServices.getInstance().getLikeById(count - offset, offset);
					if(posts != null && !posts.isEmpty()){
						PostService.getInstance().AddPosts(posts);
					}
					break;
				}else{
					List<Post> posts = TumblrServices.getInstance().getLikeById(limit, offset);
					offset = limit + offset;
					if(posts != null && !posts.isEmpty()){
						PostService.getInstance().AddPosts(posts);
					}
				}				
			}else{
				break;
			}
		}
		return null;
	}
	public void stop(){
		run = false;
	}
	public void start(){
		run = true;
	}
}
