package com.icesoft.tumblr.downloader.configure;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import org.apache.log4j.Logger;

import com.icesoft.tumblr.downloader.service.H2DBService;
import com.icesoft.tumblr.settings.TumblrToken;

public class Settings {
	private static Logger logger = Logger.getLogger(Settings.class);
	private static Settings instance = new Settings();

	private Config config;
	
	private Settings(){
		load();
	}
	private void load(){
		config = H2DBService.getInstance().loadSettings();
	}
	
	public static Settings getInstance(){
		return instance;
	}

	public boolean testProxyConnect(Proxy proxy){
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://www.tumblr.com/");
			connection = (HttpURLConnection)url.openConnection(proxy);				    
		    connection.setConnectTimeout(10 * 1000);
		    connection.setReadTimeout(10 * 1000);
		    connection.setRequestMethod("GET");
		    connection.connect();
			return true;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}finally{
			connection.disconnect();
			connection = null;
		}
	}
	public boolean testDirectConnect(){
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://www.tumblr.com/");
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY); 
		    connection.setConnectTimeout(10 * 1000);
		    connection.setRequestMethod("GET");
		    connection.connect();
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}finally{
			connection.disconnect();
			connection = null;
		}
	}
	public void applyProxy(Proxy proxy){
		switch(proxy.type())
		{
		case DIRECT:
		{
			clearProxyProperties();
		}
			break;
		case HTTP:
		{
			clearProxyProperties();
			InetSocketAddress sa = (InetSocketAddress) proxy.address();
			System.getProperties().setProperty("http.proxyHost", sa.getHostName());
			System.getProperties().setProperty("http.proxyPort", String.valueOf(sa.getPort()));
		}
			break;
		case SOCKS:{
			clearProxyProperties();
			InetSocketAddress sa = (InetSocketAddress) proxy.address();
			System.getProperties().setProperty("http.socksProxyHost", sa.getHostName());
			System.getProperties().setProperty("http.socksProxyPort", String.valueOf(sa.getPort()));
		}
			break;
		default:
			break;		
		}
	}
	public void clearProxyProperties()
	{
		System.clearProperty("http.proxyHost");
		System.clearProperty("http.proxyPort");
		System.clearProperty("https.proxyHost");
		System.clearProperty("https.proxyPort");
		System.clearProperty("socksProxyHost");
		System.clearProperty("socksProxyPort");
	}
	public Proxy loadProxy() 
	{
		config.proxy = H2DBService.getInstance().loadProxy();
		return config.proxy;
	}
	public void saveProxy(Proxy proxy)
	{
		this.config.proxy = proxy;
	}
	public Proxy getProxy(){
		return this.config.proxy;
	}


	private String basePath;
	public boolean testPath(String path) {
		File file = new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				return true;
			}else{
				return false;
			}
		}else{
			if(file.mkdirs()){
				return true;
			}else{
				return false;
			}
		}
	}
	public void applyPath(String path) {
		this.basePath = path;
	}
	public void savePath(String path){
		applyPath(path);
		H2DBService.getInstance().savePath();
	}
	public String loadPath(){
		String path = H2DBService.getInstance().loadPath();
		this.basePath = path==null ? "./" : path;
		return config.basePath;
	}
	public String getPath(){
		return config.basePath;
	}
	public void saveWindowSettings(int x, int y, int width, int height) {
		config.windowX = x;
		config.windowY = y;
		config.windowW = width;
		config.windowH = height;
		H2DBService.getInstance().updateSettings(config);
	}
	public int getWindowX() {
		return config.windowX;
	}
	public int getWindowY() {
		return config.windowY;
	}
	public int getWindowW() {
		return config.windowW;
	}
	public int getWindowH() {
		return config.windowH;
	}
	public int getHttpClientCount() {
		return config.clientCount;
	}
	public int getConnectionTimeout() {
		return config.connectTimeout;
	}
	public int readTimeout() {
		return config.readTimeout;
	}
}


