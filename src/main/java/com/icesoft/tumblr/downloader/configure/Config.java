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
		sb.append("========= Config Begin ========="+ "\n\r");
		sb.append("basePath:" + basePath + "\n\r");
		sb.append("windowX:" + windowX + "\n\r");
		sb.append("windowY:" + windowY + "\n\r");
		sb.append("windowW:" + windowW + "\n\r");
		sb.append("windowH:" + windowH + "\n\r");
		sb.append("workerCount:" + workerCount + "\n\r");
		sb.append("clientCount:" + clientCount + "\n\r");
		sb.append("connectTimeout:" + connectTimeout + "\n\r");
		sb.append("readTimeout:" + readTimeout + "\n\r");
		sb.append("proxy:" + "\n\r" + (proxy==null?"null":proxy.toString()) + "\n\r");
		sb.append("token:" + "\n\r" + (token==null?"null":token.toString()) + "\n\r");
		sb.append("========= Config End   ========="+ "\n\r");
		return sb.toString();
	}	
}
