package com.icesoft.tumblr.downloader;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
	public static String PACKAGE_NAME = "com1.icesoft.tumblr.Settings";
	public static String consumer_key;
	public static String consumer_secret;
	public static String oauth_token;
	public static String oauth_token_secret;	
	
	public static int buffer_size = 1024;
	
	public static String proxy_socket_address="127.0.0.1";
	public static String proxy_socket_port="1080"; 
	
	public static int connect_timeout = 20000;
	public static int read_timeout = 20000;
	
	public static String save_location = "d:/tumblr";
	public static boolean useProxy = true;
	
	public static int x,y,w,h;
	
	public Settings(){
		loadWindowSettings();
		loadKeySettings();
	}
	
	public static void saveKeySettings(){
		try {
			Preferences prefs = Preferences.userRoot().node(PACKAGE_NAME);
			prefs.put("consumer_key", consumer_key);
			prefs.put("consumer_secret", consumer_secret);
			prefs.put("oauth_token", oauth_token);
			prefs.put("oauth_token_secret", oauth_token_secret);
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public static void loadKeySettings(){
		Preferences prefs = Preferences.userRoot().node(PACKAGE_NAME);
		consumer_key = prefs.get("consumer_key", "");
		consumer_secret = prefs.get("consumer_secret", "");
		oauth_token = prefs.get("oauth_token", "");
		oauth_token_secret = prefs.get("oauth_token_secret", "");
	}	
	public static void saveWindowSettings(){
		try {
			Preferences prefs = Preferences.userRoot().node(PACKAGE_NAME);
			prefs.putInt("Window.X", x);
			prefs.putInt("Window.Y", y);
			prefs.putInt("Window.W", w);
			prefs.putInt("Window.H", h);	
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public static void loadWindowSettings(){
		Preferences prefs = Preferences.userRoot().node(PACKAGE_NAME);
		x = prefs.getInt("Window.X", 100);
		y = prefs.getInt("Window.Y", 100);
		w = prefs.getInt("Window.W", 450);
		h = prefs.getInt("Window.H", 300);
	}
}
