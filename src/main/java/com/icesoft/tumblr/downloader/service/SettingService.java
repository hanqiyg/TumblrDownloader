package com.icesoft.tumblr.downloader.service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import com.icesoft.tumblr.downloader.configure.Config;
import com.icesoft.tumblr.downloader.configure.TumblrToken;

public class SettingService {
	private static SettingService instance = new SettingService();
	//private static Logger logger = Logger.getLogger(SettingService.class);
	private Config config;
	
	private SettingService(){
		load();
	}
	private void load(){
		config = H2DBService.getInstance().loadSettings();
		System.out.print(config.toString());
	}
	
	public static SettingService getInstance(){
		return instance;
	}

	public boolean testProxyConnect(Proxy proxy){
		clearProxyProperties();
		System.out.println("testProxyConnect @ " + proxy.toString());
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
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			connection.disconnect();
			connection = null;
		}
	}
	public boolean testDirectConnect(){
		clearProxyProperties();
		System.out.println("testDirectConnect");
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://www.tumblr.com/");
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY); 
		    connection.setConnectTimeout(10 * 1000);
		    connection.setRequestMethod("GET");
		    connection.connect();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			connection.disconnect();
			connection = null;
		}
	}
	public void applyProxy(Proxy proxy){
		config.proxy = proxy;
		if(proxy == null){
			clearProxyProperties();
			return;
		}
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
			System.getProperties().setProperty("socksProxyHost", sa.getHostName());
			System.getProperties().setProperty("socksProxyPort", String.valueOf(sa.getPort()));
		}
			break;
		default:
			break;		
		}
		//printProxyProperties();
	}
	public void printProxyProperties() {
		System.out.println("http.proxyHost" + "->" + System.getProperties().get("http.proxyHost"));
		System.out.println("http.proxyPort" + "->" + System.getProperties().get("http.proxyPort"));
		System.out.println("https.proxyHost" + "->" + System.getProperties().get("https.proxyHost"));
		System.out.println("https.proxyPort" + "->" + System.getProperties().get("https.proxyPort"));
		System.out.println("socksProxyHost" + "->" + System.getProperties().get("socksProxyHost"));
		System.out.println("socksProxyPort" + "->" + System.getProperties().get("socksProxyPort"));
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
		applyProxy(proxy);
		H2DBService.getInstance().updateProxy(proxy);
	}
	public Proxy getProxy(){
		return this.config.proxy;
	}
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
		config.basePath = path;
	}
	public void savePath(String path){
		applyPath(path);
		H2DBService.getInstance().updatePath(path);
	}
	public String loadPath(){
		String path = H2DBService.getInstance().loadPath();
		config.basePath = path==null ? "./" : path;
		return config.basePath;
	}
	public String getPath(){
		return config.basePath;
	}
	public void saveWindowSettings(int x, int y, int width, int height) {
		H2DBService.getInstance().updateWindowSettings(x, y, width, height);
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
	public int getWorkerCount() {
		return config.workerCount;
	}
	public int getReadTimeout() {
		return config.readTimeout;
	}
	public TumblrToken getToken(){
		return config.token;
	}
	public void applyToken(TumblrToken token){
		config.token = token;
	}
	public TumblrToken loadToken(){
		config.token = H2DBService.getInstance().loadToken();
		return config.token;
	}
	public void saveToken(TumblrToken token){
		config.token = token;
		H2DBService.getInstance().updateToken(token);
	}
}


