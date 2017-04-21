package com.icesoft.tumblr.downloader.configure;

import java.net.Proxy;

public class Config{
	public String basePath;
	public int windowX,windowY,windowW,windowH;
	public int workerCount,clientCount;
	public int connectTimeout,readTimeout;
	public TumblrToken token;
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
		this.token = null;
	}
	public Config(String basePath,int windowX, int windowY, int windowW, int windowH,
			int workerCount, int clientCount, int connectTimeout, int readTimeout, Proxy proxy,
			TumblrToken token){
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
		this.token = token;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("========= Config Begin ========="	+ Constants.ENTER);
		sb.append("basePath:" 		+ basePath 			+ Constants.ENTER);
		sb.append("windowX:" 		+ windowX 			+ Constants.ENTER);
		sb.append("windowY:" 		+ windowY 			+ Constants.ENTER);
		sb.append("windowW:" 		+ windowW 			+ Constants.ENTER);
		sb.append("windowH:" 		+ windowH 			+ Constants.ENTER);
		sb.append("workerCount:" 	+ workerCount 		+ Constants.ENTER);
		sb.append("clientCount:" 	+ clientCount 		+ Constants.ENTER);
		sb.append("connectTimeout:" + connectTimeout 	+ Constants.ENTER);
		sb.append("readTimeout:" 	+ readTimeout 		+ Constants.ENTER);
		sb.append("proxy:" 								+ Constants.ENTER);
		sb.append((proxy==null?"null":proxy.toString())	+ Constants.ENTER);
		sb.append("token:"								+ Constants.ENTER);
		sb.append((token==null?"null":token.toString())	+ Constants.ENTER);
		sb.append("========= Config End   ========="	+ Constants.ENTER);
		return sb.toString();
	}	
}
