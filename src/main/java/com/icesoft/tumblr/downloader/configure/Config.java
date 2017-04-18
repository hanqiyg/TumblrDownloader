package com.icesoft.tumblr.downloader.configure;

import java.net.Proxy;

public class Config{
	public String basePath;
	public int windowX,windowY,windowW,windowH;
	public int workerCount,clientCount;
	public int connectTimeout,readTimeout;
	public String consumerKey,consumerSecret,oauthToken,oauthTokenSecret;
	public Proxy proxy;
		public Config(){
		this.basePath = "./";
		this.windowX = 100;
		this.windowY = 100;
		this.windowW = 600;
		this.windowH = 400;
		this.workerCount = 5;
		this.clientCount = 5;
		this.connectTimeout = 10 * 1000;
		this.readTimeout = 10 * 1000;
		this.proxy = null;
	}
	public Config(String basePath,int windowX, int windowY, int windowW, int windowH,
			int workerCount, int clientCount, int connectTimeout, int readTimeout, Proxy proxy,
			String consumerKey,String consumerSecret,String oauthToken,String oauthTokenSecret){
		this.basePath = basePath;
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowW = windowW;
		this.windowH = windowH;
		this.workerCount = workerCount;
		this.clientCount = clientCount;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.proxy = proxy;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;
	}
}
