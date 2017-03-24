package com.icesoft.tumblr.downloader;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.icesoft.tumblr.downloader.panel.DownloadPanel;
import com.icesoft.tumblr.downloader.panel.LikesPanel;
import com.icesoft.tumblr.downloader.panel.SettingsPanel;
import com.icesoft.tumblr.downloader.service.TumblrServices;


public class MainWindow {
	private JFrame 			frame;
	private Settings 		settings;
	private TumblrServices 	services;
	
	public static boolean refresh = true;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					window.frame.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e){
							refresh = false;
							DownloadManager.getInstance().stopAll();
							Settings.saveWindowSettings();
							System.out.println("关闭主窗口退出！");
							System.exit(0);
						}
					}); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public MainWindow() {
		initialize();
	}
	private void initialize() {
		settings = new Settings();
		//services = new TumblrServices();
		frame = new JFrame();
		frame.setBounds(Settings.x, Settings.y, Settings.w, Settings.h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LikesPanel likesPanel = new LikesPanel(settings,services);
		SettingsPanel settingsPanel = new SettingsPanel(settings,services);
		DownloadPanel downloadPanel = new DownloadPanel(settings,services);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add("Likes", likesPanel);
		tabbedPane.add("Settings", settingsPanel);
		tabbedPane.add("Download", downloadPanel);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane);
		
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("socksProxyHost",Settings.proxy_socket_address	);
		systemProperties.setProperty("socksProxyPort",Settings.proxy_socket_port	);
		
		 //Enable header wire + context logging - Best for Debugging
/*	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
	    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");*/
		
		//Enable full wire(header and content) + context logging
/*	    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
	    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
	    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
	    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");*/
		//httpclient Enable just context logging
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
	}
}
