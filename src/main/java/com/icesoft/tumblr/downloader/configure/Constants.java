package com.icesoft.tumblr.downloader.configure;

public class Constants {
	public enum HttpClientConnectionManagerConfigure{
		MaxTotal("HttpClientConnectionManagerConfigure.MaxTotal",5);
		private String key;
		private int value;
		private HttpClientConnectionManagerConfigure(String key,int value){
			this.key = key;
			this.value = value;
		}
		public String getKey(){
			return key;
		}
		public int getValue(){
			return value;
		}
	}
	public enum DownloadManagerConfigure{
		WorkerCount("DownloadManagerConfigure.workercount",5);
		private String key;
		private int value;
		private DownloadManagerConfigure(String key,int value){
			this.key = key;
			this.value = value;
		}
		public String getKey(){
			return key;
		}
		public int getValue(){
			return value;
		}
	}
}
