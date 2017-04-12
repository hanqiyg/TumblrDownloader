package com.icesoft.tumblr.downloader.configure;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.configure.Constants.DownloadManagerConfigure;
import com.icesoft.tumblr.downloader.configure.Constants.HttpClientConnectionManagerConfigure;
import com.icesoft.tumblr.settings.ProxySettings;
import com.icesoft.tumblr.settings.TumblrToken;
import com.icesoft.tumblr.settings.WindowSettings;

public class Settings {
	private static Logger logger = Logger.getLogger(Settings.class);
	public static final String PACKAGE_NAME = "com.icesoft.tumblr.Settings";
	public static final String UNNAMEDBLOG = "UnnamedBlog";

	public int buffer_size = 1024;
	
	public int connect_timeout = 20000;
	public int read_timeout = 20000;
	
	private String save_location;
	
	private int workerCount;
	private int httpClientCount;	

	private static Settings instance = new Settings();
	private TumblrToken token;
	private WindowSettings windowSettings;
	private ProxySettings proxySettings;
	private Preferences prefs;
	
	private Settings(){
		prefs = Preferences.userRoot().node(PACKAGE_NAME);
	}
	
	public static Settings getInstance(){
		return instance;
	}
	public void setSaveLocation(String location){
		this.save_location = location;
	}	
	public String getSaveLocation(){
		if(save_location == null)
		{
			save_location = prefs.get("Save.Location", "./");
		}
		return save_location;
	}
	public void saveLocation(){
		if(save_location != null)
		{
			prefs.put("Save.Location", "d:/tumblr");
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setToken(TumblrToken token){
		this.token = token;
	}
	public void saveToken(TumblrToken token){
		try {
			prefs.put("consumer_key", 		token.getConsumer_key());
			prefs.put("consumer_secret", 	token.getConsumer_secret());
			prefs.put("oauth_token", 		token.getOauth_token());
			prefs.put("oauth_token_secret", token.getOauth_token_secret());
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static final String consumer_key = "MfA6BDjf9VUaGZhk0Qzc9mQxMoqrGAGbYNsLBM6i8ZZQDTQYaQ";
	public static final String consumer_secret = "zXRjmPNWZ4lNtZ9TK5gvuQ0qsGEzB5IpGRdt3XyVkf9o910apy";
	public static final String oauth_token = "qcTky7QPyOiTsmFQTfJCQbblSgcg8JNhKJg7pKEXr1BlBuAKWB";
	public static final String oauth_token_secret = "uvtpgLFPIq3dGTTSxqG1pQSoSKgV6GXR4ZgsYeKBzl6Bq2by1q";
	
	public TumblrToken getToken(){
		if(token == null)
		{
			token = new TumblrToken(
			prefs.get("consumer_key", consumer_key),			
			prefs.get("consumer_secret",consumer_secret),
			prefs.get("oauth_token", oauth_token),
			prefs.get("oauth_token_secret", oauth_token_secret));
		}
		return token;
	}

	public void setWindowSettings(int x, int y,int w,int h){
		if(windowSettings != null){
			windowSettings.setX(x);
			windowSettings.setY(y);
			windowSettings.setW(w);
			windowSettings.setH(h);
		}else{
			windowSettings = new WindowSettings(x,y,w,h);
		}
		saveWindowSettings(windowSettings);
	}
	public void saveWindowSettings(WindowSettings windowSettings){
		try {
			prefs.putInt("Window.X", windowSettings.getX());
			prefs.putInt("Window.Y", windowSettings.getY());
			prefs.putInt("Window.W", windowSettings.getW());
			prefs.putInt("Window.H", windowSettings.getH());	
			prefs.flush();
		} catch (BackingStoreException e) {
			logger.error("SaveWindowSettings error:" + e.getMessage());
		}
	}
	
	public WindowSettings getWindowSetting(){
		if(windowSettings == null){
			windowSettings = new WindowSettings(
					prefs.getInt("Window.X", 100),
					prefs.getInt("Window.Y", 100),
					prefs.getInt("Window.W", 450),
					prefs.getInt("Window.H", 300));
		}
		return windowSettings;
	}

	public ProxySettings getProxySettings() {
		if(proxySettings == null){
			proxySettings = new ProxySettings(
					prefs.get("Proxy.Type", ProxySettings.Type.SOCKS.toString()),
					prefs.get("Proxy.Host", "127.0.0.1"),
					prefs.getInt("Proxy.Port", 1080));
		}
		return proxySettings;
	}

	public void setProxySettings(ProxySettings proxySettings) {
		this.proxySettings = proxySettings;
	}
	public void saveProxySettings(ProxySettings proxySettings)
	{
		try {
			prefs.put("Proxy.Type",		proxySettings.getType().toString());
			prefs.put("Proxy.Host", 	proxySettings.getHost());
			prefs.putInt("Proxy.Port", 	proxySettings.getPort());
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getWorkerCount() {
		if(this.workerCount <= 0){
			this.workerCount = prefs.getInt(DownloadManagerConfigure.WorkerCount.getKey(), 
					DownloadManagerConfigure.WorkerCount.getValue());
		}
		return workerCount;
	}

	public void saveWorkerCount(){
		if(this.workerCount <= 0){
			this.workerCount = DownloadManagerConfigure.WorkerCount.getValue();
		}
		try {
			prefs.putInt(DownloadManagerConfigure.WorkerCount.getKey(),	this.workerCount);
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}
	public int getHttpClientCount() {
		if(this.httpClientCount <= 0){
			this.httpClientCount = prefs.getInt(HttpClientConnectionManagerConfigure.MaxTotal.getKey(),
					HttpClientConnectionManagerConfigure.MaxTotal.getValue());
		}
		return httpClientCount;
	}
	public void setHttpClientCount(int httpClientCount) {
		this.httpClientCount = httpClientCount;
	}
	public void saveHttpClientCount(){
		if(this.httpClientCount <= 0){
			this.httpClientCount = HttpClientConnectionManagerConfigure.MaxTotal.getValue();
		}
		try {
			prefs.putInt(HttpClientConnectionManagerConfigure.MaxTotal.getKey(),	this.httpClientCount);
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
