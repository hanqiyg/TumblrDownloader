package com.icesoft.tumblr.downloader.configure;

public class Constants {
	public static final String HTML_COLORED_TEXT = "<html><b><font color ='%s'>%s</font></b></html>";
	public static final String UNNAMEDBLOG = "UNNAMED";
	public static final String ENTER = "\n";
	public static final String FILENAME_PARTITION = "_";
	public static final String SPLITE_REGX_DOT = "\\.";
	
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
