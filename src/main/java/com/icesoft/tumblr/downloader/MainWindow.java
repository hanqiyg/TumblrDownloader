package com.icesoft.tumblr.downloader;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.PropertyConfigurator;

import com.icesoft.tumblr.downloader.configure.Settings;
import com.icesoft.tumblr.downloader.monitor.UIMonitor;
import com.icesoft.tumblr.downloader.panel.DownloadPanel;
import com.icesoft.tumblr.downloader.panel.LikesPanel;
import com.icesoft.tumblr.downloader.panel.ResourceStatusPanel;
import com.icesoft.tumblr.downloader.panel.SettingsPanel;


public class MainWindow {
	static {
		Properties pro = new Properties();
		pro.put("log4j.rootLogger", "debug,stdout,R,A");

		pro.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
		pro.put("log4j.appender.stdout.Target","System.out");
		pro.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.stdout.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n");

		pro.put("log4j.appender.R", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.R.File", Settings.getInstance().getSaveLocation() + File.separator + "logs" + File.separator + "Info.log");
		pro.put("log4j.appender.R.MaxFileSize", "10000KB");
		pro.put("log4j.appender.R.MaxBackupIndex", "20");
		pro.put("log4j.appender.R.Threshold", "INFO");
		pro.put("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.R.layout.ConversionPattern", "%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");

		pro.put("log4j.appender.A", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.A.File", Settings.getInstance().getSaveLocation() + File.separator + "logs" + File.separator + "Error.log");
		pro.put("log4j.appender.A.MaxFileSize", "10000KB");
		pro.put("log4j.appender.A.MaxBackupIndex", "20");
		pro.put("log4j.appender.A.Threshold", "ERROR");
		pro.put("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.A.layout.ConversionPattern", "%n[%d{HH:mm:ss}] [%p] %m");

		PropertyConfigurator.configure(pro);
	}
	//private static Logger logger = Logger.getLogger(MainWindow.class);  
	private JFrame frame;	
	
	private LikesPanel likesPanel;
	private SettingsPanel settingsPanel;
	private DownloadPanel downloadPanel;
	private ResourceStatusPanel resourceStatusPanel;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					window.frame.addWindowListener(new WindowAdapter()
					{
						public void windowClosing(WindowEvent e){
							ExitWindow exit = new ExitWindow();
							exit.setVisible(false);  
							exit.setModal(true);  
							exit.setAlwaysOnTop(false);  
							exit.setVisible(true);
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
		//services = new TumblrServices();
		frame = new JFrame();		
		frame.setBounds(
					Settings.getInstance().getWindowSetting().getX(),
					Settings.getInstance().getWindowSetting().getY(),
					Settings.getInstance().getWindowSetting().getW(),
					Settings.getInstance().getWindowSetting().getH()
				);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		likesPanel = new LikesPanel();
		settingsPanel = new SettingsPanel();
		downloadPanel = new DownloadPanel();
		resourceStatusPanel = new ResourceStatusPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add("Likes", likesPanel);
		tabbedPane.add("Settings", settingsPanel);
		tabbedPane.add("Download", downloadPanel);
		tabbedPane.add("ResourceStatus", resourceStatusPanel);
		
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frame.getContentPane().add(tabbedPane);
		
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("socksProxyHost",Settings.getInstance().getProxySettings().getHost());
		systemProperties.setProperty("socksProxyPort",String.valueOf(Settings.getInstance().getProxySettings().getPort()));
		
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

		UIMonitor.getInstance().addUpdatable(likesPanel);
		UIMonitor.getInstance().addUpdatable(downloadPanel);
		UIMonitor.getInstance().addUpdatable(resourceStatusPanel);
		UIMonitor.getInstance().turnOn();
	}
}
